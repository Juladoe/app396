package com.edusoho.kuozhi.clean.module.mine.question;

import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.thread.MyThreadEntity;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/5/11.
 */


class MyQuestionPresenter implements MyQuestionContract.Presenter {

    private MyQuestionContract.View mView;

    MyQuestionPresenter(MyQuestionContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void requestAskData() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .baseOnApi()
                .createApi(UserApi.class)
                .getMyAskThread(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MyThreadEntity[]>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideSwp();
                    }

                    @Override
                    public void onNext(MyThreadEntity[] myThreadEntities) {
                        if (myThreadEntities != null) {
                            mView.showAskComplete(myThreadEntities);
                        }
                    }
                });

    }

    @Override
    public void requestAnswerData() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .baseOnApi()
                .createApi(UserApi.class)
                .getMyAnswerThread(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MyThreadEntity[]>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideSwp();
                    }

                    @Override
                    public void onNext(MyThreadEntity[] myThreadEntities) {
                        if (myThreadEntities != null) {
                            mView.showAnswerComplete(myThreadEntities);
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
