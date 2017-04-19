package com.edusoho.kuozhi.clean.module.courseset.review;


import com.edusoho.kuozhi.clean.api.CourseSetApi;
import com.edusoho.kuozhi.clean.bean.CourseReview;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.v3.adapter.discuss.CourseDiscussAdapter;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/3/31.
 */

class CourseEvaluatePresenter implements CourseEvaluateContract.Presenter {

    private CourseEvaluateContract.View mView;
    private int mCourseSetId;
    private int mStart = 0;
    private boolean mIsHave = true;
    private boolean mIsFirst = true;

    CourseEvaluatePresenter(CourseEvaluateContract.View mView, int mCourseSetId) {
        this.mView = mView;
        this.mCourseSetId = mCourseSetId;
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .createApi(CourseSetApi.class)
                .getCourseReview(mCourseSetId, 10, mStart)
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
                        firstLoad(courseReview);
                    }
                });
    }

    private void firstLoad(CourseReview courseReview){
        if (courseReview != null) {
            int length = courseReview.data.size();
            if (length < 10) {
                mIsHave = false;
                mView.setRecyclerViewStatus(CourseEvaluateAdapter.NO_LOAD_MORE);
            }
            mStart += 10;
            if (length == 0) {
                mView.setEmptyViewVis(true);
            } else {
                mView.showCompanies(courseReview.data);
            }
        }
    }

    @Override
    public void loadMore() {
        if (!mIsHave) {
            if (mIsFirst) {
                mIsFirst = false;
                mView.showToast();
            }
            mView.changeMoreStatus(CourseDiscussAdapter.NO_LOAD_MORE);
            return;
        }
        HttpUtils.getInstance()
                .createApi(CourseSetApi.class)
                .getCourseReview(mCourseSetId, 10, mStart)
                .subscribeOn(Schedulers.io())
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
                            int length = courseReview.data.size();
                            mStart += 10;
                            mIsHave = length > 10;
                            mView.addData(courseReview.data);
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
