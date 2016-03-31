package com.edusoho.kuozhi.imserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.edusoho.kuozhi.imserver.listener.ImReceiver;
import java.util.List;

/**
 * Created by su on 2016/3/18.
 */
public class ImService extends Service {

    public static final String HOST = "host";

    private ImServer mImServer;
    private ImBinder mImBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(getClass().getSimpleName(), "onCreate");
        mImServer = new ImServer();
        mImBinder = new ImBinder();
        mImServer.setReceiver(new ImServer.Receiver() {
            @Override
            public void onReceive(String msg) {
                Intent intent = new Intent("com.edusoho.kuozhi.push.action.IM_MESSAGE");
                intent.putExtra("message", msg);
                sendBroadcast(intent);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        List<String> hostList = intent.getStringArrayListExtra(HOST);
        if (hostList != null && ! hostList.isEmpty()) {
            mImServer.clear();
            mImServer.initWithHost(hostList);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getSimpleName(), "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(getClass().getSimpleName(), "onBind");
        return mImBinder;
    }

    public class ImBinder extends IImServerAidlInterface.Stub
    {
        private ImReceiver mImReceiver;

        public void send(String message) {
            mImServer.sendMessage(message);
        }

        public void joinConversation(String clientId, String nickname, String convNo) {
            mImServer.joinConversation(clientId, nickname, convNo);
        }
    }
}
