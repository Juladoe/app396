package com.edusoho.kuozhi.clean.module.course.rate;

import com.edusoho.kuozhi.clean.bean.CourseProject;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseProjectRatesPresenter implements CourseProjectRatesContract.Presenter {

    private CourseProject mCourseProject;
    private CourseProjectRatesContract.View mView;

    public CourseProjectRatesPresenter(CourseProject courseProject, CourseProjectRatesContract.View view) {
        this.mCourseProject = courseProject;
        this.mView = view;
    }

    @Override
    public void getRates(int courseProjectId) {

    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
