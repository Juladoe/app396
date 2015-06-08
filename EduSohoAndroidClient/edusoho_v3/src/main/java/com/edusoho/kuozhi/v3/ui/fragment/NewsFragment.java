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
import com.edusoho.kuozhi.v3.model.InitModelTool;
import com.edusoho.kuozhi.v3.model.bal.news.NewsEnum;
import com.edusoho.kuozhi.v3.model.bal.news.NewsItem;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenu;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuCreator;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuItem;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuListView;

import java.util.List;

/**
 * Created by JesseHuang on 15/4/26.
 * 动态列表
 */
public class NewsFragment extends BaseFragment {
    private SwipeMenuListView lvNewsList;
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
        List<NewsItem> list = InitModelTool.initNewsItemList();
//        if (app.loginUser.nickname.equals("suju")) {
//            list = InitModelTool.initNewsItemList1();
//        } else {
//            list = InitModelTool.initNewsItemList();
//        }

        lvNewsList = (SwipeMenuListView) view.findViewById(R.id.lv_news_list);
        mSwipeAdapter = new SwipeAdapter(mContext, R.layout.news_item, list);
        lvNewsList.setAdapter(mSwipeAdapter);
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
                deleteItem.setWidth(AppUtil.dp2px(mContext, 90));
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        lvNewsList.setMenuCreator(creator);
        lvNewsList.setOnMenuItemClickListener(mMenuItemClickListener);
        lvNewsList.setOnItemClickListener(mItemClickListener);
    }

    SwipeMenuListView.OnMenuItemClickListener mMenuItemClickListener = new SwipeMenuListView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
            switch (index) {
                case 0:
                    mSwipeAdapter.removeItem(position);
                    mSwipeAdapter.notifyDataSetChanged();
                    // TODO 本地数据库操作
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
            final NewsItem newItem = (NewsItem) parent.getItemAtPosition(position);
            if (newItem.type == NewsEnum.FRIEND || newItem.type == NewsEnum.TEACHER) {
                app.mEngine.runNormalPlugin("ChatActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(ChatActivity.CHAT_DATA, newItem);
                    }
                });
            } else {

            }
        }
    };

    private void getNewOneMessage(Bundle data) {
        NewsItem item = (NewsItem) data.getSerializable("msg");
        //mSwipeAdapter.findNews(item);
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (messageType.code == Const.CHAT_MSG) {
            getNewOneMessage(message.data);
            message.callback.success("success");
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{new MessageType(Const.CHAT_MSG, source)};
        return messageTypes;
    }
}
