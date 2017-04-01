package com.edusoho.kuozhi.clean.module.courseset.plan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseStudyPlan;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.module.courseset.BaseLazyFragment;
import com.edusoho.kuozhi.v3.util.Const;

import java.util.List;

/**
 * Created by DF on 2017/3/21.
 */

public class StudyPlayFragment extends BaseLazyFragment
                                    implements StudyPlanContract.View{

    private View mLoad;
    private RecyclerView mRv;
    private StudyPlanAdapter mStudyPlanAdapter;
    private StudyPlanContract.Presenter mPresenter;
    private int mCourseId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCourseId = getArguments().getInt(Const.COURSE_ID);
    }

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

        mPresenter = new StudyPlanPresenter(this, mCourseId + "");
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void lazyLoad() {
        mPresenter.subscribe();
    }

    @Override
    public void setPresenter(StudyPlanContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void setLoadViewVis(boolean isVis) {
        mLoad.setVisibility(isVis ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showComPanies(List<CourseStudyPlan> list, List<VipInfo> vipInfos) {
        mStudyPlanAdapter.reFreshData(list, vipInfos);
    }
}
