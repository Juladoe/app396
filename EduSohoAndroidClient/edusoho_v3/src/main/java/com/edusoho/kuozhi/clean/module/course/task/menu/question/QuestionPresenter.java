package com.edusoho.kuozhi.clean.module.course.task.menu.question;

import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/25.
 */

public class QuestionPresenter implements QuestionContract.Presenter {

    private int mCourseProjectId;
    private int mStart;
    private QuestionContract.View mView;

    public QuestionPresenter(QuestionContract.View mView, int mCourseProjectId) {
        this.mView = mView;
        this.mCourseProjectId = mCourseProjectId;
    }

    @Override
    public void subscribe() {
        new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(EdusohoApp.app.host + "/api/")
                .build().create(CourseApi.class)
                .getCourseDiscuss(EdusohoApp.app.token, mCourseProjectId, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DiscussDetail>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.setSwipeView(false);
                        mView.setEmptyView(true);
                        mView.changeAdapterMoreStatus(QuestionAdapter.NO_LOAD_MORE);
                    }

                    @Override
                    public void onNext(DiscussDetail discussDetail) {
                        mView.setSwipeView(false);
                        mView.changeAdapterMoreStatus(QuestionAdapter.NO_LOAD_MORE);
                        if (discussDetail != null && discussDetail.getResources() != null && discussDetail.getResources().size() != 0) {
                            mStart = 15;
                            mView.setEmptyView(false);
                            mView.showCompleteView(discussDetail.getResources(), discussDetail.getResources().size() >= 15);
                        } else {
                            mView.setEmptyView(true);
                        }
                    }
                });
    }

    @Override
    public void reFreshData() {
        new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(EdusohoApp.app.host + "/api/")
                .build().create(CourseApi.class)
                .getCourseDiscuss(EdusohoApp.app.token, mCourseProjectId, mStart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DiscussDetail>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.changeAdapterMoreStatus(QuestionAdapter.NO_LOAD_MORE);
                    }

                    @Override
                    public void onNext(DiscussDetail discussDetail) {
                        mView.setSwipeView(false);
                        mView.changeAdapterMoreStatus(QuestionAdapter.NO_LOAD_MORE);
                        if (discussDetail != null && discussDetail.getResources() != null && discussDetail.getResources().size() != 0) {
                            mView.setEmptyView(false);
                            mView.addAdapterData(discussDetail.getResources(), discussDetail.getResources().size() >= 15);
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

}
