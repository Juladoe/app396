package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.SwipeAdapter;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.sql.NewDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenu;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuCreator;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuItem;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 15/4/26.
 * 动态列表
 */
public class NewsFragment extends BaseFragment {
    private SwipeMenuListView lvNewsList;
    private SwipeAdapter mSwipeAdapter;
    public static final int UPDATE_UNREAD = 0x01;

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
    }

    SwipeMenuListView.OnMenuItemClickListener mMenuItemClickListener = new SwipeMenuListView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
            switch (index) {
                case 0:
                    mSwipeAdapter.removeItem(position);
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

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (Const.LOGIN_SUCCESS.equals(message.type.type)) {
            initData();
        } else {
            switch (messageType.code) {
                case Const.ADD_CHAT_MSG:
                    //收到消息更新消息列表的信息
                    try {
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
                            newModel.unread = wrapperMessage.isForeground ? 0 : news.get(0).unread + 1;
                            newDataSource.update(newModel);
                            updateNew(newModel);
                        }
                        newDataSource.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case UPDATE_UNREAD:
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
            }
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{new MessageType(Const.ADD_CHAT_MSG, source), new MessageType(Const.LOGIN_SUCCESS),
                new MessageType(UPDATE_UNREAD, source)};
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
