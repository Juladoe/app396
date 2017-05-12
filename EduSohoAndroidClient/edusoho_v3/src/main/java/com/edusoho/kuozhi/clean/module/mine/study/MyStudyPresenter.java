package com.edusoho.kuozhi.clean.module.mine.study;

import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.StudyCourse;
import com.edusoho.kuozhi.clean.bean.innerbean.Study;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/5/11.
 */

public class MyStudyPresenter implements MyStudyContract.Presenter {

    private MyStudyContract.View mView;

    public MyStudyPresenter(MyStudyContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void getMyStudyCourse() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyStudyCourse(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataPageResult<StudyCourse>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideSwp();
                    }

                    @Override
                    public void onNext(DataPageResult<StudyCourse> studyCourseDataPageResult) {
                        mView.hideSwp();
                        if (studyCourseDataPageResult != null && studyCourseDataPageResult.data != null) {
                            mView.showStudyCourseComplete(studyCourseDataPageResult.data);

                            List<StudyCourse> list = getNormalCourse(studyCourseDataPageResult);
                        }
                    }
                });
    }

    private List<StudyCourse> getNormalCourse(DataPageResult<StudyCourse> studyCourseDataPageResult) {
        List<StudyCourse> list = new ArrayList<StudyCourse>();
        for (StudyCourse studyCourse : studyCourseDataPageResult.data) {
            if ("normal".equals(studyCourse.courseSet.type)) {
                list.add(studyCourse);
            }
        }
        return list;
    }

    @Override
    public void getMyStudyLiveCourseSet() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyStudyLiveCourseSet(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Study>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideSwp();
                    }

                    @Override
                    public void onNext(List<Study> studies) {
                        mView.hideSwp();
                        if (studies != null) {
                            mView.showLiveCourseComplete(studies);
                        }
                    }
                });
    }

    @Override
    public void getMyStudyClassRoom() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyStudyClassRoom(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Study>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideSwp();
                    }

                    @Override
                    public void onNext(List<Study> studies) {
                        mView.hideSwp();
                        if (studies != null) {
                            mView.showClassRoomComplete(studies);
                        }
                    }
                });
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
