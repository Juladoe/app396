package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.NewsAdapter;
import com.edusoho.kuozhi.v3.model.InitModelTool;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

/**
 * Created by JesseHuang on 15/4/26.
 * 动态列表
 */
public class NewsFragment extends BaseFragment {
    private ListView lvChatList;

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
        NewsAdapter newsAdapter = new NewsAdapter(mContext, R.layout.news_item, InitModelTool.initNewsItemList());
        lvChatList.setAdapter(newsAdapter);
        lvChatList.setOnItemClickListener(mItemClickListener);
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

}
