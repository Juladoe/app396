package com.edusoho.kuozhi.clean.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 * 教学计划
 */

public class CourseProject implements Serializable {

    public int id;
    public int courseSetId;
    public String title;
    public String expiryMode;
    public String expiryDays;
    public String summary;
    public int studentNum;
    public String[] audiences;
    public String rating;
    public float price;
    public float originPrice;
    public int vipLevelId;
    public int publishedTaskNum;
    public Service[] services;
    public Teacher[] teachers;

    public static class Service implements Serializable {
        public String short_name;
        public String full_name;
        public String summary;
    }

    public static class Teacher implements Serializable {
        public int id;
        public String nickname;
        public String title;
        @SerializedName("largeAvatar")
        public String avatar;
    }
}
