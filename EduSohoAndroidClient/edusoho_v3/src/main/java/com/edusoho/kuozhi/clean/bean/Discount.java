package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;

/**
 * Created by DF on 2017/4/10.
 */

public class Discount implements Serializable {

    private String id;
    private String name;
    private String type;
    private String startTime;
    private String endTime;
    private String itemType;
    private String itemCount;
    private String globalDiscount;
    private String status;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getItemType() {
        return itemType;
    }

    public String getItemCount() {
        return itemCount;
    }

    public String getGlobalDiscount() {
        return globalDiscount;
    }

    public String getStatus() {
        return status;
    }
}
