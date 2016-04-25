package com.edusoho.kuozhi.imserver.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.ImService;

/**
 * Created by su on 2016/3/22.
 */
public class IMBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "onReceive");
        final String message = intent.getStringExtra("message");
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                IMClient.getClient().invokeReceiver(message);
            }
        });
    }
}
