package com.edusoho.kuozhi.imserver;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
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
import com.edusoho.kuozhi.imserver.error.MessageSaveFailException;
import com.edusoho.kuozhi.imserver.listener.IChannelReceiveListener;
import com.edusoho.kuozhi.imserver.listener.IConnectManagerListener;
import com.edusoho.kuozhi.imserver.listener.IHeartStatusListener;
import com.edusoho.kuozhi.imserver.service.IConnectionManager;
import com.edusoho.kuozhi.imserver.service.IHeartManager;
import com.edusoho.kuozhi.imserver.service.IMsgManager;
import com.edusoho.kuozhi.imserver.service.Impl.ConnectionManager;
import com.edusoho.kuozhi.imserver.service.Impl.HeartManagerImpl;
import com.edusoho.kuozhi.imserver.service.Impl.MsgManager;
import com.edusoho.kuozhi.imserver.ui.entity.PushUtil;
import com.edusoho.kuozhi.imserver.util.ConvDbHelper;
import com.edusoho.kuozhi.imserver.util.IMConnectStatus;
import com.edusoho.kuozhi.imserver.util.MsgDbHelper;
import com.edusoho.kuozhi.imserver.util.SystemUtil;

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
    private static String[] PUSH_TYPE = {
            Destination.ARTICLE,
            Destination.COURSE,
            Destination.CLASSROOM,
            Destination.GLOBAL
    };

    private int flag;

    private String[] pingCmd = {
            "cmd", "ping"
    };

    private String[] connectCmd = {
            "cmd", "connect",
            "token", ""
    };

    private String[] offlineMsgCmd = {
            "cmd", "offlineMsg",
            "lastMsgNo", ""
    };

    private MsgDbHelper mMsgDbHelper;
    private ConvDbHelper mConvDbHelper;
    private Context mContext;
    private String mClientName;
    private int mClientId;
    private List<String> mHostList;
    private List<String> mIgnoreNosList;

    private IConnectionManager mIConnectionManager;
    private IHeartManager mIHeartManager;
    private IMsgManager mIMsgManager;

    public ImServer(Context context) {
        this.mContext = context;
        this.flag = CONNECT_NONE;
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
                        break;
                    case IConnectManagerListener.CLOSE:
                    case IConnectManagerListener.END:
                        mIConnectionManager.switchConnect();
                        break;
                    case IConnectManagerListener.ERROR:
                        flag = CONNECT_ERROR;
                        pause();
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
        new Handler(Looper.getMainLooper()).postAtTime(new Runnable() {
            @Override
            public void run() {
                if (isCancel()) {
                    Log.d(TAG, "reConnect");
                    start();
                }
            }
        }, SystemClock.uptimeMillis() + 5000);
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

    public void initWithHost(int clientId, String clientName, List<String> host, List<String> ignoreNosList) {
        this.mIgnoreNosList = ignoreNosList;
        this.mClientName = clientName;
        this.mHostList = host;
        this.mClientId = clientId;

        this.mMsgDbHelper = new MsgDbHelper(mContext);
        this.mConvDbHelper = new ConvDbHelper(mContext);
    }

    public boolean isConnected() {
        return mIConnectionManager != null && mIConnectionManager.isConnected();
    }

    public int getStatus() {
        if (mIConnectionManager == null || !mIConnectionManager.isConnected()) {
            return IMConnectStatus.ERROR;
        }
        return mIConnectionManager.getStatus();
    }

    public boolean isReady() {
        if (TextUtils.isEmpty(mClientName)) {
            return false;
        }

        if (mHostList == null || mHostList.isEmpty()
                || mIgnoreNosList == null || mIgnoreNosList.isEmpty()) {
            return false;
        }

        return true;
    }

    public void pause() {
        if (this.mIHeartManager != null) {
            this.mIHeartManager.stop();
        }

        if (this.mIConnectionManager != null) {
            this.mIConnectionManager.stop();
        }
        this.mIConnectionManager = null;
    }

    public void stop() {
        pause();
        sendConnectStatusBroadcast(IConnectManagerListener.CLOSE);
        this.mMsgDbHelper = null;
        this.mConvDbHelper = null;
    }

    public boolean isCancel() {
        if (mIConnectionManager == null) {
            return true;
        }
        int status = mIConnectionManager.getStatus();
        return status == IConnectManagerListener.ERROR;
    }

    public void start() {
        pause();
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
        send(new String[]{
                "cmd", "ack",
                "msgNo", msgNo
        });
    }

    public void requestConnect() {
        send(connectCmd);
    }

    public void sendMessage(SendEntity sendEntity) {
        send(new String[]{
                "cmd", "send",
                "toId", sendEntity.getToId(),
                "convNo", sendEntity.getConvNo(),
                "msg", sendEntity.getMsg()
        });
    }

    public void onReceiveOfflineMsg(ArrayList<MessageEntity> messageEntities) {
        ArrayList<MessageEntity> entities = new ArrayList<>();
        for (MessageEntity messageEntity : messageEntities) {
            try {
                messageEntity = handleReceiveMessage(messageEntity);
                if (messageEntity != null) {
                    entities.add(messageEntity);
                }
            } catch (MessageSaveFailException e) {
            }
        }
        Intent intent = new Intent("com.edusoho.kuozhi.push.action.IM_MESSAGE");
        intent.putExtra(IMBroadcastReceiver.ACTION, IMBroadcastReceiver.OFFLINE_MSG);
        intent.putExtra("message", entities);
        mContext.sendBroadcast(intent);
    }

    private boolean filterMessageBody(MessageBody messageBody) {
        if (messageBody == null) {
            return true;
        }
        if (PushUtil.ChatMsgType.PUSH.equals(messageBody.getType())) {
            Source source = messageBody.getSource();
            if (Destination.CLASSROOM.equals(source.getType()) || Destination.COURSE.equals(source.getType())) {
                return true;
            }
        }

        return false;
    }

    private MessageEntity handleReceiveMessage(MessageEntity messageEntity) throws MessageSaveFailException {
        if (getMsgDbHelper().hasMessageByNo(messageEntity.getMsgNo())) {
            Log.d("MessageCommand", "hasMessageByNo");
            return null;
        }

        if ("message".equals(messageEntity.getCmd()) || "offlineMsg".equals(messageEntity.getCmd())) {
            MessageBody messageBody = new MessageBody(messageEntity);
            if (messageBody == null) {
                return null;
            }

            if (filterMessageBody(messageBody)) {
                return messageEntity;
            }

            messageEntity.setConvNo(getMessageConvNo(messageBody));
            int messageStatus = MessageEntity.StatusType.NONE;
            if (PushUtil.ChatMsgType.TEXT.equals(messageBody.getType())
                    || PushUtil.ChatMsgType.MULTI.equals(messageBody.getType())) {
                messageStatus = MessageEntity.StatusType.SUCCESS;
            }
            messageEntity.setStatus(messageStatus);
            messageEntity = saveMessageEntityToDb(messageEntity);
            ConvEntity convEntity = getConvEntityFromMessage(messageBody);
            if (convEntity == null) {
                convEntity = createConv(messageBody);
                convEntity.setUnRead(convEntity.getUnRead() + 1);
                convEntity.setUid(mClientId);
                mConvDbHelper.save(convEntity);
            } else {
                updateConvEntity(convEntity, messageEntity);
            }
        }

        return messageEntity;
    }

    private ConvEntity getConvEntityFromMessage(MessageBody messageBody) {
        if (TextUtils.isEmpty(messageBody.getConvNo())) {
            return mConvDbHelper.getConvByTypeAndId(messageBody.getSource().getType(), messageBody.getSource().getId());
        }

        return mConvDbHelper.getConvByConvNo(messageBody.getConvNo());
    }

    private MessageEntity saveMessageEntityToDb(MessageEntity messageEntity) throws MessageSaveFailException {
        long resultId = mMsgDbHelper.save(messageEntity);
        if (resultId != 0) {
            messageEntity = mMsgDbHelper.getMessageByMsgNo(messageEntity.getMsgNo());
            if (messageEntity == null) {
                throw new MessageSaveFailException();
            }
            return messageEntity;
        }
        throw new MessageSaveFailException();
    }

    public void onReceiveMessage(MessageEntity messageEntity) {
        try {
            messageEntity = handleReceiveMessage(messageEntity);
            if (messageEntity == null) {
                return;
            }
            Intent intent = new Intent("com.edusoho.kuozhi.push.action.IM_MESSAGE");
            intent.putExtra(IMBroadcastReceiver.ACTION, IMBroadcastReceiver.RECEIVER);
            intent.putExtra("message", messageEntity);
                mContext.sendBroadcast(intent);
        } catch (MessageSaveFailException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private String getMessageConvNo(MessageBody messageBody) {
        if ("push".equals(messageBody.getType())) {
            return messageBody.getSource().getType();
        }
        return messageBody.getConvNo();
    }

    private void updateConvEntity(ConvEntity convEntity, MessageEntity messageEntity) {
        convEntity.setUnRead(convEntity.getUnRead() + 1);
        convEntity.setLaterMsg(messageEntity.getMsg());
        convEntity.setUpdatedTime(messageEntity.getTime() * 1000L);
        Role role = IMClient.getClient().getRoleManager().getRole(convEntity.getType(), convEntity.getTargetId());
        if (role.getRid() != 0) {
            convEntity.setTargetName(role.getNickname());
            convEntity.setAvatar(role.getAvatar());
        }

        if (checkPushConvEntityCanUpdate(convEntity.getConvNo(), messageEntity.getConvNo())) {
            mConvDbHelper.update(convEntity);
            return;
        }
        mConvDbHelper.updateByConvNo(convEntity);
    }

    /*
        检查convNo是否是push类型 push类型的convNo和已增加convNo统一为一个
     */
    private boolean checkPushConvEntityCanUpdate(String convNo, String messageConvNo) {
        if (TextUtils.isEmpty(messageConvNo) || messageConvNo.equals(convNo)) {
            return false;
        }

        return SystemUtil.searchInArray(PUSH_TYPE, convNo) != -1;
    }

    private ConvEntity getConvFromPush(MessageBody messageBody) {
        ConvEntity convEntity = new ConvEntity();

        convEntity.setTargetName(getPushTypeName(messageBody.getSource().getType()));
        convEntity.setLaterMsg(messageBody.toJson());
        convEntity.setConvNo(messageBody.getSource().getType());
        convEntity.setCreatedTime(messageBody.getCreatedTime());
        convEntity.setType(messageBody.getSource().getType());

        convEntity.setTargetId(messageBody.getSource().getId());
        convEntity.setUpdatedTime(messageBody.getCreatedTime());
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
        convEntity.setUpdatedTime(messageBody.getCreatedTime());

        Source source = messageBody.getSource();
        Destination destination = messageBody.getDestination();
        String destionationType = destination.getType();
        if (Destination.USER.equals(destionationType)) {
            convEntity.setType(Destination.USER);
            convEntity.setTargetName(source.getNickname());
            convEntity.setTargetId(source.getId());
        } else {
            convEntity.setTargetName(destination.getNickname());
            convEntity.setType(destination.getType());
            convEntity.setTargetId(destination.getId());
        }

        return convEntity;
    }

    private ConvEntity createConv(MessageBody messageBody) {
        ConvEntity convEntity = null;
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
