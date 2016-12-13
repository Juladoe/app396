package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

/**
 * Created by Zhang on 2016/12/8.
 */

public class CourseDetailFragment extends BaseFragment {

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
        setContainerView(R.layout.fragment_course_detail);

        initView();
        initData();
        initEvent();
    }

    private void initData() {

    }

    private void initEvent() {

    }

    private void initView() {

    }
}
