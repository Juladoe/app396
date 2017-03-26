package com.edusoho.kuozhi.clean.module.course.rate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseProjectRatesFragment extends Fragment implements CourseProjectRatesContract.View {

    @Override
    public void showRates() {

    }

    @Override
    public void setPresenter(CourseProjectRatesContract.Presenter presenter) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_project_rates, container, false);
    }
}
