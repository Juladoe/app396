package com.edusoho.kuozhi.Service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.edusoho.kuozhi.ui.ActionBarBaseActivity;

/**
 * Created by howzhi on 14-8-13.
 */
public class EdusohoMainService extends Service {

    protected static final String TAG = "EdusohoMainService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public static void start(ActionBarBaseActivity activity)
    {
        activity.runService(TAG);
    }
}
