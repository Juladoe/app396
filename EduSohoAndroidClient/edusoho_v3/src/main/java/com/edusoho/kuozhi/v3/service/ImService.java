package com.edusoho.kuozhi.v3.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.edusoho.kuozhi.imserver.ImServer;

/**
 * Created by su on 2016/3/18.
 */
public class ImService extends Service {

    private ImServer mImServer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(getClass().getSimpleName(), "onCreate");
        mImServer = new ImServer();
        mImServer.initWithHost("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getSimpleName(), "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(getClass().getSimpleName(), "onBind");
        return new ImBinder();
    }

    public class ImBinder extends Binder
    {
        public void send(String message) {
            mImServer.sendMessage(message);
        }

        public ImService getService() {
            return ImService.this;
        }
    }
}
