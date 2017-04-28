package com.edusoho.kuozhi.v3.model.bal;

/**
 * Created by tree on 2017/4/27.
 */

public class VipVoucher {

    public String name;
    public int price;
    public String desc;
    public boolean isShow;//是否显示“使用说明”的布局，属于ui使用的变量，与业务无关

    public VipVoucher(String name, int price, String desc) {
        this.name = name;
        this.price = price;
        this.desc = desc;
    }
}
