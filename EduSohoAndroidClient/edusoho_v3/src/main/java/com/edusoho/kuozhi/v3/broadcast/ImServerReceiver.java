package com.edusoho.kuozhi.v3.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by su on 2016/3/22.
 */
public class ImServerReceiver extends BroadcastReceiver {

    private ReceiveCallback mReceiveCallback;

    public ImServerReceiver(){
    }

    public ImServerReceiver(ReceiveCallback receiveCallback)
    {
        this.mReceiveCallback = receiveCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getStringExtra("message");
        Log.d("onReceive:", "" + msg);
        if (mReceiveCallback != null && mReceiveCallback.onReceive(msg)) {
            return;
        }
    }

    public interface ReceiveCallback
    {
        boolean onReceive(String msg);
    }
}
