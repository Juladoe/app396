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

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.SwipeAdapter;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.bal.push.CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.TypeBusinessEnum;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
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
    public static final int UPDATE_UNREAD_MSG = 10;
    public static final int UPDATE_UNREAD_BULLETIN = 11;

    private SwipeMenuListView lvNewsList;
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

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
//                SwipeMenuItem openItem = new SwipeMenuItem(
//                        mContext);
//                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
//                        0xCE)));
//                openItem.setWidth(AppUtil.dp2px(mContext, 90));
//                openItem.setTitle("标为未读");
//                openItem.setTitleSize(18);
//                openItem.setTitleColor(Color.WHITE);
//                menu.addMenuItem(openItem);

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
    }

    private void initData() {
        if (app.loginUser != null) {
            NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openRead();
            List<New> news = newDataSource.getNews("WHERE BELONGID = ?", app.loginUser.id + "");
            mSwipeAdapter.update(news);
            newDataSource.close();
        }
        if (TextUtils.isEmpty(mSchoolAvatar)) {
            RequestUrl requestUrl = app.bindNewUrl(Const.SCHOOL_APPS, true);
            StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
            requestUrl.url = stringBuffer.toString();
            mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    SchoolApp[] schoolAppResult = mActivity.parseJsonValue(response, new TypeToken<SchoolApp[]>() {
                    });
                    if (schoolAppResult.length != 0) {
                        mSchoolAvatar = schoolAppResult[0].avatar;
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
                    NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openWrite();
                    newDataSource.delete(newModel.id);
                    ChatDataSource chatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openWrite();
                    chatDataSource.delete(newModel.fromId, mActivity.app.loginUser.id);
                    mSwipeAdapter.notifyDataSetChanged();
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
                            startIntent.putExtra(ChatActivity.TITLE, newItem.title);
                        }
                    });
                    if (newItem.unread > 0) {

                    }
                    break;
                case "bulletin":
                    app.mEngine.runNormalPlugin("BulletinActivity", mContext, null);
                    break;
                case "course":
                    // TODO 打开课程
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
                    //收到消息更新消息列表的信息
                    int handleType = message.data.getInt(Const.ADD_CHAT_MSG_TYPE, 0);
                    if (handleType == HANDLE_RECEIVE_MSG) {
                        handleReceiveMsg(message);
                    } else if (handleType == HANDLE_SEND_MSG) {
                        handleSendMsg(message);
                    }
                    break;
                case UPDATE_UNREAD_MSG:
                    int fromId = message.data.getInt(Const.FROM_ID);
                    NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openWrite();
                    List<New> news = newDataSource.getNews("WHERE FROMID = ? AND BELONGID = ?", fromId + "", app.loginUser.id + "");
                    if (news.size() > 0) {
                        New newModel = news.get(0);
                        newModel.unread = 0;
                        newDataSource.update(newModel);
                        updateNew(newModel);
                    }
                    break;
                case Const.ADD_BULLETIT_MSG:
                    handleBulletinMsg(message);
                    break;
                case UPDATE_UNREAD_BULLETIN:
                    NewDataSource bulletinDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openWrite();
                    List<New> bulletins = bulletinDataSource.getNews("WHERE BELONGID = ? AND TYPE = ? ORDER BY CREATEDTIME DESC", mActivity.app.loginUser.id + "",
                            TypeBusinessEnum.BULLETIN.getName());
                    if (bulletins.size() > 0) {
                        New newModel = bulletins.get(0);
                        newModel.unread = 0;
                        bulletinDataSource.update(newModel);
                        updateNew(newModel);
                    }
                    bulletinDataSource.close();
                    break;
            }
        }
    }

    /**
     * 处理公告
     *
     * @param message
     */
    private void handleBulletinMsg(WidgetMessage message) {
        WrapperXGPushTextMessage wrapperMessage = (WrapperXGPushTextMessage) message.data.get(Const.CHAT_DATA);
        New newModel = new New();
        newModel.belongId = app.loginUser.id;
        newModel.title = wrapperMessage.title;
        newModel.content = wrapperMessage.content;
        CustomContent customContent = EdusohoApp.app.parseJsonValue(wrapperMessage.getCustomContent(), new TypeToken<CustomContent>() {
        });
        newModel.imgUrl = app.host + "/" + mSchoolAvatar;
        newModel.createdTime = customContent.getCreatedTime();
        newModel.setType(TypeBusinessEnum.BULLETIN.getName());
        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openWrite();
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
        newDataSource.close();
    }

    private void handleReceiveMsg(WidgetMessage message) {
        WrapperXGPushTextMessage wrapperMessage = (WrapperXGPushTextMessage) message.data.get(Const.CHAT_DATA);
        New newModel = new New(wrapperMessage);
        newModel.belongId = app.loginUser.id;
        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openWrite();
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
        newDataSource.close();
    }

    private void handleSendMsg(WidgetMessage message) {
        WrapperXGPushTextMessage wrapperMessage = (WrapperXGPushTextMessage) message.data.get(Const.CHAT_DATA);
        New newModel = new New(wrapperMessage);
        newModel.belongId = app.loginUser.id;
        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openWrite();
        List<New> news = newDataSource.getNews("WHERE FROMID = ? AND BELONGID = ?", newModel.fromId + "", app.loginUser.id + "");
        if (news.size() == 0) {
            newModel.unread = 0;
            newDataSource.create(newModel);
            insertNew(newModel);
        } else {
            newModel.unread = (wrapperMessage.isForeground && ChatActivity.CurrentFromId == newModel.fromId) ? 0 : news.get(0).unread + 1;
            newDataSource.update(newModel);
            setItemToTop(newModel);
        }
        newDataSource.close();
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{new MessageType(Const.ADD_CHAT_MSG, source), new MessageType(Const.ADD_BULLETIT_MSG, source),
                new MessageType(Const.LOGIN_SUCCESS),
                new MessageType(UPDATE_UNREAD_MSG, source),
                new MessageType(UPDATE_UNREAD_BULLETIN, source)};
        return messageTypes;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateUnreadFromLocalDB(New newItem) {
        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openWrite();
        newItem.unread = 0;
        newDataSource.update(newItem);
        newDataSource.close();
        updateNew(newItem);
    }
}
