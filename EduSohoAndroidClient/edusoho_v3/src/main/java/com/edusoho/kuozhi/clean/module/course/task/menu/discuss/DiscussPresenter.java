package com.edusoho.kuozhi.clean.module.course.task.menu.discuss;

import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.nostra13.universalimageloader.core.ImageLoader;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/25.
 */

public class DiscussPresenter implements DiscussContract.Presenter {

    private int mCourseProjectId;
    private int mStart;
    private DiscussContract.View mView;

    public DiscussPresenter(DiscussContract.View mView, int mCourseProjectId) {
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
                .getCourseDiscuss(EdusohoApp.app.token, mCourseProjectId, mCourseProjectId, 0)
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
                        mView.changeAdapterMoreStatus(DiscussAdapter.NO_LOAD_MORE);
                    }

                    @Override
                    public void onNext(DiscussDetail discussDetail) {
                        mView.setSwipeView(false);
                        mView.setAdapterStatus(2);
                        if (discussDetail != null && discussDetail.getResources() != null && discussDetail.getResources().size() != 0) {
                            mStart = 15;
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
                .getCourseDiscuss(EdusohoApp.app.token, mCourseProjectId, mCourseProjectId, mStart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DiscussDetail>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DiscussDetail discussDetail) {
                        mView.setSwipeView(false);
                        if (discussDetail != null && discussDetail.getResources() != null && discussDetail.getResources().size() != 0) {
                            mView.setAdapterStatus(2);
                            mView.addAdapterData(discussDetail.getResources(), discussDetail.getResources().size() >= 15);
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

}
