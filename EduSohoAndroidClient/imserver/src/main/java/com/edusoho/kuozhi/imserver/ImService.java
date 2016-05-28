package com.edusoho.kuozhi.imserver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.edusoho.kuozhi.imserver.broadcast.NetWorkStatusBroadcastReceiver;
import com.edusoho.kuozhi.imserver.util.IMConnectStatus;
import com.edusoho.kuozhi.imserver.util.NetTypeConst;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by su on 2016/3/18.
 */
public class ImService extends Service {

    private static final String TAG = "ImService";

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
        Log.d(TAG, "onCreate");
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
                Log.d(TAG, String.format("onStatusChange netType:%d isConnected:%b", netType, isConnected));
                if (! mImServer.isReady()) {
                    return;
                }
                if (isConnected && mImServer.isCancel()) {
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
        Log.d(TAG, "onStartCommand" + intent);
        if (intent == null) {
            initServerHostFromLater();
            return super.onStartCommand(intent, flags, startId);
        }
        int action = intent.getIntExtra(ACTION, 0);

        if (action == ACTION_INIT) {
            Log.d(TAG, "init");
            List<String> hostList = intent.getStringArrayListExtra(HOST);
            List<String> ignoreNosList = intent.getStringArrayListExtra(IGNORE_NOS);
            String clientName = intent.getStringExtra(CLIENT_NAME);
            if (TextUtils.isEmpty(clientName) || hostList == null || ignoreNosList == null) {
                return super.onStartCommand(intent, flags, startId);
            }
            initServerHost(clientName, hostList, ignoreNosList);
            saveLaterHost(clientName, ignoreNosList, hostList);
            return Service.START_STICKY;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void initServerHostFromLater() {
        SharedPreferences sp = getSharedPreferences("laterHost", Context.MODE_PRIVATE);
        String clientName = sp.getString("clientName", null);
        Set ignoreNosSet = sp.getStringSet("ignoreNosList", null);
        Set hostSet = sp.getStringSet("hostList", null);

        if (TextUtils.isEmpty(clientName) || ignoreNosSet == null || hostSet == null) {
            return;
        }

        Log.d(TAG, "initServerHostFromLater");
        initServerHost(clientName, new ArrayList<String>(ignoreNosSet), new ArrayList<String>(hostSet));
    }

    private void saveLaterHost(String clientName ,List<String> ignoreNosList, List<String> hostList) {
        SharedPreferences.Editor editor = getSharedPreferences("laterHost", Context.MODE_PRIVATE).edit();
        editor.putString("clientName", clientName);
        editor.putStringSet("ignoreNosList", new HashSet<String>(ignoreNosList));
        editor.putStringSet("hostList", new HashSet<String>(hostList));
        editor.commit();
    }

    private void initServerHost(String clientName, List<String> hostList, List<String> ignoreNosList) {
        if (hostList == null || hostList.isEmpty()) {
            Log.d(TAG, "no server host");
            return;
        }

        Log.d(getClass().getSimpleName(), Arrays.toString(hostList.toArray()));
        mImServer.initWithHost(clientName, hostList, ignoreNosList);
        mImServer.start();
    }

    protected void sendWakeUpAlert() {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, null);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        sendWakeUpAlert();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mImBinder;
    }

    public class ImBinder extends IImServerAidlInterface.Stub
    {
        public void requestConnect() {
            mImServer.requestConnect();
        }

        public void requestOfflineMsg() {
            mImServer.requestOfflineMsg();
        }

        public void send(SendEntity sendEntity) {
            mImServer.sendMessage(sendEntity);
        }

        @Override
        public void closeIMServer() throws RemoteException {
            mImServer.stop();
        }

        @Override
        public int getIMStatus() throws RemoteException {
            if (! mImServer.isReady()) {
                return IMConnectStatus.NO_READY;
            }
            return mImServer.isConnected() ? IMConnectStatus.OPEN : IMConnectStatus.CLOSE;
        }

        public void joinConversation(String clientId, String nickname, String convNo) {
            //nothing
        }
    }
}
