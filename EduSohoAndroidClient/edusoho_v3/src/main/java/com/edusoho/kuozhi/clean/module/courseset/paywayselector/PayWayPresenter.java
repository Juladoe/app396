package com.edusoho.kuozhi.clean.module.courseset.paywayselector;

import com.edusoho.kuozhi.clean.api.RetrofitService;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/7.
 */

public class PayWayPresenter implements PayWayContract.Presenter {

    private PayWayContract.View mView;
    private int mPlanId;

    public PayWayPresenter(PayWayContract.View mView, int mPlanId) {
        this.mView = mView;
        this.mPlanId = mPlanId;
    }

    @Override
    public void subscribe() {
        RetrofitService.getMyVirtualCoin()
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
    public void goPay(String type, String payWay) {
        RetrofitService.goPay(mPlanId, type, payWay)
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
