package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.SwipeAdapter;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.bal.push.CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.TypeBusinessEnum;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.NewsCourseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.BulletinDataSource;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.NewDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenu;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuCreator;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuItem;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuListView;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 15/4/26.
 * 动态列表
 */
public class NewsFragment extends BaseFragment {
    public static final int HANDLE_SEND_MSG = 1;
    public static final int HANDLE_RECEIVE_MSG = 2;
    public static final int HANDLE_RECEIVE_COURSE = 3;
    public static final int UPDATE_UNREAD_MSG = 10;
    public static final int UPDATE_UNREAD_BULLETIN = 11;
    public static final int UPDATE_UNREAD_NEWS_COURSE = 12;

    private SwipeMenuListView lvNewsList;
    private View mEmptyView;
    private TextView tvEmptyText;
    private SwipeAdapter mSwipeAdapter;
    private String mSchoolAvatar;

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
            getNewChatMsg(HANDLE_RECEIVE_MSG, NotificationUtil.mMessage);
            NotificationUtil.mMessage = null;
        }
    }

    private void initData() {
        if (app.loginUser != null) {
            NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
            List<New> news = newDataSource.getNews("WHERE BELONGID = ? ORDER BY CREATEDTIME DESC", app.loginUser.id + "");
            mSwipeAdapter.update(news);
            setListVisibility(mSwipeAdapter.getCount() == 0);
        }
        if (TextUtils.isEmpty(mSchoolAvatar)) {
            RequestUrl requestUrl = app.bindNewUrl(Const.SCHOOL_APPS, true);
            mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    SchoolApp[] schoolAppResult = mActivity.parseJsonValue(response, new TypeToken<SchoolApp[]>() {
                    });
                    if (schoolAppResult != null && schoolAppResult.length != 0) {
                        mSchoolAvatar = schoolAppResult[0].avatar;
                    } else {
                        CommonUtil.shortToast(mContext, getResources().getString(R.string.school_info_error));
                    }
                }
            }, null);
        }
    }

    SwipeMenuListView.OnMenuItemClickListener mMenuItemClickListener = new SwipeMenuListView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
            switch (index) {
                case 0:
                    New newModel = mSwipeAdapter.getItem(position);
                    mSwipeAdapter.removeItem(position);
                    NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                    newDataSource.delete(newModel.id);
                    int notificationId;
                    if (newModel.getType().equals(TypeBusinessEnum.BULLETIN.getName())) {
                        BulletinDataSource bulletinDataSource = new BulletinDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                        notificationId = bulletinDataSource.delete();
                    } else {
                        ChatDataSource chatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openWrite();
                        chatDataSource.delete(newModel.fromId, mActivity.app.loginUser.id);
                        notificationId = newModel.fromId;
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

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final New newItem = (New) parent.getItemAtPosition(position);
            TypeBusinessEnum.getName(newItem.type);
            switch (newItem.type) {
                case "friend":
                case "teacher":
                    app.mEngine.runNormalPlugin("ChatActivity", mContext, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(ChatActivity.FROM_ID, newItem.fromId);
                            startIntent.putExtra(Const.ACTIONBAR_TITLE, newItem.title);
                            startIntent.putExtra(ChatActivity.HEAD_IMAGE_URL, newItem.imgUrl);
                        }
                    });
                    if (newItem.unread > 0) {

                    }
                    break;
                case "bulletin":
                    app.mEngine.runNormalPlugin("BulletinActivity", mContext, null);
                    break;
                case PushUtil.CourseType.LESSON_PUBLISH:
                    app.mEngine.runNormalPlugin("NewsCourseActivity", mContext, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(NewsCourseActivity.COURSE_ID, newItem.fromId);
                            startIntent.putExtra(Const.ACTIONBAR_TITLE, newItem.title);
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
            switch (messageType.code) {
                case Const.ADD_CHAT_MSG:
                    int handleType = message.data.getInt(Const.ADD_CHAT_MSG_TYPE, 0);
                    WrapperXGPushTextMessage chatMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
                    getNewChatMsg(handleType, chatMessage);
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
                    NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                    List<New> news = newDataSource.getNews("WHERE FROMID = ? AND BELONGID = ?", message.data.getInt(Const.FROM_ID) + "", app.loginUser.id + "");
                    if (news.size() > 0) {
                        New newModel = news.get(0);
                        newModel.unread = 0;
                        newDataSource.update(newModel);
                        updateNew(newModel);
                    }
                    break;
                case UPDATE_UNREAD_BULLETIN:
                    NewDataSource bulletinDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                    List<New> bulletins = bulletinDataSource.getNews("WHERE BELONGID = ? AND TYPE = ? ORDER BY CREATEDTIME DESC", mActivity.app.loginUser.id + "",
                            TypeBusinessEnum.BULLETIN.getName());
                    if (bulletins.size() > 0) {
                        New newModel = bulletins.get(0);
                        newModel.unread = 0;
                        bulletinDataSource.update(newModel);
                        updateNew(newModel);
                    }
                    break;
                case UPDATE_UNREAD_NEWS_COURSE:
                    NewDataSource newsCourseDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                    int fromId = message.data.getInt(Const.FROM_ID);
                    newsCourseDataSource.updateUnread(message.data.getInt(Const.FROM_ID), app.loginUser.id, PushUtil.CourseType.LESSON_PUBLISH);
                    mSwipeAdapter.updateItem(fromId, PushUtil.CourseType.LESSON_PUBLISH);
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
            case HANDLE_RECEIVE_MSG:
                handleReceiveMsg(xgPushTextMessage);
                break;
            case HANDLE_SEND_MSG:
                handleSendMsg(xgPushTextMessage);
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
        CustomContent customContent = EdusohoApp.app.parseJsonValue(wrapperMessage.getCustomContentJson(), new TypeToken<CustomContent>() {
        });
        newModel.imgUrl = app.host + "/" + mSchoolAvatar;
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
            newDataSource.updateBulletin(newModel);
            setItemToTop(newModel);
        }
    }

    private void handlerReceiveCourse(WrapperXGPushTextMessage wrapperMessage) {
        New newModel = new New();
        V2CustomContent v2CustomContent = wrapperMessage.getV2CustomContent();
        newModel.fromId = v2CustomContent.getFrom().getId();
        newModel.belongId = app.loginUser.id;
        newModel.title = wrapperMessage.title;
        newModel.content = wrapperMessage.content;
        newModel.imgUrl = v2CustomContent.getFrom().getImage();
        newModel.createdTime = v2CustomContent.getCreatedTime();
        newModel.setType(v2CustomContent.getBody().getType());

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

    private void handleReceiveMsg(WrapperXGPushTextMessage wrapperMessage) {
        New newModel = new New(wrapperMessage);
        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        List<New> news = newDataSource.getNews("WHERE FROMID = ? AND BELONGID = ?", newModel.fromId + "", app.loginUser.id + "");
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

    private void handleSendMsg(WrapperXGPushTextMessage wrapperMessage) {
        New newModel = new New(wrapperMessage);
        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        List<New> news = newDataSource.getNews("WHERE FROMID = ? AND BELONGID = ?", newModel.fromId + "", app.loginUser.id + "");
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

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[]{new MessageType(Const.ADD_CHAT_MSG, source),
                new MessageType(Const.ADD_BULLETIT_MSG, source),
                new MessageType(Const.ADD_CHAT_MSGS, source),
                new MessageType(Const.LOGIN_SUCCESS),
                new MessageType(UPDATE_UNREAD_MSG, source),
                new MessageType(UPDATE_UNREAD_BULLETIN, source),
                new MessageType(UPDATE_UNREAD_NEWS_COURSE, source)};
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
