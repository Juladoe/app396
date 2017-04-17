package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;

/**
 * Created by DF on 2017/4/10.
 */

public class Discount implements Serializable {

    public int id;
    public String name;
    public String type;
    public long startTime;
    public long endTime;
    public String status;
}
