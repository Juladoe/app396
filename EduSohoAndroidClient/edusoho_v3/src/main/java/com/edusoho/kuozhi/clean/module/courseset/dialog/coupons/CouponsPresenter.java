package com.edusoho.kuozhi.clean.module.courseset.dialog.coupons;

/**
 * Created by DF on 2017/4/14.
 */

public class CouponsPresenter implements CouponsContract.Presenter {

    private CouponsContract.View mView;

    public CouponsPresenter(CouponsContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
