package com.edusoho.kuozhi.clean.module.courseset.confirmorder;

import com.edusoho.kuozhi.clean.api.RetrofitService;

import java.util.Map;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/6.
 */

public class ConfirmOrderPresenter implements ConfirmOrderContract.Presenter {

    private ConfirmOrderContract.View mView;
    private int mPlanId;

    public ConfirmOrderPresenter(ConfirmOrderContract.View mView, int mPlanId) {
        this.mView = mView;
        this.mPlanId = mPlanId;
    }

    @Override
    public void subscribe() {
        RetrofitService.getMyCoupons()
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {

                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void postOrderInfo(String type) {
        RetrofitService.postOrderInfo(type, mPlanId)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {

                    }
                });
    }

    @Override
    public void createOrder(Map<String, String> map) {
        RetrofitService.createOrder(map)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {

                    }
                });

    }

}
