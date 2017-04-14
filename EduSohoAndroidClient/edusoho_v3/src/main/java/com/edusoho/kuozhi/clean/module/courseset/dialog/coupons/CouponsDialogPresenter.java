package com.edusoho.kuozhi.clean.module.courseset.dialog.coupons;

/**
 * Created by DF on 2017/4/14.
 */

public class CouponsDialogPresenter implements CouponsDialogContract.Presenter {

    private CouponsDialogContract.View mView;

    public CouponsDialogPresenter(CouponsDialogContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
