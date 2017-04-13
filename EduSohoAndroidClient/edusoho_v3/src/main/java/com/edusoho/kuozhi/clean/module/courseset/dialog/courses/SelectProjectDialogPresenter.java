package com.edusoho.kuozhi.clean.module.courseset.dialog.courses;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.JsonObject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/13.
 */

public class SelectProjectDialogPresenter implements SelectProjectDialogContract.Presenter {

    private SelectProjectDialogContract.View mView;

    public SelectProjectDialogPresenter(SelectProjectDialogContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void joinFreeCourse(int courseId) {
        RetrofitService.joinFreeCourse(EdusohoApp.app.token, courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {

                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
