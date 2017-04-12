package com.edusoho.kuozhi.clean.module.courseset.payment;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.JsonObject;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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
    public void createOrderAndPay(Map<String, String> map, final String type, final String payment) {
        map.put("targetId", mPlanId + "");
        RetrofitService.createOrder(EdusohoApp.app.token , map)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<JsonObject, Observable<String>>() {
                    @Override
                    public Observable<String> call(JsonObject jsonObject) {
                        if (jsonObject != null) {
                            int id = jsonObject.get("id").getAsInt();
                            return RetrofitService.goPay(id, type, payment);
                        }
                        return null;
                    }
                })
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
}
