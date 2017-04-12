package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DF on 2017/4/11.
 */

public class OrderInfo implements Serializable {

    public String targetId;
    public String targetType;
    public String totalPrice;
    public String title;
    public AccountBean account;
    public int hasPayPassword;
    public String verifiedMobile;
    public String cashRate;
    public String priceType;
    public int coinPayAmount;
    public int maxCoin;
    public int fullCoinPayable;
    public List<?> availableCoupons;
    
    public static class AccountBean {
        public String id;
        public String userId;
        public String cash;
    }
}
