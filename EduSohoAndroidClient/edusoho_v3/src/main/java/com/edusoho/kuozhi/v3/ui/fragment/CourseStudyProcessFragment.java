package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.StudyProcessRecyclerAdapter;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

import java.util.List;

/**
 * Created by melomelon on 15/12/9.
 */
public class CourseStudyProcessFragment extends BaseFragment{

    private RecyclerView studyProcessRecyclerView;

    private StudyProcessRecyclerAdapter mAdapter;

    private List dataList;
    private Bundle mBundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_course_study_process_layout);
        initData();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mAdapter = new StudyProcessRecyclerAdapter(mContext);
        studyProcessRecyclerView = (RecyclerView) view.findViewById(R.id.study_process_list);
        studyProcessRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        studyProcessRecyclerView.setAdapter(mAdapter);
        studyProcessRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public void initData(){
        initList();
        mBundle = getArguments();
//        mAdapter.setDataList(dataList);
    }

    public void initList(){
        //// TODO: 15/12/10 作模拟数据用

    }
}
