package com.edusoho.kuozhi.clean.module.courseset.payments;

import android.support.annotation.NonNull;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.clean.module.courseset.payments.PaymentsContract.View;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.JsonObject;

import java.util.HashMap;
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

class PaymentsPresenter implements com.edusoho.kuozhi.clean.module.courseset.payments.PaymentsContract.Presenter {

    static final String ALIPAY = "alipay";
    static final String COIN = "coin";
    private static final String ORDER_ID = "id";
    private static final String TARGET_TYPE = "targetType";
    private static final String TARGET_ID = "targetId";
    private static final String COUPON_CODE = "couponCode";
    private static final String PAY_PASSWORD = "payPassword";
    private static final String COIN_PAYAMOUNT = "coinPayAmount";
    private static final String PAYMENT_HTML = "paymentHtml";
    private static final String STATUS = "status";
    private static final String STATUS_PAID = "paid";

    private View mView;
    private OrderInfo mOrderInfo;
    private int mPosition;

    PaymentsPresenter(View mView, OrderInfo orderInfo, int position) {
        this.mView = mView;
        this.mOrderInfo = orderInfo;
        this.mPosition = position;
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void createOrderAndPay(final String payment, String password, float orderPrice) {
        Map<String, String> map = createParameter(payment, password, orderPrice);
        RetrofitService.createOrder(EdusohoApp.app.token , map)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<JsonObject, Observable<JsonObject>>() {
                    @Override
                    public Observable<JsonObject> call(JsonObject jsonObject) {
                        if (jsonObject != null) {
                            int orderId = jsonObject.get(ORDER_ID).getAsInt();
                            return RetrofitService.goPay(EdusohoApp.app.token, orderId,
                                    mOrderInfo.targetType, payment);
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
                    public void onNext(JsonObject jsonObject) {
                        responseDeal(jsonObject, payment);
                    }
                });
    }

    private void responseDeal(JsonObject jsonObject, String payment) {
        mView.showLoadDialog(false);
        if (STATUS_PAID.equals(jsonObject.get(STATUS).getAsString())) {
            mView.sendBroad();
            return;
        }
        if (ALIPAY.equals(payment)) {
            String data = jsonObject.get(PAYMENT_HTML).getAsString();
            Pattern p = Pattern.compile("post");
            Matcher m = p.matcher(data);
            data = m.replaceFirst("get");
            mView.goToAlipay(data);
        }
    }

    @NonNull
    private Map<String, String> createParameter(String payment, String password, float orderPrice) {
        Map<String, String> map = new HashMap<>();
        if (mPosition != -1) {
            map.put(COUPON_CODE, mOrderInfo.availableCoupons.get(mPosition).code);
        }
        if (COIN.equals(payment)) {
            map.put(COIN_PAYAMOUNT, orderPrice + "");
            map.put(PAY_PASSWORD, password);
        }
        map.put(TARGET_TYPE, mOrderInfo.targetType);
        map.put(TARGET_ID, mOrderInfo.targetId + "");
        return map;
    }

    @Override
    public void unsubscribe() {
    }
}
