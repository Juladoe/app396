package com.edusoho.kuozhi.v3.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.belladati.httpclientandroidlib.util.TextUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by howzhi on 14-8-13.
 */
public class EdusohoMainService extends Service {

    protected EdusohoApp app;
    public static final String TAG = "EdusohoMainService";
    private static EdusohoMainService mService;
    private Handler workHandler;
    //private User mLoginUser;
    private Queue<Request<String>> mAjaxQueue;

    public static final int LOGIN_WITH_TOKEN = 0001;
    public static final int EXIT_USER = 0002;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(null, "create Main service");
        mAjaxQueue = new LinkedList<Request<String>>();
        app = (EdusohoApp) getApplication();
        mService = this;

        workHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case EXIT_USER:
                        app.loginUser = null;
                        break;
                    case LOGIN_WITH_TOKEN:
                        loginWithToken((ActionBarBaseActivity) msg.obj);
                        break;
                }
            }
        };
    }

    public void sendMessage(int type, Object obj) {
        Message message = workHandler.obtainMessage(type);
        message.obj = obj;
        message.sendToTarget();
    }

    private void loginWithToken(final ActionBarBaseActivity activity) {
        Log.d(null, "send loginwithtoken message1 " + app.token);
        if (TextUtils.isEmpty(app.token)) {
            return;
        }
        synchronized (this) {
            if (app.loginUser != null) {
                return;
            }
            if (!mAjaxQueue.isEmpty()) {
                return;
            }

            Log.d(null, "send loginwithtoken message " + app.token);
            RequestUrl url = app.bindUrl(Const.CHECKTOKEN, true);

            Request<String> request = app.postUrl(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mAjaxQueue.poll();
                    UserResult result = app.gson.fromJson(
                            response.toString(), new TypeToken<UserResult>() {
                            }.getType());
                    Log.d(null, "callback loginWithToken result->" + result);

                    if (result != null) {
                        //mLoginUser = result.data;
                        app.saveToken(result);
                    }

                    app.sendMessage(Const.LOGIN_SUCCESS, null);
                    app.sendMsgToTarget(DefaultPageActivity.XG_PUSH_REGISTER, null, DefaultPageActivity.class);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            mAjaxQueue.offer(request);
        }
    }

    public static EdusohoMainService getService() {
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

    public static void start(ActionBarBaseActivity activity) {
        activity.runService(TAG);
    }
}
