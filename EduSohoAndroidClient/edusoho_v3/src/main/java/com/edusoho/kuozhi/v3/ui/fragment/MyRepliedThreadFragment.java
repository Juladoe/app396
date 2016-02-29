package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.MyThreadAdapter;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.view.EduSohoDivederLine;

/**
 * Created by melomelon on 16/2/26.
 */
public class MyRepliedThreadFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private MyThreadAdapter mAdapter;
    private EduSohoDivederLine mDividerLine;

    public MyRepliedThreadFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.my_replied_thread_fragment_layout);
    }

    @Override
    protected void initView(View view) {

        mDividerLine = new EduSohoDivederLine(EduSohoDivederLine.VERTICAL);
        mDividerLine.setColor(getResources().getColor(R.color.material_grey));
        mDividerLine.setSize(1);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_replied_thread_recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyThreadAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(mDividerLine);
    }
}
