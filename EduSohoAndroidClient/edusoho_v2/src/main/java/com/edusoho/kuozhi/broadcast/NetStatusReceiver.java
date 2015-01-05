package com.edusoho.kuozhi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.Service.M3U8DownService;
import com.edusoho.kuozhi.util.Const;

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

        if (EdusohoApp.app == null) {
            return;
        }

        ConnectivityManager connManager = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            M3U8DownService m3U8DownService = M3U8DownService.getService();
            int offlineType = EdusohoApp.app.config.offlineType;
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                    && m3U8DownService != null
                    && offlineType == Const.NET_WIFI) {
                Log.d(null, "cancel all donwload task");
                m3U8DownService.cancelAllDownloadTask();
            }
            /*
            if (EdusohoApp.app != null && EdusohoApp.app.loginUser == null) {
                EdusohoApp.app.checkToken();
            }
            */
        }
    }
}
