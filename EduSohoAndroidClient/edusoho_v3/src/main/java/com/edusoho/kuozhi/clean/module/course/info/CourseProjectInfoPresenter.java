package com.edusoho.kuozhi.clean.module.course.info;

import android.graphics.Paint;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;

import java.util.Locale;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseProjectInfoPresenter implements CourseProjectInfoContract.Presenter {

    private static final String NO_VIP = "0";
    private static final String FREE = "0.00";
    private CourseProject mCourseProject;
    private CourseProjectInfoContract.View mView;

    public CourseProjectInfoPresenter(CourseProject courseProject, CourseProjectInfoContract.View view) {
        this.mCourseProject = courseProject;
        this.mView = view;
    }

    @Override
    public void getTaskInfo() {

    }

    @Override
    public void getRelativeTask() {

    }

    @Override
    public void getTaskMembers() {

    }

    @Override
    public void subscribe() {
        mView.showCourseProjectInfo(mCourseProject);
        if (mCourseProject.originPrice.compareTo(mCourseProject.price) == 0 && FREE.equals(mCourseProject.originPrice)) {
            mView.showPrice(CourseProjectPriceEnum.FREE, mCourseProject.price, mCourseProject.originPrice);
        } else if (mCourseProject.originPrice.compareTo(mCourseProject.price) == 0) {
            mView.showPrice(CourseProjectPriceEnum.ORIGINAL, mCourseProject.price, mCourseProject.originPrice);
        } else {
            mView.showPrice(CourseProjectPriceEnum.SALE, mCourseProject.price, mCourseProject.originPrice);
        }
    }

    @Override
    public void unsubscribe() {

    }
}
