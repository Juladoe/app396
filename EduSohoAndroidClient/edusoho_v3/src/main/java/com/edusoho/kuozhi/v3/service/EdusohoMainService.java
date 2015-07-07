package com.edusoho.kuozhi.v3.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.belladati.httpclientandroidlib.util.TextUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by howzhi on 14-8-13.
 */
public class EdusohoMainService extends Service {

    protected EdusohoApp app;
    public static final String TAG = "EdusohoMainService";
    private static EdusohoMainService mService;
    private WorkHandler mWorkHandler;
    //private User mLoginUser;
    private Queue<Request<String>> mAjaxQueue;

    public static final int LOGIN_WITH_TOKEN = 0001;
    public static final int EXIT_USER = 0002;
    public static final int INSERT_CHAT = 0x03;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(null, "create Main service");
        mAjaxQueue = new LinkedList<>();
        app = (EdusohoApp) getApplication();
        mService = this;
        mWorkHandler = new WorkHandler(this);
    }

    public void sendMessage(int type, Object obj) {
        Message message = mWorkHandler.obtainMessage(type);
        message.obj = obj;
        message.sendToTarget();
    }

    private void loginWithToken() {

        if (TextUtils.isEmpty(app.token)) {
            app.pushRegister(null);
            return;
        }
        synchronized (this) {
            if (app.loginUser != null) {
                app.sendMessage(Const.LOGIN_SUCCESS, null);
                Bundle bundle = new Bundle();
                bundle.putString(Const.BIND_USER_ID, app.loginUser.id + "");
                app.pushRegister(bundle);
                return;
            }
            if (!mAjaxQueue.isEmpty()) {
                return;
            }
            RequestUrl url = app.bindUrl(Const.CHECKTOKEN, true);
            Request<String> request = app.postUrl(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("-->", "onResponse");
                        mAjaxQueue.poll();
                        UserResult result = app.gson.fromJson(
                                response, new TypeToken<UserResult>() {
                                }.getType());

                        if (result != null) {
                            app.saveToken(result);
                            app.sendMessage(Const.LOGIN_SUCCESS, null);
                            Bundle bundle = new Bundle();
                            bundle.putString(Const.BIND_USER_ID, result.user.id + "");
                            app.pushRegister(bundle);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    public static class WorkHandler extends Handler {
        WeakReference<EdusohoMainService> mWeakReference;
        EdusohoMainService mEdusohoMainService;

        public WorkHandler(EdusohoMainService service) {
            mWeakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mEdusohoMainService == null) {
                mEdusohoMainService = mWeakReference.get();
            }
            super.handleMessage(msg);
            switch (msg.what) {
                case EXIT_USER:
                    mEdusohoMainService.app.loginUser = null;
                    break;
                case LOGIN_WITH_TOKEN:
                    mEdusohoMainService.loginWithToken();
                    break;
                case Const.ADD_CHAT_MSG:
                    try {
                        //消息写入到Chat表中
                        WrapperXGPushTextMessage xgMessage = (WrapperXGPushTextMessage) msg.obj;
                        Chat chatModel = new Chat(xgMessage);
                        ChatDataSource chatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mService, EdusohoApp.app.domain)).openWrite();
                        chatDataSource.create(chatModel);
                        chatDataSource.close();
                        if (!xgMessage.isForeground) {
                            //如果ChatActivity不在最顶栈，显示通知
                            NotificationUtil.showNotification(xgMessage);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


}
