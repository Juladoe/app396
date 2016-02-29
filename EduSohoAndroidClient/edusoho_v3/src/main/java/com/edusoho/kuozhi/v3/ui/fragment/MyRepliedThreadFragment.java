package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.MyThreadAdapter;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.view.EduSohoDivederLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by melomelon on 16/2/26.
 */
public class MyRepliedThreadFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private MyThreadAdapter mAdapter;
    private EduSohoDivederLine mDividerLine;
    private TextView mEmptyTv;

    private List mDataList;

    public MyRepliedThreadFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.my_replied_thread_fragment_layout);
    }

    @Override
    protected void initView(View view) {
        mEmptyTv = (TextView) view.findViewById(R.id.empty_replied_thread);

        mDividerLine = new EduSohoDivederLine(EduSohoDivederLine.VERTICAL);
        mDividerLine.setColor(getResources().getColor(R.color.material_grey));
        mDividerLine.setSize(1);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_replied_thread_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(mDividerLine);

        initData();
    }

    public void initData() {
        mDataList = new ArrayList();

        //// TODO: 16/2/29
        if (mDataList.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyTv.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyTv.setVisibility(View.GONE);
        }

        mAdapter = new MyThreadAdapter(mContext, mDataList);
        mRecyclerView.setAdapter(mAdapter);
    }
}
