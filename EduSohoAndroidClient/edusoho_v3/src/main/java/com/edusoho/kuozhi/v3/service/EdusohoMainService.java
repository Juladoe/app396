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
import com.edusoho.kuozhi.v3.model.bal.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
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
//            if (mLoginUser != null) {
//                app.loginUser = mLoginUser;
//                return;
//            }
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
                        app.saveToken(result.data);
                    }

//                    app.sendMsgToTarget(MineFragment.LOGINT_WITH_TOKEN, null, MineFragment.class);
//                    app.sendMsgToTarget(SchoolRoomFragment.LOGINT_WITH_TOKEN, null, SchoolRoomFragment.class);

                    //app.sendMsgToTarget(MyInfoFragment.LOGINT_WITH_TOKEN, null, MyInfoFragment.class);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


//            AjaxCallback ajaxCallback = app.postUrl(false, url, new ResultCallback() {
//                @Override
//                public void callback(String url, String object, AjaxStatus ajaxStatus) {
//                    Log.d(null, "callback loginWithToken->" + ajaxStatus.getCode());
//                    mAjaxQueue.poll();
//                    TokenResult result = app.gson.fromJson(
//                            object, new TypeToken<TokenResult>() {
//                            }.getType());
//                    Log.d(null, "callback loginWithToken result->" + result);
//
//                    if (result != null) {
//                        //mLoginUser = result.data;
//                        app.saveToken(result);
//                    }
//
////                    app.sendMsgToTarget(MineFragment.LOGINT_WITH_TOKEN, null, MineFragment.class);
////                    app.sendMsgToTarget(SchoolRoomFragment.LOGINT_WITH_TOKEN, null, SchoolRoomFragment.class);
//
//                    //app.sendMsgToTarget(MyInfoFragment.LOGINT_WITH_TOKEN, null, MyInfoFragment.class);
//                }
//
//                @Override
//                public void update(String url, String object, AjaxStatus ajaxStatus) {
//                    int code = ajaxStatus.getCode();
//                    Log.d(null, "update loginWithToken ->" + code);
//                    if (code != Const.OK) {
//                        return;
//                    }
//                    TokenResult result = app.gson.fromJson(
//                            object, new TypeToken<TokenResult>() {
//                            }.getType());
//                    if (result == null) {
//                        if (app.loginUser != null) {
//                            app.removeToken();
////                            app.sendMsgToTarget(MineFragment.LOGINT_WITH_TOKEN, null, MineFragment.class);
////                            app.sendMsgToTarget(SchoolRoomFragment.LOGINT_WITH_TOKEN, null, SchoolRoomFragment.class);
//                            //app.sendMsgToTarget(MyInfoFragment.LOGOUT, null, MyInfoFragment.class);
//                        }
//                        return;
//                    }
//                    app.loginUser = result.data;
//                    app.saveToken(result);
////                    app.sendMsgToTarget(MineFragment.LOGINT_WITH_TOKEN, null, MineFragment.class);
////                    app.sendMsgToTarget(SchoolRoomFragment.LOGINT_WITH_TOKEN, null, SchoolRoomFragment.class);
//                    //app.sendMsgToTarget(MyInfoFragment.LOGINT_WITH_TOKEN, null, MyInfoFragment.class);
//                }
//            });

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
