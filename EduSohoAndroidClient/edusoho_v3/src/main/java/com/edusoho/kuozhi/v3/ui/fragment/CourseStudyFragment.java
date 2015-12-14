package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

/**
 * Created by JesseHuang on 15/12/14.
 */
public class CourseStudyFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_study_fragment);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    protected void initView(View view) {

    }

    private void initData() {
    }
}
