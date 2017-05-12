package com.edusoho.kuozhi.clean.module.mine.teach;


import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.lesson.TeachLesson;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/5/12.
 */

public class MyTeachPresenter implements MyTeachContract.Presenter {

    private MyTeachContract.View mView;

    public MyTeachPresenter(MyTeachContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .baseOnApi()
                .createApi(UserApi.class)
                .getMyTeachCourse(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TeachLesson>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideSwpView();
                    }

                    @Override
                    public void onNext(TeachLesson teachLesson) {
                        mView.hideSwpView();
                        if (teachLesson != null && teachLesson.getResources() != null && teachLesson.getResources().size() != 0) {
                            mView.showRequestComplete(teachLesson);
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
