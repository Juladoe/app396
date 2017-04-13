package com.edusoho.kuozhi.clean.module.courseset.payment;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        mView.showLoadDialog(true);
        RetrofitService.getMyVirtualCoin()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showLoadDialog(false);
                    }

                    @Override
                    public void onNext(String s) {
                        mView.showLoadDialog(false);
                    }
                });
    }

    @Override
    public void createOrderAndPay(Map<String, String> map, final String type, final String payment) {
        mView.showLoadDialog(true);
        map.put("targetId", mPlanId + "");
        RetrofitService.createOrder(EdusohoApp.app.token , map)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<JsonObject, Observable<JsonObject>>() {
                    @Override
                    public Observable<JsonObject> call(JsonObject jsonObject) {
                        if (jsonObject != null) {
                            int id = jsonObject.get("id").getAsInt();
                            return RetrofitService.goPay(EdusohoApp.app.token, id, type, payment);
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showLoadDialog(false);
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        mView.showLoadDialog(false);
                        String data = s.get("paymentHtml").getAsString();
                        Pattern p = Pattern.compile("post");
                        Matcher m = p.matcher(data);
                        data = m.replaceFirst("get");
                        mView.goToAlipay(data);
                    }
                });
    }

    @Override
    public void unsubscribe() {
    }
}
