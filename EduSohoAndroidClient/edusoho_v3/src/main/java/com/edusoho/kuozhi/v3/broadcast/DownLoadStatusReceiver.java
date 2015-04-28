package com.edusoho.kuozhi.v3.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by howzhi on 14-6-10.
 */
public class DownLoadStatusReceiver extends BroadcastReceiver {

    //所发的Intent的名字
    public static final String ACTION = "android.intent.action.DOWNLOAD_STATUS";

    private StatusCallback mStatusCallback;

    public DownLoadStatusReceiver() {
        super();
    }

    public DownLoadStatusReceiver(StatusCallback callback) {
        this();
        this.mStatusCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DownLoadStatusReceiver", "onReceive ");
        if (mStatusCallback != null) {
            mStatusCallback.invoke(intent);
            return;
        }
        if (ACTION.equals(intent.getAction())) {
        }

    }

    public interface StatusCallback {
        public void invoke(Intent intent);
    }
}
