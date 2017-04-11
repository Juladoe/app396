package com.edusoho.kuozhi.clean.module.courseset.payment;

import com.edusoho.kuozhi.clean.api.RetrofitService;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/7.
 */

public class PayWayPresenter implements com.edusoho.kuozhi.clean.module.courseset.payment.PayWayContract.Presenter {

    private com.edusoho.kuozhi.clean.module.courseset.payment.PayWayContract.View mView;
    private int mPlanId;

    public PayWayPresenter(com.edusoho.kuozhi.clean.module.courseset.payment.PayWayContract.View mView, int mPlanId) {
        this.mView = mView;
        this.mPlanId = mPlanId;
    }

    @Override
    public void subscribe() {
        RetrofitService.getMyVirtualCoin()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                .observeOn(AndroidSchedulers.mainThread())
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
