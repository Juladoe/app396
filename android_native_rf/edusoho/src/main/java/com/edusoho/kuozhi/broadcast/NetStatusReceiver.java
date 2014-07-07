package com.edusoho.kuozhi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.edusoho.kuozhi.EdusohoApp;

/**
 * 网络切换广播接受
 * Created by howzhi on 14-6-10.
 */
public class NetStatusReceiver extends BroadcastReceiver {
    //android 中网络变化时所发的Intent的名字
    private static final String ACTION="android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!intent.getAction().equals(ACTION)) {
            return;
        }
        ConnectivityManager connManager = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (EdusohoApp.app != null) {
                EdusohoApp.app.checkToken();
            }
        }
    }
}
