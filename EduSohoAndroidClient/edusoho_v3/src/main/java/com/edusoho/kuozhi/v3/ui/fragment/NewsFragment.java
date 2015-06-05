package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.NewsAdapter;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.InitModelTool;
import com.edusoho.kuozhi.v3.model.bal.news.NewsEnum;
import com.edusoho.kuozhi.v3.model.bal.news.NewsItem;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;

import java.util.List;

/**
 * Created by JesseHuang on 15/4/26.
 * 动态列表
 */
public class NewsFragment extends BaseFragment {
    private ListView lvChatList;
    private NewsAdapter mNewsAdapter;

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
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected void initView(View view) {
        lvChatList = (ListView) view.findViewById(R.id.lv_chat_list);
        List<NewsItem> list = InitModelTool.initNewsItemList();
//        if (app.loginUser.nickname.equals("suju")) {
//            list = InitModelTool.initNewsItemList1();
//        } else {
//            list = InitModelTool.initNewsItemList();
//        }
        mNewsAdapter = new NewsAdapter(mContext, R.layout.news_item, list);
        lvChatList.setAdapter(mNewsAdapter);
        lvChatList.setOnItemClickListener(mItemClickListener);
    }

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
        mNewsAdapter.findNews(item);
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
