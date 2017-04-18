package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DF on 2017/4/11.
 */

public class OrderInfo implements Serializable {

    public int targetId;
    public String targetType;
    public float totalPrice;
    public AccountBean account;
    public int hasPayPassword;
    public String coinName;
    public float cashRate;
    public String priceType;
    public float coinPayAmount;
    public float maxCoin;
    public String duration;
    public String fullCoinPayable;
    public String title;
    public List<Coupon> availableCoupons;

    public static class AccountBean implements Serializable{
        public String id;
        public String userId;
        public float cash;
    }

    public static class Coupon implements Serializable{
        public String id;
        public String code;
        public String type;
        public String status;
        public float rate;
        public String userId;
        public String deadline;
        public String targetType;
        public String targetId;
        public boolean isSelector;
    }
}
