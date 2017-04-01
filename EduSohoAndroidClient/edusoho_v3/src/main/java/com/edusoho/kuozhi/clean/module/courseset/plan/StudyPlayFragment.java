package com.edusoho.kuozhi.clean.module.courseset.plan;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.courseset.BaseLazyFragment;

/**
 * Created by DF on 2017/3/21.
 */

public class StudyPlayFragment extends BaseLazyFragment {

    private View mLoad;
    private RecyclerView mRv;
    private StudyPlanAdapter mStudyPlanAdapter;

    @Override
    protected int initContentView() {
        return R.layout.fragment_study_plan;
    }

    @Override
    protected void initView(View view) {
        mRv = (RecyclerView) view.findViewById(R.id.rv_content);
        mLoad = view.findViewById(R.id.ll_frame_load);
        mStudyPlanAdapter = new StudyPlanAdapter(getContext());
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mRv.setAdapter(mStudyPlanAdapter);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void lazyLoad() {

    }
}
