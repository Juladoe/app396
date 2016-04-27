package com.edusoho.kuozhi.imserver;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import com.edusoho.kuozhi.imserver.broadcast.NetWorkStatusBroadcastReceiver;
import com.edusoho.kuozhi.imserver.util.NetTypeConst;

import java.util.Arrays;
import java.util.List;

/**
 * Created by su on 2016/3/18.
 */
public class ImService extends Service {

    public static final String HOST = "host";
    public static final String IGNORE_NOS = "ignoreNos";
    public static final String CLIENT_NAME = "clientName";
    public static final String ACTION = "action";
    public static final int ACTION_INIT = 0011;

    private NetWorkStatusBroadcastReceiver mReceiver;
    private ImServer mImServer;
    private ImBinder mImBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(getClass().getSimpleName(), "onCreate");
        mImServer = new ImServer(getBaseContext());
        mImBinder = new ImBinder();

        registNetWorkStatusBroadcastReceiver();
    }

    private void registNetWorkStatusBroadcastReceiver() {
        mReceiver = new NetWorkStatusBroadcastReceiver(getNetWorkStatusCallback());
        registerReceiver(mReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected NetWorkStatusBroadcastReceiver.NetWorkStatusCallback getNetWorkStatusCallback() {

        return new NetWorkStatusBroadcastReceiver.NetWorkStatusCallback() {
            @Override
            public void onStatusChange(int netType, boolean isConnected) {
                if (! mImServer.isReady()) {
                    return;
                }
                if (isConnected && !mImServer.isConnected()) {
                    mImServer.start();
                }
                switch (netType) {
                    case NetTypeConst.WIFI:
                        mImServer.getHeartManager().switchPingType(NetTypeConst.WIFI);
                        break;
                    case NetTypeConst.NONE:
                        mImServer.stop();
                        break;
                    case NetTypeConst.WCDMA:
                        mImServer.getHeartManager().switchPingType(NetTypeConst.WCDMA);
                        break;
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(getClass().getSimpleName(), "onStartCommand" + intent);
        if (intent == null) {
            return 0;
        }
        int action = intent.getIntExtra(ACTION, 0);

        if (action == ACTION_INIT) {
            Log.d(getClass().getSimpleName(), "init");
            List<String> hostList = intent.getStringArrayListExtra(HOST);
            List<String> ignoreNosList = intent.getStringArrayListExtra(IGNORE_NOS);
            String clientName = intent.getStringExtra(CLIENT_NAME);
            initServerHost(clientName, hostList, ignoreNosList);
            return super.onStartCommand(intent, flags, startId);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void initServerHost(String clientName, List<String> hostList, List<String> ignoreNosList) {
        if (hostList == null || hostList.isEmpty()) {
            Log.d(getClass().getSimpleName(), "no server host");
            return;
        }

        Log.d(getClass().getSimpleName(), Arrays.toString(hostList.toArray()));
        mImServer.initWithHost(clientName, hostList, ignoreNosList);
        mImServer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        Log.d(getClass().getSimpleName(), "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(getClass().getSimpleName(), "onBind");
        return mImBinder;
    }

    public class ImBinder extends IImServerAidlInterface.Stub
    {
        public void send(String convNo, String message) {
            mImServer.sendMessage(convNo, message);
        }

        public void joinConversation(String clientId, String nickname, String convNo) {

        }
    }
}
