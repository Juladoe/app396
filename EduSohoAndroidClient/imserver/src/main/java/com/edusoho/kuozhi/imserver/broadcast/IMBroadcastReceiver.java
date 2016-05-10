package com.edusoho.kuozhi.imserver.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.listener.IConnectManagerListener;

import java.util.ArrayList;

/**
 * Created by su on 2016/3/22.
 */
public class IMBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION = "action";
    public static final int RECEIVER = 0;
    public static final int STATUS_CHANGE = 1;
    public static final int OFFLINE_MSG = 2;

    @Override
    public void onReceive(Context context, final Intent intent) {
        Log.d(getClass().getSimpleName(), "onReceive");
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int action = intent.getIntExtra(ACTION, RECEIVER);
                if (action == RECEIVER) {
                    MessageEntity message = intent.getParcelableExtra("message");
                    IMClient.getClient().invokeReceiver(message);
                } else if (action == OFFLINE_MSG) {
                    ArrayList<MessageEntity> message = intent.getParcelableArrayListExtra("message");
                    IMClient.getClient().invokeOfflineMsgReceiver(message);
                } else if (action == STATUS_CHANGE) {
                    int status = intent.getIntExtra("status", IConnectManagerListener.OPEN);
                    boolean isConnected = intent.getBooleanExtra("isConnected", false);
                    IMClient.getClient().invokeConnectReceiver(status, isConnected);
                }
            }
        });
    }
}
