package com.edusoho.kuozhi.clean.module.course.rate;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.v3.model.bal.User;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    private void getRates(String courseProjectId, String courseId) {
        RetrofitService.getCourseProjectReviews(courseProjectId, courseId, 0, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataPageResult<Review>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DataPageResult<Review> reviewDataPageResult) {
                        mView.showRates(reviewDataPageResult.data);
                    }
                });
    }

    @Override
    public void subscribe() {
        getRates(mCourseProject.courseSetId, mCourseProject.id);
    }

    @Override
    public void unsubscribe() {

    }
}
