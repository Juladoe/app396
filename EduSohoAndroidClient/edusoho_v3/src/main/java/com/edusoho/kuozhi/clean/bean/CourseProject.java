package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 * 教学计划
 */

public class CourseProject implements Serializable {

    public String id;
    public String courseSetId;
    public String title;
    public String expiryMode;
    public String expiryDays;
    public String summary;
    public int studentNum;
    public String[] audiences;
    public String rating;
    public String price;
    public String originPrice;
    public String vipLevelId;
    public int publishedTaskNum;
    public Service[] services;
    public Teacher[] teachers;

    public static class Service implements Serializable {
        public String short_name;
        public String full_name;
        public String summary;
    }

    public static class Teacher implements Serializable {
        public String id;
        public String nickname;
        public String title;
    }
}
