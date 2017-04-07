package com.edusoho.kuozhi.clean.module.courseset.paywayselector;

/**
 * Created by DF on 2017/4/7.
 */

public class PayWayPresenter implements PayWayContract.Presenter {

    private PayWayContract.View mView;

    public PayWayPresenter(PayWayContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
