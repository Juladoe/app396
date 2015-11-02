package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.SwipeAdapter;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.model.bal.push.TypeBusinessEnum;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.ClassroomDiscussActivity;
import com.edusoho.kuozhi.v3.ui.NewsCourseActivity;
import com.edusoho.kuozhi.v3.ui.ServiceProviderActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.BulletinDataSource;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.ClassroomDiscussDataSource;
import com.edusoho.kuozhi.v3.util.sql.NewDataSource;
import com.edusoho.kuozhi.v3.util.sql.NewsCourseDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenu;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuCreator;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuItem;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 15/4/26.
 * 动态列表
 */
public class NewsFragment extends BaseFragment {
    public static final int HANDLE_SEND_CHAT_MSG = 11;
    public static final int HANDLE_RECEIVE_CHAT_MSG = 12;
    public static final int HANDLE_SEND_CLASSROOM_DISCUSS_MSG = 13;
    public static final int HANDLE_RECEIVE_CLASSROOM_DISCUSS_MSG = 14;
    public static final int UPDATE_UNREAD_MSG = 15;
    public static final int UPDATE_UNREAD_BULLETIN = 17;
    public static final int UPDATE_UNREAD_NEWS_COURSE = 18;
    public static final int UPDATE_UNREAD_ARTICLE_CREATE = 19;
    public static final int REFRESH_LIST = 20;

    private SwipeMenuListView lvNewsList;
    private View mEmptyView;
    private TextView tvEmptyText;
    private SwipeAdapter mSwipeAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_news);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            mActivity.setTitle(getString(R.string.title_news));
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.news_search) {
            app.mEngine.runNormalPlugin("QrSearchActivity", mContext, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initView(View view) {
        lvNewsList = (SwipeMenuListView) view.findViewById(R.id.lv_news_list);
        mEmptyView = view.findViewById(R.id.view_empty);
        tvEmptyText = (TextView) view.findViewById(R.id.tv_empty_text);
        tvEmptyText.setText(getResources().getString(R.string.news_empty_text));
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        mContext);
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(AppUtil.dp2px(mContext, 65));
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        mSwipeAdapter = new SwipeAdapter(mContext, R.layout.news_item, new ArrayList<New>());
        lvNewsList.setAdapter(mSwipeAdapter);
        lvNewsList.setMenuCreator(creator);
        lvNewsList.setOnMenuItemClickListener(mMenuItemClickListener);
        lvNewsList.setOnItemClickListener(mItemClickListener);
        initData();
        if (NotificationUtil.mMessage != null) {
            JSONObject jsonObject;
            WrapperXGPushTextMessage message = NotificationUtil.mMessage;
            try {
                jsonObject = new JSONObject(NotificationUtil.mMessage.getCustomContentJson());
                if (jsonObject.has("typeBusiness")) {
                    String type = jsonObject.getString("typeBusiness");
                    if (PushUtil.ChatUserType.FRIEND.equals(type) || PushUtil.ChatUserType.TEACHER.equals(type)) {
                        getNewChatMsg(HANDLE_RECEIVE_CHAT_MSG, NotificationUtil.mMessage);
                    } else {
                        handleBulletinMsg(message);
                    }
                } else {
                    Gson gson = new Gson();
                    V2CustomContent v2CustomContent = gson.fromJson(message.getCustomContentJson(), V2CustomContent.class);
                    switch (v2CustomContent.getBody().getType()) {
                        case PushUtil.ChatUserType.USER:
                        case PushUtil.ChatUserType.FRIEND:
                        case PushUtil.ChatUserType.TEACHER:
                            getNewChatMsg(HANDLE_RECEIVE_CHAT_MSG, message);
                            break;
                        case PushUtil.CourseType.LESSON_PUBLISH:
                            handlerReceiveCourse(message);
                            break;
                        case PushUtil.CourseType.TESTPAPER_REVIEWED:
                            break;
                    }
                }
                NotificationUtil.mMessage = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initData() {
        if (app.loginUser != null) {
            NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
            List<New> news = newDataSource.getNews("WHERE BELONGID = ? ORDER BY CREATEDTIME DESC", app.loginUser.id + "");
            mSwipeAdapter.update(news);
            setListVisibility(mSwipeAdapter.getCount() == 0);
        }
    }

    private SwipeMenuListView.OnMenuItemClickListener mMenuItemClickListener = new SwipeMenuListView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
            switch (index) {
                case 0:
                    New newModel = mSwipeAdapter.getItem(position);
                    mSwipeAdapter.removeItem(position);
                    NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                    newDataSource.delete(newModel);
                    int notificationId = 0;
                    switch (newModel.getType()) {
                        case PushUtil.BulletinType.TYPE:
                            BulletinDataSource bulletinDataSource = new BulletinDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                            notificationId = bulletinDataSource.getMaxId();
                            bulletinDataSource.delete();
                            break;
                        case PushUtil.ChatUserType.FRIEND:
                        case PushUtil.ChatUserType.TEACHER:
                            ChatDataSource chatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                            chatDataSource.delete(newModel.fromId, app.loginUser.id);
                            notificationId = newModel.fromId;
                            break;
                        case PushUtil.ChatUserType.CLASSROOM:
                            ClassroomDiscussDataSource mClassroomDiscussDataSource = new ClassroomDiscussDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                            mClassroomDiscussDataSource.delete(newModel.fromId, app.loginUser.id);
                            notificationId = newModel.fromId;
                            break;
                        case PushUtil.CourseType.TYPE:
                            NewsCourseDataSource newsCourseDataSource = new NewsCourseDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                            notificationId = newModel.fromId;
                            newsCourseDataSource.delete(newModel.fromId, app.loginUser.id);
                            break;
                        case PushUtil.ArticleType.TYPE:
                            notificationId = newModel.fromId;
                            break;
                    }
                    NotificationUtil.cancelById(notificationId);
                    setListVisibility(mSwipeAdapter.getCount() == 0);
                    break;
                case 1:
                    break;
            }
            return false;
        }
    };

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final New newItem = (New) parent.getItemAtPosition(position);
            TypeBusinessEnum.getName(newItem.type);
            switch (newItem.type) {
                case PushUtil.ChatUserType.FRIEND:
                case PushUtil.ChatUserType.TEACHER:
                    app.mEngine.runNormalPlugin("ChatActivity", mContext, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(ChatActivity.FROM_ID, newItem.fromId);
                            startIntent.putExtra(Const.ACTIONBAR_TITLE, newItem.title);
                            startIntent.putExtra(Const.NEWS_TYPE, newItem.type);
                            startIntent.putExtra(ChatActivity.HEAD_IMAGE_URL, newItem.imgUrl);
                        }
                    });
                    break;
                case PushUtil.ChatUserType.CLASSROOM:
                    app.mEngine.runNormalPlugin("ClassroomDiscussActivity", mContext, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(ClassroomDiscussActivity.FROM_ID, newItem.fromId);
                            startIntent.putExtra(Const.ACTIONBAR_TITLE, newItem.title);
                            startIntent.putExtra(ClassroomDiscussActivity.CLASSROOM_IMAGE, newItem.imgUrl);
                        }
                    });
                    break;
                case PushUtil.BulletinType.TYPE:
                    app.mEngine.runNormalPlugin("BulletinActivity", mContext, null);
                    break;
                case PushUtil.CourseType.TYPE:
                    app.mEngine.runNormalPlugin("NewsCourseActivity", mContext, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(NewsCourseActivity.COURSE_ID, newItem.fromId);
                            startIntent.putExtra(Const.ACTIONBAR_TITLE, newItem.title);
                        }
                    });
                    break;
                case PushUtil.ArticleType.TYPE:
                    app.mEngine.runNormalPlugin("ServiceProviderActivity", mContext, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(ServiceProviderActivity.SERVICE_TYPE, PushUtil.ArticleType.TYPE);
                            startIntent.putExtra(ServiceProviderActivity.SERVICE_ID, newItem.fromId);
                            startIntent.putExtra(Const.ACTIONBAR_TITLE, "资讯");
                        }
                    });
                    break;
            }
        }
    };

    private void insertNew(New newModel) {
        mSwipeAdapter.addItem(newModel);
    }

    private void updateNew(New newModel) {
        mSwipeAdapter.updateItem(newModel);
    }

    private void setItemToTop(New newModel) {
        mSwipeAdapter.setItemToTop(newModel);
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (Const.LOGIN_SUCCESS.equals(message.type.type)) {
            initData();
        } else {
            if (message.data == null) {
                return;
            }
            int fromId = message.data.getInt(Const.FROM_ID, 0);
            NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
            switch (messageType.code) {
                case Const.ADD_MSG:
                    int chatHandleType = message.data.getInt(Const.ADD_CHAT_MSG_DESTINATION, 0);
                    WrapperXGPushTextMessage chatMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
                    getNewChatMsg(chatHandleType, chatMessage);
                    break;
                case Const.ADD_COURSE_MSG:
                    WrapperXGPushTextMessage newsCourseMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
                    handlerReceiveCourse(newsCourseMessage);
                    break;
                case Const.ADD_BULLETIT_MSG:
                    WrapperXGPushTextMessage bulletinMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
                    handleBulletinMsg(bulletinMessage);
                    break;
                case UPDATE_UNREAD_MSG:
                    String type = message.data.getString(Const.NEWS_TYPE);
                    newDataSource.updateUnread(fromId, app.loginUser.id, type);
                    mSwipeAdapter.updateItem(fromId, type);
                    break;
                case UPDATE_UNREAD_BULLETIN:
                    newDataSource.updateBulletinUnread(app.loginUser.id, PushUtil.BulletinType.TYPE);
                    mSwipeAdapter.updateItem(fromId, PushUtil.BulletinType.TYPE);
                    break;
                case UPDATE_UNREAD_NEWS_COURSE:
                    newDataSource.updateUnread(fromId, app.loginUser.id, PushUtil.CourseType.TYPE);
                    mSwipeAdapter.updateItem(fromId, PushUtil.CourseType.TYPE);
                    break;
                case Const.ADD_CHAT_MSGS:
                    ArrayList<New> newArrayList = (ArrayList<New>) message.data.get(Const.GET_PUSH_DATA);
                    for (final New newModel : newArrayList) {
                        if (mSwipeAdapter.getContainItem(newModel)) {
                            updateNew(newModel);
                        } else {
                            insertNew(newModel);
                        }
                    }
                    break;
                case Const.ADD_ARTICLE_CREATE_MAG:
                    WrapperXGPushTextMessage articleCreateMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
                    handlerReceiveArticleMessage(articleCreateMessage);
                    break;
                case UPDATE_UNREAD_ARTICLE_CREATE:
                    NewDataSource articleDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                    articleDataSource.updateUnread(fromId, app.loginUser.id, PushUtil.ArticleType.TYPE);
                    mSwipeAdapter.updateItem(fromId, PushUtil.ArticleType.TYPE);
                    NotificationUtil.cancelById(fromId);
                    break;
                case Const.ADD_CLASSROOM_MSG:
                    WrapperXGPushTextMessage classroomMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
                    int classroomHandleType = message.data.getInt(Const.ADD_CLASSROOM_DISCUSS_MSG_DESTINATION, 0);
                    getNewChatMsg(classroomHandleType, classroomMessage);
                    break;
                case REFRESH_LIST:
                    List<New> news = newDataSource.getNews("WHERE BELONGID = ? ORDER BY CREATEDTIME DESC", app.loginUser.id + "");
                    mSwipeAdapter.update(news);
                    break;
            }
            setListVisibility(mSwipeAdapter.getCount() == 0);
        }
    }

    /**
     * 添加一条新消息
     *
     * @param chatType          消息处理方式：1发送，2接收
     * @param xgPushTextMessage 消息结构
     */
    private void getNewChatMsg(int chatType, WrapperXGPushTextMessage xgPushTextMessage) {
        switch (chatType) {
            case HANDLE_RECEIVE_CHAT_MSG:
                handleReceiveChatMsg(xgPushTextMessage);
                break;
            case HANDLE_RECEIVE_CLASSROOM_DISCUSS_MSG:
                handlerReceiveClassroomMsg(xgPushTextMessage);
                break;
            case HANDLE_SEND_CHAT_MSG:
                handleSendChatMsg(xgPushTextMessage);
                break;
            case HANDLE_SEND_CLASSROOM_DISCUSS_MSG:
                handleSendClassroomMsg(xgPushTextMessage);
                break;
        }
    }

    /**
     * 添加一条公告
     *
     * @param wrapperMessage 消息结构
     */
    private void handleBulletinMsg(WrapperXGPushTextMessage wrapperMessage) {
        New newModel = new New();
        newModel.belongId = app.loginUser.id;
        newModel.title = wrapperMessage.title;
        newModel.content = wrapperMessage.content;
        V2CustomContent customContent = wrapperMessage.getV2CustomContent();
        newModel.imgUrl = customContent.getFrom().getImage();
        newModel.createdTime = customContent.getCreatedTime();
        newModel.setType(TypeBusinessEnum.BULLETIN.getName());
        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        List<New> bulletins = newDataSource.getNews("WHERE BELONGID = ? AND TYPE = ? ORDER BY CREATEDTIME DESC", mActivity.app.loginUser.id + "",
                TypeBusinessEnum.BULLETIN.getName());
        if (bulletins.size() == 0) {
            newModel.unread = 1;
            newDataSource.create(newModel);
            insertNew(newModel);
        } else {
            newModel.unread = wrapperMessage.isForeground ? 0 : bulletins.get(0).unread + 1;
            newDataSource.update(newModel);
            setItemToTop(newModel);
        }
    }

    private void handlerReceiveArticleMessage(WrapperXGPushTextMessage wrapperMessage) {
        New newModel = new New();

        newModel.belongId = app.loginUser.id;
        newModel.title = wrapperMessage.title;
        V2CustomContent v2CustomContent = wrapperMessage.getV2CustomContent();
        newModel.content = wrapperMessage.content;
        newModel.setImgUrl(v2CustomContent.getFrom().getImage());
        newModel.setFromId(v2CustomContent.getFrom().getId());
        newModel.setType(v2CustomContent.getFrom().getType());
        newModel.setCreatedTime(v2CustomContent.getCreatedTime());

        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        List<New> news = newDataSource.getNews("WHERE BELONGID = ? AND TYPE = ?", app.loginUser.id + "", newModel.type);
        if (news.size() == 0) {
            newModel.unread = 1;
            newDataSource.create(newModel);
            insertNew(newModel);
        } else {
            newModel.unread = (wrapperMessage.isForeground) ? 0 : news.get(0).unread + 1;
            newDataSource.update(newModel);
            setItemToTop(newModel);
        }
    }

    private void handlerReceiveCourse(WrapperXGPushTextMessage wrapperMessage) {
        New newModel = new New();
        V2CustomContent v2CustomContent = wrapperMessage.getV2CustomContent();
        newModel.fromId = v2CustomContent.getFrom().getId();
        newModel.belongId = app.loginUser.id;
        newModel.title = wrapperMessage.title;
        String type = "";
        switch (v2CustomContent.getBody().getType()) {
            case PushUtil.CourseType.LESSON_PUBLISH:
                type = PushUtil.CourseCode.LESSON_PUBLISH;
                break;
            case PushUtil.CourseType.TESTPAPER_REVIEWED:
                type = PushUtil.CourseCode.TESTPAPER_REVIEWED;
                break;
            case PushUtil.CourseType.COURSE_ANNOUNCEMENT:
                type = PushUtil.CourseCode.COURSE_ANNOUNCEMENT;
                break;
        }
        newModel.content = String.format("【%s】%s", type, wrapperMessage.content);
        newModel.imgUrl = v2CustomContent.getFrom().getImage();
        newModel.createdTime = v2CustomContent.getCreatedTime();
        newModel.setType(v2CustomContent.getFrom().getType());

        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        List<New> news = newDataSource.getNews("WHERE FROMID = ? AND BELONGID = ?", newModel.fromId + "", app.loginUser.id + "");
        if (news.size() == 0) {
            newModel.unread = 1;
            newDataSource.create(newModel);
            insertNew(newModel);
        } else {
            newModel.unread = (wrapperMessage.isForeground && NewsCourseActivity.CurrentCourseId == newModel.fromId) ? 0 : news.get(0).unread + 1;
            newDataSource.update(newModel);
            setItemToTop(newModel);
        }
    }

    private void handleReceiveChatMsg(WrapperXGPushTextMessage wrapperMessage) {
        New newModel = new New(wrapperMessage);
        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        List<New> news = newDataSource.getNews("WHERE FROMID = ? AND TYPE = ? AND BELONGID = ?",
                newModel.fromId + "", newModel.type, app.loginUser.id + "");
        if (news.size() == 0) {
            newModel.unread = 1;
            newDataSource.create(newModel);
            insertNew(newModel);
        } else {
            newModel.unread = (wrapperMessage.isForeground && ChatActivity.CurrentFromId == newModel.fromId) ? 0 : news.get(0).unread + 1;
            newDataSource.update(newModel);
            setItemToTop(newModel);
        }
    }

    private void handlerReceiveClassroomMsg(WrapperXGPushTextMessage message) {
        New model = new New();
        V2CustomContent v2CustomContent = message.getV2CustomContent();
        model.fromId = v2CustomContent.getTo().getId();
        model.title = message.getTitle();
        switch (v2CustomContent.getBody().getType()) {
            case PushUtil.ChatMsgType.AUDIO:
                model.content = String.format("[%s]", Const.MEDIA_AUDIO);
                break;
            case PushUtil.ChatMsgType.IMAGE:
                model.content = String.format("[%s]", Const.MEDIA_IMAGE);
                break;
            case PushUtil.ChatMsgType.MULTI:
                RedirectBody body = EdusohoApp.app.parseJsonValue(message.getContent(), new TypeToken<RedirectBody>() {
                });
                model.content = body.content;
                break;
            default:
                model.content = message.getContent();
        }
        model.content = v2CustomContent.getFrom().getNickname() + ": " + model.content;
        model.createdTime = v2CustomContent.getCreatedTime();
        model.imgUrl = v2CustomContent.getTo().getImage();
        model.type = v2CustomContent.getTo().getType();
        model.belongId = EdusohoApp.app.loginUser.id;
        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        List<New> news = newDataSource.getNews("WHERE FROMID = ? AND TYPE = ? AND BELONGID = ?",
                model.fromId + "", model.type, app.loginUser.id + "");
        if (news.size() == 0) {
            model.unread = 1;
            model.id = (int) newDataSource.create(model);
            insertNew(model);
        } else {
            model.unread = (message.isForeground && ClassroomDiscussActivity.CurrentClassroomId == model.fromId) ? 0 : news.get(0).unread + 1;
            newDataSource.update(model);
            setItemToTop(model);
        }
    }

    private void handleSendChatMsg(WrapperXGPushTextMessage wrapperMessage) {
        New newModel = new New(wrapperMessage);
        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        List<New> news = newDataSource.getNews("WHERE FROMID = ? AND TYPE = ? AND BELONGID = ?",
                newModel.fromId + "", newModel.type, app.loginUser.id + "");
        if (news.size() == 0) {
            newModel.unread = 0;
            newModel.id = (int) newDataSource.create(newModel);
            insertNew(newModel);
        } else {
            newModel.unread = (wrapperMessage.isForeground && ChatActivity.CurrentFromId == newModel.fromId) ? 0 : news.get(0).unread + 1;
            newDataSource.update(newModel);
            setItemToTop(newModel);
        }
    }

    private void handleSendClassroomMsg(WrapperXGPushTextMessage message) {
        New model = new New();
        V2CustomContent v2CustomContent = message.getV2CustomContent();
        model.fromId = v2CustomContent.getTo().getId();
        model.title = message.getTitle();
        switch (v2CustomContent.getBody().getType()) {
            case PushUtil.ChatMsgType.AUDIO:
                model.content = String.format("[%s]", Const.MEDIA_AUDIO);
                break;
            case PushUtil.ChatMsgType.IMAGE:
                model.content = String.format("[%s]", Const.MEDIA_IMAGE);
                break;
            case PushUtil.ChatMsgType.MULTI:
                RedirectBody body = EdusohoApp.app.parseJsonValue(message.getContent(), new TypeToken<RedirectBody>() {
                });
                model.content = body.content;
                break;
            default:
                model.content = message.getContent();
        }
        model.createdTime = v2CustomContent.getCreatedTime();
        model.imgUrl = v2CustomContent.getTo().getImage();
        model.type = v2CustomContent.getTo().getType();
        model.belongId = EdusohoApp.app.loginUser.id;
        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        List<New> news = newDataSource.getNews("WHERE FROMID = ? AND TYPE = ? AND BELONGID = ?",
                model.fromId + "", model.type, app.loginUser.id + "");
        if (news.size() == 0) {
            model.unread = 0;
            model.id = (int) newDataSource.create(model);
            insertNew(model);
        } else {
            model.unread = (message.isForeground && ClassroomDiscussActivity.CurrentClassroomId == model.fromId) ? 0 : news.get(0).unread + 1;
            newDataSource.update(model);
            setItemToTop(model);
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[]{
                new MessageType(Const.ADD_MSG, source),
                new MessageType(Const.ADD_BULLETIT_MSG, source),
                new MessageType(Const.ADD_ARTICLE_CREATE_MAG, source),
                new MessageType(Const.LOGIN_SUCCESS),
                new MessageType(UPDATE_UNREAD_MSG, source),
                new MessageType(UPDATE_UNREAD_BULLETIN, source),
                new MessageType(UPDATE_UNREAD_NEWS_COURSE, source), new MessageType(REFRESH_LIST, source)};
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 设置空数据背景ICON
     *
     * @param visibility 是否空数据
     */

    private void setListVisibility(boolean visibility) {
        lvNewsList.setVisibility(visibility ? View.GONE : View.VISIBLE);
        mEmptyView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
}
