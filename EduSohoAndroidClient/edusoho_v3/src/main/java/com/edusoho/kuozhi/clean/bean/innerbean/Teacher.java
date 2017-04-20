package com.edusoho.kuozhi.clean.bean.innerbean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/4/20.
 */

public class Teacher implements Serializable {
    public int id;
    public String nickname;
    public String title;
    @SerializedName("userAvatar")
    public Avatar avatar;
}
