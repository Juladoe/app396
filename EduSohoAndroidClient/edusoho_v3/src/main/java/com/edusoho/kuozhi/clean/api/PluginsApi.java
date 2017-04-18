package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.Discount;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.v3.model.bal.VipLevel;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by JesseHuang on 2017/4/18.
 */

public interface PluginsApi {
    @GET("plugins/vip/vip_levels")
    Observable<List<VipInfo>> getVipInfo();

    @GET("plugins/vip/vip_levels/{id}")
    Observable<VipLevel> getVipLevel(@Path("id") int id);

    @GET("plugins/discount/discounts/{discountId}")
    Observable<Discount> getDiscountInfo(@Path("discountId") int discountId);
}
