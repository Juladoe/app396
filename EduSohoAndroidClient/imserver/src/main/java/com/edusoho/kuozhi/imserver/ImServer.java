package com.edusoho.kuozhi.imserver;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.edusoho.kuozhi.imserver.broadcast.IMBroadcastReceiver;
import com.edusoho.kuozhi.imserver.command.CommandFactory;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.entity.message.Source;
import com.edusoho.kuozhi.imserver.listener.IChannelReceiveListener;
import com.edusoho.kuozhi.imserver.listener.IConnectManagerListener;
import com.edusoho.kuozhi.imserver.listener.IHeartStatusListener;
import com.edusoho.kuozhi.imserver.service.IConnectionManager;
import com.edusoho.kuozhi.imserver.service.IHeartManager;
import com.edusoho.kuozhi.imserver.service.IMsgManager;
import com.edusoho.kuozhi.imserver.service.Impl.ConnectionManager;
import com.edusoho.kuozhi.imserver.service.Impl.HeartManagerImpl;
import com.edusoho.kuozhi.imserver.service.Impl.MsgManager;
import com.edusoho.kuozhi.imserver.util.ConvDbHelper;
import com.edusoho.kuozhi.imserver.util.MsgDbHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by su on 2016/3/17.
 */
public class ImServer {

    private static final String TAG = "ImServer";
    private static final int CONNECT_NONE = 0001;
    private static final int CONNECT_WAIT = 0002;
    private static final int CONNECT_OPEN = 0003;
    private static final int CONNECT_ERROR = 0004;

    private int flag;

    private String[] pingCmd = {
            "cmd" , "ping"
    };

    private String[] connectCmd = {
            "cmd", "connect",
            "token", ""
    };

    private String[] offlineMsgCmd = {
            "cmd" , "offlineMsg",
            "lastMsgNo", ""
    };

    private MsgDbHelper mMsgDbHelper;
    private ConvDbHelper mConvDbHelper;
    private Context mContext;
    private String mClientName;
    private List<String> mHostList;
    private List<String> mIgnoreNosList;

    private IConnectionManager mIConnectionManager;
    private IHeartManager mIHeartManager;
    private IMsgManager mIMsgManager;

    public ImServer(Context context) {
        this.mContext = context;
        this.flag = CONNECT_NONE;
        this.mMsgDbHelper = new MsgDbHelper(context);
        this.mConvDbHelper = new ConvDbHelper(context);
        initHeartManager();
        initMsgManager();
    }

    private void initMsgManager() {
        this.mIMsgManager = new MsgManager();
    }

    private void initHeartManager() {
        this.mIHeartManager = new HeartManagerImpl();
        this.mIHeartManager.addHeartStatusListener(new IHeartStatusListener() {
            @Override
            public void onPing() {
                Log.d(TAG, "onPing");
                ping();
            }

            @Override
            public void onPong(int status) {
                Log.d(TAG, "status:" + status);
                if (status == IHeartStatusListener.TIMEOUT) {
                    mIConnectionManager.switchConnect();
                }
            }
        });
    }

    private void initConnectManager(String clientName, List<String> host) {
        this.mIConnectionManager = new ConnectionManager(clientName);
        this.mIConnectionManager.setServerHostList(host);

        this.mIConnectionManager.addIChannelReceiveListener(new IChannelReceiveListener() {
            @Override
            public void onReceiver(String content) {
                handleCmd(content);
            }
        });

        this.mIConnectionManager.addIConnectStatusListener(new IConnectManagerListener() {
            @Override
            public void onStatusChange(int status, String error) {
                Log.d(TAG, "IConnectManagerListener status:" + status);
                sendConnectStatusBroadcast(status);
                switch (status) {
                    case IConnectManagerListener.OPEN:
                        flag = CONNECT_OPEN;
                        mIHeartManager.start();
                        break;
                    case IConnectManagerListener.CLOSE:
                    case IConnectManagerListener.END:
                    case IConnectManagerListener.ERROR:
                        flag = CONNECT_ERROR;
                        reConnect();
                }
            }
        });

        this.mIConnectionManager.accept();
    }

    private synchronized void reConnect() {
        if (flag == CONNECT_WAIT || flag == CONNECT_OPEN) {
            return;
        }
        flag = CONNECT_WAIT;
        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "reConnect");
                start();
            }
        }, SystemClock.uptimeMillis() + 3000);
    }

    public void requestOfflineMsg() {
        offlineMsgCmd[3] = "";
        offlineMsgCmd[3] = mMsgDbHelper.getLaterNo();
        send(offlineMsgCmd);
        Log.d(TAG, "requestOfflineMsg:" + offlineMsgCmd[3]);
    }

    private void sendConnectStatusBroadcast(int status) {
        Intent intent = new Intent("com.edusoho.kuozhi.push.action.IM_MESSAGE");
        intent.putExtra(IMBroadcastReceiver.ACTION, IMBroadcastReceiver.STATUS_CHANGE);
        intent.putExtra("status", status);
        intent.putExtra("isConnected", isConnected());
        mContext.sendBroadcast(intent);
    }

    public void initWithHost(String clientName, List<String> host, List<String> ignoreNosList) {
        this.mIgnoreNosList = ignoreNosList;
        this.mClientName = clientName;
        this.mHostList = host;
    }

    public boolean isConnected() {
        return mIConnectionManager != null && mIConnectionManager.isConnected();
    }

    public boolean isReady() {
        return mIHeartManager != null && mIConnectionManager != null;
    }

    private void cancel() {
        if (this.mIHeartManager != null) {
            this.mIHeartManager.stop();
        }

        if (this.mIConnectionManager != null) {
            this.mIConnectionManager.stop();
        }
        this.mIConnectionManager = null;
    }

    public void stop() {
        cancel();
        sendConnectStatusBroadcast(IConnectManagerListener.CLOSE);
    }

    public boolean isCancel() {
        if (!isConnected()) {
            return true;
        }
        int status = mIConnectionManager.getStatus();
        return status == IConnectManagerListener.ERROR
                || status == IConnectManagerListener.CLOSE
                || status == IConnectManagerListener.END;
    }

    public void start() {
        cancel();
        if (TextUtils.isEmpty(mClientName) || mHostList == null || mHostList.isEmpty()) {
            return;
        }
        initConnectManager(mClientName, mHostList);
    }

    public IHeartManager getHeartManager() {
        return mIHeartManager;
    }

    public void ack(String msgNo) {
        Log.d(TAG, "ack:" + msgNo);
        send(new String[] {
                "cmd", "ack",
                "msgNo", msgNo
        });
    }

    public void requestConnect() {
        send(connectCmd);
    }

    public void sendMessage(SendEntity sendEntity) {
        send(new String[] {
                "cmd" , "send",
                "toId" , sendEntity.getToId(),
                "convNo" , sendEntity.getConvNo(),
                "msg", sendEntity.getMsg()
        });
    }

    public void onReceiveOfflineMsg(ArrayList<MessageEntity> messageEntities) {
        for (MessageEntity messageEntity : messageEntities) {
            if (getMsgDbHelper().hasMessageByNo(messageEntity.getMsgNo())) {
                Log.d("MessageCommand", "hasMessageByNo");
                return;
            }
            mMsgDbHelper.save(messageEntity);
            ConvEntity convEntity = mConvDbHelper.getConv(messageEntity.getMsgNo());
            updateConv(convEntity, messageEntity);
        }
        Intent intent = new Intent("com.edusoho.kuozhi.push.action.IM_MESSAGE");
        intent.putExtra(IMBroadcastReceiver.ACTION, IMBroadcastReceiver.OFFLINE_MSG);
        intent.putExtra("message", messageEntities);
        mContext.sendBroadcast(intent);
    }

    public void onReceiveMessage(MessageEntity messageEntity) {
        if (getMsgDbHelper().hasMessageByNo(messageEntity.getMsgNo())) {
            Log.d("MessageCommand", "hasMessageByNo");
            return;
        }

        if ("message".equals(messageEntity.getCmd())) {
            mMsgDbHelper.save(messageEntity);
            ConvEntity convEntity = mConvDbHelper.getConv(messageEntity.getConvNo());
            updateConv(convEntity, messageEntity);
        }

        Intent intent = new Intent("com.edusoho.kuozhi.push.action.IM_MESSAGE");
        intent.putExtra(IMBroadcastReceiver.ACTION, IMBroadcastReceiver.RECEIVER);
        intent.putExtra("message", messageEntity);
        mContext.sendBroadcast(intent);
    }

    private void updateConv(ConvEntity convEntity, MessageEntity messageEntity) {
        if (convEntity == null) {
            convEntity = createConv(messageEntity);
            mConvDbHelper.save(convEntity);
            return;
        }
        convEntity.setUnRead(convEntity.getUnRead() + 1);
        convEntity.setLaterMsg(messageEntity.getMsg());
        convEntity.setUpdatedTime(messageEntity.getTime() * 1000);

        mConvDbHelper.update(convEntity);
    }

    private ConvEntity getConvFromPush(MessageBody messageBody) {
        ConvEntity convEntity = new ConvEntity();

        convEntity.setTargetName(getPushTypeName(messageBody.getSource().getType()));
        convEntity.setLaterMsg(messageBody.toJson());
        convEntity.setConvNo(messageBody.getConvNo());
        convEntity.setCreatedTime(messageBody.getCreatedTime());
        convEntity.setType(messageBody.getSource().getType());

        convEntity.setTargetId(messageBody.getSource().getId());
        convEntity.setUpdatedTime(0);
        return convEntity;
    }

    private String getPushTypeName(String type) {
        switch (type) {
            case "news":
                return "资讯";
        }

        return "";
    }

    private ConvEntity getConvFromMessage(MessageBody messageBody) {
        ConvEntity convEntity = new ConvEntity();

        convEntity.setConvNo(messageBody.getConvNo());
        convEntity.setLaterMsg(messageBody.toJson());
        convEntity.setCreatedTime(messageBody.getCreatedTime());
        convEntity.setUpdatedTime(0);

        Source source = messageBody.getSource();
        Destination destination = messageBody.getDestination();
        String destionationType = destination.getType();
        if (Destination.USER.equals(destionationType)) {
            convEntity.setType(Destination.USER);
            convEntity.setTargetName(source.getNickname());
            convEntity.setTargetId(source.getId());
            convEntity.setUid(destination.getId());
        } else {
            convEntity.setTargetName(destination.getNickname());
            convEntity.setType(destination.getType());
            convEntity.setTargetId(destination.getId());
        }

        return convEntity;
    }

    private ConvEntity createConv(MessageEntity messageEntity) {
        ConvEntity convEntity = null;
        MessageBody messageBody = new MessageBody(messageEntity);
        if ("push".equals(messageBody.getType())) {
            convEntity = getConvFromPush(messageBody);
        } else {
            convEntity = getConvFromMessage(messageBody);
        }

        Role role = IMClient.getClient().getRoleManager().getRole(convEntity.getType(), convEntity.getTargetId());
        if (role.getRid() != 0) {
            convEntity.setTargetName(role.getNickname());
            convEntity.setAvatar(role.getAvatar());
        }
        return convEntity;
    }

    public MsgDbHelper getMsgDbHelper() {
        return mMsgDbHelper;
    }

    private void ping() {
        send(pingCmd);
    }

    private void send(String[] params) {
        try {
            JSONObject msgObj = new JSONObject();
            for (int i = 0; i < params.length; i = i + 2) {
                msgObj.put(params[i], params[i + 1]);
            }
            this.mIConnectionManager.send(msgObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleCmd(String cmdStr) {
        try {
            JSONObject jsonObject = new JSONObject(cmdStr);
            String cmd = jsonObject.optString("cmd");
            CommandFactory.getInstance().create(this, cmd).invoke(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
