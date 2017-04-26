package com.edusoho.kuozhi.clean.module.course.task.menu.rate;

/**
 * Created by DF on 2017/4/25.
 */

public class RatesPresenter implements RatesContract.Presenter {

    private int mCourseProjectId;
    private RatesContract.View mView;

    public RatesPresenter(int mCourseProjectId, RatesContract.View mView) {
        this.mCourseProjectId = mCourseProjectId;
        this.mView = mView;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
