package com.edusoho.kuozhi.Service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 14-8-13.
 */
public class EdusohoMainService extends Service {

    protected EdusohoApp app;
    public static final String TAG = "EdusohoMainService";
    private static EdusohoMainService mService;
    private Handler workHandler;
    private User mLoginUser;

    public static final int LOGIN_WITH_TOKEN = 0001;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(null, "create Main service");
        app = (EdusohoApp) getApplication();
        mService = this;

        workHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case LOGIN_WITH_TOKEN:
                        loginWithToken();
                        break;
                }
            }
        };
    }

    public void sendMessage(int type, Object obj)
    {
        Message message = workHandler.obtainMessage(type);
        message.obj = obj;
        message.sendToTarget();
    }

    private User loginWithToken()
    {
        synchronized (this) {
            if (mLoginUser != null) {
                return mLoginUser;
            }
            Log.d(null, "send loginwithtoken message token->" + app.token);
            String url = app.bindUrl(Const.CHECKTOKEN);

            app.postUrl(url, null, new ResultCallback(){
                @Override
                public void callback(String url, String object, AjaxStatus ajaxStatus) {
                    Log.d(null, " loginWithToken->" + object);
                    TokenResult result = app.gson.fromJson(
                            object, new TypeToken<TokenResult>() {
                    }.getType());
                    if (result != null) {
                        mLoginUser = result.user;
                        app.saveToken(result);
                    }
                }
            });
        }

        return null;
    }

    public static EdusohoMainService getService()
    {
        return mService;
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
