package com.edusoho.kuozhi.clean.module.course.rate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.course.CourseProjectFragmentListener;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseProjectRatesFragment extends Fragment implements
        CourseProjectRatesContract.View, CourseProjectFragmentListener {

    private static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    private CourseProjectRatesContract.Presenter mPresenter;
    private RecyclerView rateRecyclerView;

    public CourseProjectFragmentListener newInstance(CourseProject courseProject) {
        CourseProjectRatesFragment fragment = new CourseProjectRatesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(COURSE_PROJECT_MODEL, courseProject);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public String getBundleKey() {
        return COURSE_PROJECT_MODEL;
    }

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rateRecyclerView = (RecyclerView) view.findViewById(R.id.rv_content);
    }

    public static class RateViewHolder extends RecyclerView.ViewHolder {

        public RateViewHolder(View itemView) {
            super(itemView);
        }
    }
}
