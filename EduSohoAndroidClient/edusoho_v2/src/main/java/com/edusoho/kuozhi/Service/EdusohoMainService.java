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
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.TokenResult;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.fragment.MyInfoFragment;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.PushUtil;
import com.edusoho.listener.ResultCallback;
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
    private User mLoginUser;
    private Queue<AjaxCallback> mAjaxQueue;

    public static final int LOGIN_WITH_TOKEN = 0001;
    public static final int EXIT_USER = 0002;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(null, "create Main service");
        mAjaxQueue = new LinkedList<AjaxCallback>();
        app = (EdusohoApp) getApplication();
        mService = this;

        workHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case EXIT_USER:
                        mLoginUser = null;
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

    public void stopAjaxFromQueue() {
        AjaxCallback ajaxCallback = null;
        while ((ajaxCallback = mAjaxQueue.poll()) != null) {
            Log.d(null, "abort->" + ajaxCallback);
            ajaxCallback.abort();
        }
    }

    private void loginWithToken(final ActionBarBaseActivity activity) {
        if ("".equals(app.token)) {
            return;
        }
        synchronized (this) {
            if (mLoginUser != null) {
                app.loginUser = mLoginUser;
                return;
            }
            if (!mAjaxQueue.isEmpty()) {
                return;
            }

            Log.d(null, "send loginwithtoken message token->" + app.token);
            RequestUrl url = app.bindUrl(Const.CHECKTOKEN, true);
            AjaxCallback ajaxCallback = app.postUrl(url, new ResultCallback() {
                @Override
                public void callback(String url, String object, AjaxStatus ajaxStatus) {
                    mAjaxQueue.poll();
                    TokenResult result = app.gson.fromJson(
                            object, new TypeToken<TokenResult>() {
                            }.getType());
                    if (result != null) {
                        mLoginUser = result.user;
                        app.saveToken(result);
                        //PushUtil.startPusherService(activity, activity.getBaseContext(), mLoginUser);
                    }
                    app.sendMsgToTarget(MyInfoFragment.LOGINT_WITH_TOKEN, null, MyInfoFragment.class);
                }
            });

            mAjaxQueue.offer(ajaxCallback);
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
