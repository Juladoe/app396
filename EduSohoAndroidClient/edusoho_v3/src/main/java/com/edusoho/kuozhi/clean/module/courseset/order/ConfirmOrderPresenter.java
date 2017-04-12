package com.edusoho.kuozhi.clean.module.courseset.order;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.v3.EdusohoApp;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/6.
 */

public class ConfirmOrderPresenter implements com.edusoho.kuozhi.clean.module.courseset.order.ConfirmOrderContract.Presenter {

    private com.edusoho.kuozhi.clean.module.courseset.order.ConfirmOrderContract.View mView;
    private int mPlanId;

    public ConfirmOrderPresenter(ConfirmOrderContract.View mView, int mPlanId) {
        this.mView = mView;
        this.mPlanId = mPlanId;
    }

    @Override
    public void subscribe() {
        RetrofitService.postOrderInfo(EdusohoApp.app.token, "course", mPlanId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(OrderInfo orderInfo) {
                        if (orderInfo != null && "1".equals(orderInfo.fullCoinPayable)) {
                            mView.showCouponView();
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

}
