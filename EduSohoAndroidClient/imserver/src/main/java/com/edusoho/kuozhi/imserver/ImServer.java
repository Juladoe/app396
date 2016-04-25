package com.edusoho.kuozhi.imserver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.edusoho.kuozhi.imserver.command.CommandFactory;
import com.edusoho.kuozhi.imserver.listener.IChannelReceiveListener;
import com.edusoho.kuozhi.imserver.listener.IConnectStatusListener;
import com.edusoho.kuozhi.imserver.listener.IHeartStatusListener;
import com.edusoho.kuozhi.imserver.service.IConnectionManager;
import com.edusoho.kuozhi.imserver.service.IHeartManager;
import com.edusoho.kuozhi.imserver.service.IMsgManager;
import com.edusoho.kuozhi.imserver.service.Impl.ConnectionManager;
import com.edusoho.kuozhi.imserver.service.Impl.HeartManagerImpl;
import com.edusoho.kuozhi.imserver.service.Impl.MsgManager;
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

    private String[] addCmd = {
            "cmd" , "ping",
            "toId"
    };

    private Context mContext;
    private String mClientName;
    private List<String> mHostList;

    private IConnectionManager mIConnectionManager;
    private IHeartManager mIHeartManager;
    private IMsgManager mIMsgManager;

    public ImServer(Context context) {
        this.mContext = context;
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

        this.mIConnectionManager.addIConnectStatusListener(new IConnectStatusListener() {
            @Override
            public void onStatusChange(int status, String error) {
                Log.d(TAG, "IConnectStatusListener status:" + status);
                switch (status) {
                    case IConnectStatusListener.OPEN:
                        mIHeartManager.start();
                        break;
                    case IConnectStatusListener.CLOSE:
                    case IConnectStatusListener.END:
                    case IConnectStatusListener.ERROR:
                        mIHeartManager.stop();
                }
            }
        });

        this.mIConnectionManager.accept();
    }

    public void initWithHost(String clientName, List<String> host) {
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
        initConnectManager(mClientName, mHostList);
    }

    public IHeartManager getHeartManager() {
        return mIHeartManager;
    }

    public void sendMessage(String convNo, String msg) {
        send(new String[] {
                "cmd" , "send",
                "toId" , "all",
                "convNo" , convNo,
                "msg", msg
        });
    }

    public void onReceiveMessage(String msg) {
        Intent intent = new Intent("com.edusoho.kuozhi.push.action.IM_MESSAGE");
        intent.putExtra("message", msg);
        mContext.sendBroadcast(intent);
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
