package com.edusoho.kuozhi.clean.module.course;

import android.support.annotation.Nullable;

/**
 * Created by JesseHuang on 2017/3/23.
 */

public class CourseProjectPresenter implements CourseProjectContract.Presenter {

    @Nullable
    private int mCourseProjectId;

    private CourseProjectContract.View mView;

    public CourseProjectPresenter(int courseProjectId, CourseProjectContract.View view) {
        mCourseProjectId = courseProjectId;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void learnTask(int taskId) {

    }

    @Override
    public void favorite(int taskId) {

    }

    @Override
    public void consult() {

    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void initFragments() {
        CourseProjectEnum[] courses = {CourseProjectEnum.INFO, CourseProjectEnum.TASKS, CourseProjectEnum.RATE};
        mView.showFragments(courses);
    }
}
