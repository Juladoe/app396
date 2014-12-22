package com.edusoho.kuozhi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.edusoho.kuozhi.Service.PusherService;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.PushUtil;

/**
 * Created by JesseHuang on 14/12/17.
 * 用户通知PusherService唤醒WakeLock
 */
public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (PushUtil.hasNetwork(context) == false) {
            return;
        }

        Intent startSrv = new Intent(context, PusherService.class);
        startSrv.putExtra(Const.PUSH_CMD_CODE, PusherService.WAKE);
        context.startService(startSrv);
    }
}
