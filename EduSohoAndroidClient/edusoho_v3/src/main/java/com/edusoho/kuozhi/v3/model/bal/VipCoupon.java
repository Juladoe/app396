package com.edusoho.kuozhi.v3.model.bal;

/**
 * Created by tree on 2017/4/27.
 */

public class VipCoupon {

    public String id;
    public String deadline;
    public String type;
    public String rate;
    public String code;
    public String status;
    public String batchId;
    public String userId;
    public int generatedNum;
    public String prefixCode;
    public String detail;
    public int digits;
    public String orderId;
    public String orderTime;
    public String targetId;
    public String targetType;
    public String createdTime;
    public String receiveTime;
    public String fullDiscountPrice;
    public String url;

    /**
     * 属于batchs的属性
     */
    public String name;
    public String description;

    /**
     * 是否显示“使用说明”的布局，属于ui使用的变量，与业务无关
     */
    public boolean isShow;

}
