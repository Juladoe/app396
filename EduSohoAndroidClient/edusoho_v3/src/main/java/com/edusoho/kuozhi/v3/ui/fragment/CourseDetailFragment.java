package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.view.View;

/**
 * Created by Zhang on 2016/12/8.
 */

public class CourseDetailFragment extends BaseDetailFragment {

    private String mCourseId;

    public CourseDetailFragment() {
    }

    public CourseDetailFragment(String courseId) {
        this.mCourseId = courseId;
    }

    public void setCourseId(String courseId) {
        this.mCourseId = courseId;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
    }

    protected void initData() {

    }

    protected void initEvent() {

    }

    @Override
    protected void vipInfo() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected void moreStudent() {

    }

    @Override
    protected void moreReview() {

    }
}
