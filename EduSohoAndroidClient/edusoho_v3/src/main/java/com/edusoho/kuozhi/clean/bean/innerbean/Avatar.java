package com.edusoho.kuozhi.clean.bean.innerbean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/4/20.
 */

public class Avatar implements Serializable {
    public String small;
    @SerializedName("medium")
    public String middle;
    public String large;
}
