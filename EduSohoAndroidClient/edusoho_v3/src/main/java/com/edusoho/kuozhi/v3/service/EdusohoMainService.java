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
import com.edusoho.kuozhi.v3.model.bal.article.ArticleModel;
import com.edusoho.kuozhi.v3.model.bal.push.Bulletin;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.NewsCourseEntity;
import com.edusoho.kuozhi.v3.model.bal.push.OfflineMsgEntity;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.BulletinDataSource;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.NewDataSource;
import com.edusoho.kuozhi.v3.util.sql.NewsCourseDataSource;
import com.edusoho.kuozhi.v3.util.sql.ServiceProviderDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
            if (!app.getNetIsConnect()) {
                try {
                    app.loginUser = app.loadUserInfo();
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            }

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
            WrapperXGPushTextMessage xgMessage = (WrapperXGPushTextMessage) msg.obj;
            switch (msg.what) {
                case EXIT_USER:
                    mEdusohoMainService.app.loginUser = null;
                    break;
                case LOGIN_WITH_TOKEN:
                    mEdusohoMainService.loginWithToken();
                    break;
                case Const.ADD_ARTICLE_CREATE_MAG:
                    //资讯推送
                    ArticleModel articleModel = new ArticleModel(xgMessage);
                    new ServiceProviderDataSource(SqliteChatUtil.getSqliteChatUtil(mService, EdusohoApp.app.domain)).create(articleModel);
                    if (!xgMessage.isForeground) {
                        //如果Activity不在最顶栈，显示通知
                        NotificationUtil.showArticleNotification(EdusohoApp.app.mContext, xgMessage);
                    }
                    break;
                case Const.ADD_CHAT_MSG:
                    //普通消息
                    Chat chatModel = new Chat(xgMessage);
                    ChatDataSource chatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mService, EdusohoApp.app.domain));
                    chatDataSource.create(chatModel);
                    if (!xgMessage.isForeground || (xgMessage.isForeground && ChatActivity.CurrentFromId != chatModel.fromId)) {
                        NotificationUtil.showMsgNotification(EdusohoApp.app.mContext, xgMessage);
                    }
                    break;
                case Const.ADD_BULLETIT_MSG:
                    //公告消息消息
                    Bulletin bulletin = new Bulletin(xgMessage);
                    BulletinDataSource bulletinDataSource = new BulletinDataSource(SqliteChatUtil.getSqliteChatUtil(mService, EdusohoApp.app.domain));
                    bulletinDataSource.create(bulletin);
                    if (!xgMessage.isForeground) {
                        NotificationUtil.showBulletinNotification(EdusohoApp.app.mContext, xgMessage);
                    }
                    break;
                case Const.ADD_COURSE_MSG:
                    NewsCourseEntity newsCourseEntity = new NewsCourseEntity(xgMessage);
                    NewsCourseDataSource newsCourseDataSource = new NewsCourseDataSource(SqliteChatUtil.getSqliteChatUtil(mService, EdusohoApp.app.domain));
                    newsCourseDataSource.create(newsCourseEntity);
                    if (!xgMessage.isForeground) {
                        NotificationUtil.showNewsCourseNotification(EdusohoApp.app.mContext, xgMessage);
                    }
                    break;
                case Const.ADD_DISCOUNT_PASS:
                    NotificationUtil.showDiscountPass(EdusohoApp.app.mContext, xgMessage);
                    break;
            }
        }
    }

    public void getOfflineMsgs() {
        final ChatDataSource chatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(EdusohoMainService.this, app.domain));
        final NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(EdusohoMainService.this, app.domain));
        final int id = (int) chatDataSource.getMaxId();
        String path = id == 0 ? Const.GET_LASTEST_OFFLINE_MSG : Const.GET_LASTEST_OFFLINE_MSG + "?lastMaxId=" + id;
        RequestUrl url = app.bindPushUrl(String.format(path, app.loginUser.id));
        app.getUrl(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<OfflineMsgEntity> latestChat = app.parseJsonValue(response, new TypeToken<ArrayList<OfflineMsgEntity>>() {
                });
                if (latestChat.size() > 0) {
                    //Collections.reverse(latestChat);
                    HashMap<Integer, ArrayList<OfflineMsgEntity>> latestHashMap = filterLatestChats(latestChat);
                    Iterator<Map.Entry<Integer, ArrayList<OfflineMsgEntity>>> iterators = latestHashMap.entrySet().iterator();
                    ArrayList<New> newArrayList = new ArrayList<>();
                    while (iterators.hasNext()) {
                        Map.Entry<Integer, ArrayList<OfflineMsgEntity>> iterator = iterators.next();
                        save2DB(iterator.getValue());
                        OfflineMsgEntity offlineMsgModel = iterator.getValue().get(iterator.getValue().size() - 1); //最新一个消息
                        New newModel = new New(offlineMsgModel);
                        List<New> news = newDataSource.getNews("WHERE FROMID = ? AND BELONGID = ?", offlineMsgModel.getCustom().getFrom().getId() + "", EdusohoApp.app.loginUser.id + "");
                        if (news.size() == 0) {
                            newModel.unread = iterator.getValue().size();
                            newDataSource.create(newModel);
                        } else {
                            newModel.unread = news.get(0).unread + iterator.getValue().size();
                            newDataSource.update(newModel);
                        }
                        newArrayList.add(newModel);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Const.GET_PUSH_DATA, newArrayList);
                    EdusohoApp.app.sendMsgToTarget(Const.ADD_CHAT_MSGS, bundle, NewsFragment.class);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error");
            }
        });
    }

    public ArrayList<Chat> save2DB(ArrayList<OfflineMsgEntity> offlineMsgEntityArrayList) {
        ArrayList<Chat> chatArrayList = new ArrayList<>();
        ArrayList<NewsCourseEntity> courseArrayList = new ArrayList<>();
        for (OfflineMsgEntity offlineMsgModel : offlineMsgEntityArrayList) {
            switch (offlineMsgModel.getCustom().getFrom().getType()) {
                case PushUtil.ChatUserType.TEACHER:
                case PushUtil.ChatUserType.FRIEND:
                    chatArrayList.add(new Chat(offlineMsgModel));
                    break;
                case PushUtil.CourseType.TESTPAPER_REVIEWED:
                    courseArrayList.add(new NewsCourseEntity(offlineMsgModel));
                    break;
            }
        }
        if (chatArrayList.size() > 0) {
            final ChatDataSource chatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(EdusohoMainService.this, app.domain));
            chatDataSource.create(chatArrayList);
        }
        if (courseArrayList.size() > 0) {
            final NewsCourseDataSource newsCourseDataSource = new NewsCourseDataSource(SqliteChatUtil.getSqliteChatUtil(EdusohoMainService.this, app.domain));
            newsCourseDataSource.create(courseArrayList);
        }
        return chatArrayList;
    }

    private HashMap<Integer, ArrayList<OfflineMsgEntity>> filterLatestChats(ArrayList<OfflineMsgEntity> latestChats) {
        int size = latestChats.size();
        HashMap<Integer, ArrayList<OfflineMsgEntity>> chatHashMaps = new HashMap<>();
        for (int i = 0; i < size; i++) {
            OfflineMsgEntity offlineMsgModel = latestChats.get(i);
            V2CustomContent v2CustomContent = offlineMsgModel.getCustom();
            if (v2CustomContent.getFrom() != null) {
                int fromId = v2CustomContent.getFrom().getId();
                if (chatHashMaps.containsKey(fromId)) {
                    chatHashMaps.get(fromId).add(offlineMsgModel);
                } else {
                    ArrayList<OfflineMsgEntity> tmpLatestChat = new ArrayList<>();
                    tmpLatestChat.add(offlineMsgModel);
                    chatHashMaps.put(fromId, tmpLatestChat);
                }
//                switch (v2CustomContent.getFrom().getType()) {
//                    case PushUtil.ChatUserType.FRIEND:
//                    case PushUtil.ChatUserType.TEACHER:
//                        int fromId = v2CustomContent.getFrom().getId();
//                        if (chatHashMaps.containsKey(fromId)) {
//                            chatHashMaps.get(fromId).add(offlineMsgModel);
//                        } else {
//                            ArrayList<OfflineMsgEntity> tmpLatestChat = new ArrayList<>();
//                            tmpLatestChat.add(offlineMsgModel);
//                            chatHashMaps.put(fromId, tmpLatestChat);
//                        }
//                        break;
//                    case PushUtil.CourseType.TESTPAPER_REVIEWED:
//                        break;
//                    default:
//                }
            }
        }
        return chatHashMaps;
    }

    public void setNewNotification() {
        app.config.newVerifiedNotify = true;
        app.saveConfig();
    }
}
