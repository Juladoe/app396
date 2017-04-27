package com.edusoho.kuozhi.clean.module.course.rate;

import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.utils.CommonConstant;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseProjectRatesPresenter implements CourseProjectRatesContract.Presenter {

    private CourseProject mCourseProject;
    private CourseProjectRatesContract.View mView;
    private int mOffset = 0;

    public CourseProjectRatesPresenter(CourseProject courseProject, CourseProjectRatesContract.View view) {
        this.mCourseProject = courseProject;
        this.mView = view;
    }

    @Override
    public void getRates() {
        getRates(mCourseProject.id);
    }

    public void getRates(int courseId) {
        HttpUtils.getInstance()
                .createApi(CourseApi.class)
                .getCourseProjectReviews(courseId, mOffset, CommonConstant.LIMIT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataPageResult<Review>>() {
                    @Override
                    public void onCompleted() {
                        mView.loadMoreCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DataPageResult<Review> reviewDataPageResult) {
                        if (reviewDataPageResult.data.size() > 0) {
                            mView.loadRates(reviewDataPageResult.data);
                            mOffset = reviewDataPageResult.paging.offset + CommonConstant.LIMIT;
                        }
                    }
                });
    }

    @Override
    public void subscribe() {
        mOffset = 0;
        mView.clearData();
        getRates(mCourseProject.id);
    }

    @Override
    public void unsubscribe() {

    }
}
