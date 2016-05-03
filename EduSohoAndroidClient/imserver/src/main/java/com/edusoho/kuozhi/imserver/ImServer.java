package com.edusoho.kuozhi.imserver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.edusoho.kuozhi.imserver.broadcast.IMBroadcastReceiver;
import com.edusoho.kuozhi.imserver.command.CommandFactory;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.listener.IChannelReceiveListener;
import com.edusoho.kuozhi.imserver.listener.IConnectManagerListener;
import com.edusoho.kuozhi.imserver.listener.IHeartStatusListener;
import com.edusoho.kuozhi.imserver.service.IConnectionManager;
import com.edusoho.kuozhi.imserver.service.IHeartManager;
import com.edusoho.kuozhi.imserver.service.IMsgManager;
import com.edusoho.kuozhi.imserver.service.Impl.ConnectionManager;
import com.edusoho.kuozhi.imserver.service.Impl.HeartManagerImpl;
import com.edusoho.kuozhi.imserver.service.Impl.MsgManager;
import com.edusoho.kuozhi.imserver.util.MsgDbHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

/**
 * Created by su on 2016/3/17.
 */
public class ImServer {

    private static final String TAG = "ImServer";

    private String[] pingCmd = {
            "cmd" , "ping"
    };

    private MsgDbHelper mMsgDbHelper;
    private Context mContext;
    private String mClientName;
    private List<String> mHostList;
    private List<String> mIgnoreNosList;

    private IConnectionManager mIConnectionManager;
    private IHeartManager mIHeartManager;
    private IMsgManager mIMsgManager;

    public ImServer(Context context) {
        this.mContext = context;
        this.mMsgDbHelper = new MsgDbHelper(context);
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
                    mIConnectionManager.accept();
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
                        mIHeartManager.start();
                        break;
                    case IConnectManagerListener.CLOSE:
                    case IConnectManagerListener.END:
                    case IConnectManagerListener.ERROR:
                        mIHeartManager.stop();
                }
            }
        });

        this.mIConnectionManager.accept();
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
        return mIConnectionManager.isConnected();
    }

    public boolean isReady() {
        return mIHeartManager != null && mIConnectionManager != null;
    }

    public void stop() {
        if (this.mIHeartManager != null) {
            this.mIHeartManager.stop();
        }
        if (this.mIConnectionManager != null) {
            this.mIConnectionManager.close();
        }
    }

    public void start() {
        stop();
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

    public void sendMessage(SendEntity sendEntity) {
        send(new String[] {
                "cmd" , "send",
                "toId" , sendEntity.getToId(),
                "convNo" , sendEntity.getConvNo(),
                "msg", sendEntity.getMsg(),
                "extr", sendEntity.getMsg()
        });
    }

    public void onReceiveMessage(MessageEntity messageEntity) {
        Intent intent = new Intent("com.edusoho.kuozhi.push.action.IM_MESSAGE");
        intent.putExtra(IMBroadcastReceiver.ACTION, IMBroadcastReceiver.RECEIVER);
        intent.putExtra("message", messageEntity);
        mContext.sendBroadcast(intent);
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
