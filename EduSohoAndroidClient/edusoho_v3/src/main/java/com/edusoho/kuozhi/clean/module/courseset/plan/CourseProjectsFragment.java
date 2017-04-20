package com.edusoho.kuozhi.clean.module.courseset.plan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.module.courseset.BaseLazyFragment;
import com.edusoho.kuozhi.clean.module.courseset.CourseUnLearnActivity;

import java.util.List;

/**
 * Created by DF on 2017/3/21.
 */

public class CourseProjectsFragment extends BaseLazyFragment
        implements CourseProjectsContract.View {

    private View mLoad;
    private RecyclerView mRv;
    private CourseProjectsAdapter mCourseProjectsAdapter;
    private CourseProjectsContract.Presenter mPresenter;
    private int mCourseSetId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCourseSetId = getArguments().getInt(CourseUnLearnActivity.COURSE_SET_ID);
    }

    @Override
    protected int initContentView() {
        return R.layout.fragment_study_plan;
    }

    @Override
    protected void initView(View view) {
        mRv = (RecyclerView) view.findViewById(R.id.rv_content);
        mLoad = view.findViewById(R.id.ll_frame_load);
        mCourseProjectsAdapter = new CourseProjectsAdapter(getContext());
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mRv.setAdapter(mCourseProjectsAdapter);

        mPresenter = new CourseProjectsPresenter(this, mCourseSetId);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        mPresenter.subscribe();
    }

    @Override
    public void setLoadViewVis(boolean isVis) {
        if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
            return;
        }
        mLoad.setVisibility(isVis ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showComPanies(List<CourseProject> list, List<VipInfo> vipInfos) {
        if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
            return;
        }
        mCourseProjectsAdapter.reFreshData(list, vipInfos);
    }
}
