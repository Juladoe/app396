package com.edusoho.kuozhi.clean.module.courseset.review;


import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseReview;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/3/31.
 */

public class CourseEvaluatePresenter implements CourseEvaluateContract.Presenter {

    private CourseEvaluateContract.View mView;
    private String mCourseId;
    private int mStart = 0;
    private boolean mIsHave = true;
    private boolean mIsFirst = true;

    public CourseEvaluatePresenter(CourseEvaluateContract.View mView, String mCourseId) {
        this.mView = mView;
        this.mCourseId = mCourseId;
        this.mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        getCourseReview(mCourseId, 10, mStart)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseReview>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.setLoadViewVis(false);
                    }

                    @Override
                    public void onNext(CourseReview courseReview) {
                        mView.setLoadViewVis(false);
                        if (courseReview != null) {
                            int length = courseReview.getData().size();
                            if (length < 10) {
                                mIsHave = false;
                                mView.setRecyclerViewStatus(CourseEvaluateAdapter.NO_LOAD_MORE);
                            }
                            mStart += 10;
                            if (length == 0) {
                                mView.setEmptyViewVis(true);
                            } else {
                                mView.showCompanies(courseReview.getData());
                            }
                        }
                    }
                });
    }

    @Override
    public void loadMore() {
        if (!mIsHave) {
            if (mIsFirst) {
                mIsFirst = false;
                mView.showToast();
            }
        }
        getCourseReview(mCourseId, 10, mStart)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseReview>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.changeMoreStatus(CourseEvaluateAdapter.NO_LOAD_MORE);
                    }

                    @Override
                    public void onNext(CourseReview courseReview) {
                        if (courseReview != null) {
                            int length = courseReview.getData().size();
                            mStart += 10;
                            mIsHave = length > 10;
                            mView.addData(courseReview.getData());
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

    private Observable<CourseReview> getCourseReview(String id, int limit, int offset) {
        return RetrofitService.getCourseReview(id, limit, offset);
    }
}
