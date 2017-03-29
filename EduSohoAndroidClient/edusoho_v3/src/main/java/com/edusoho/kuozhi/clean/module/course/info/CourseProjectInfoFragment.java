package com.edusoho.kuozhi.clean.module.course.info;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.course.CourseProjectContract;
import com.edusoho.kuozhi.clean.module.course.CourseProjectEnum;
import com.edusoho.kuozhi.clean.module.course.CourseProjectFragmentListener;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.wefika.flowlayout.FlowLayout;

/**
 * Created by JesseHuang on 2017/3/26.
 * 教学计划简介
 */

public class CourseProjectInfoFragment extends Fragment implements CourseProjectInfoContract.View, CourseProjectFragmentListener {

    private static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    private FlowLayout promiseFlowLayout;

    public CourseProjectFragmentListener newInstance(CourseProject courseProject) {
        CourseProjectInfoFragment fragment = new CourseProjectInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(COURSE_PROJECT_MODEL, courseProject);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public String getBundleKey() {
        return COURSE_PROJECT_MODEL;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_project_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        promiseFlowLayout = (FlowLayout) view.findViewById(R.id.fl_promise_layout);

        String[] str = {"24小时作业批阅", "24小时阅卷点评", "提问必答", "24小时作业11111批阅"};

        for (String s : str) {
            TextView tv = new TextView(getActivity());
            tv.setTextColor(Color.BLACK);
            tv.setText(s);
            tv.setTextSize(20);
            FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.rightMargin = 20;
            tv.setLayoutParams(lp);
            Log.d("MainActivity", tv.getMeasuredWidth() + "");
            promiseFlowLayout.addView(tv);
        }
    }

    @Override
    public void setPresenter(CourseProjectInfoContract.Presenter presenter) {

    }
}
