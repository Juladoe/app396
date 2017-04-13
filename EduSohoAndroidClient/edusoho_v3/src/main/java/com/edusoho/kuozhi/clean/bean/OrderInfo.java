package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DF on 2017/4/11.
 */

public class OrderInfo implements Serializable {

    public String targetId;
    public String targetType;
    public int totalPrice;
    public AccountBean account;
    public int hasPayPassword;
    public String verifiedMobile;
    public String coinName;
    public String cashRate;
    public String priceType;
    public int coinPayAmount;
    public int maxCoin;
    public String unitType;
    public String duration;
    public String buyType;
    public int fullCoinPayable;
    public String title;
    public List<AvailableCouponsBean> availableCoupons;

    public static class AccountBean {
        public String id;
        public String userId;
        public String cash;
    }

    public static class AvailableCouponsBean {
        public String id;
        public String code;
        public String type;
        public String status;
        public final rate;
        public String userId;
        public String deadline;
        public String targetType;
        public String targetId;
    }
}
