package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by JesseHuang on 2017/4/18.
 */

public interface OrderApi {
    @FormUrlEncoded
    @POST("order_info")
    Observable<OrderInfo> postOrderInfo(@Field("targetType") String type, @Field("targetId") int id);

    @FormUrlEncoded
    @POST("orders")
    Observable<JsonObject> createOrder(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("pay_center")
    Observable<JsonObject> goPay(@Field("orderId") int id, @Field("targetType") String type, @Field("payment") String payWay);
}
