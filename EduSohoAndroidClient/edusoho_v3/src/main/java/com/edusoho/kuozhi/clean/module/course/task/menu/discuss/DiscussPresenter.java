package com.edusoho.kuozhi.clean.module.course.task.menu.discuss;

import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;

import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/25.
 */

public class DiscussPresenter implements DiscussContract.Presenter {

    private int mCourseProjectId;
    private DiscussContract.View mView;

    public DiscussPresenter(DiscussContract.View mView, int mCourseProjectId) {
        this.mView = mView;
        this.mCourseProjectId = mCourseProjectId;
    }

    @Override
    public void subscribe() {
        Map<String, String> map = new HashMap<>();
        map.put("Accept", "");
        HttpUtils.getInstance()
                .addHeader(map)
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .getCourseDiscuss(mCourseProjectId, mCourseProjectId, 0)
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
                    }

                    @Override
                    public void onNext(DiscussDetail discussDetail) {
                        mView.setSwipeView(false);
                        if (discussDetail != null && discussDetail.getResources() != null && discussDetail.getResources().size() != 0) {
                            mView.showCompleteView(discussDetail.getResources());
                        } else {
                            mView.setEmptyView(true);
                        }
                    }
                });
    }

    @Override
    public void reFreshData() {

    }

    @Override
    public void unsubscribe() {

    }

}
