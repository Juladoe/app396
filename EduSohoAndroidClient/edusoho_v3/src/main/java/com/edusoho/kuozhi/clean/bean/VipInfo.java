package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;

/**
 * Created by DF on 2017/4/1.
 */

public class VipInfo implements Serializable {

    private String id;
    private String name;
    private String description;
    private String maxRate;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMaxRate() {
        return maxRate;
    }
}
