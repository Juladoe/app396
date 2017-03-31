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
    public List<String> audiences;
    public String rating;
    public String price;
    public String originPrice;
    public String vipLevelId;
    public int publishedTaskNum;
    public List<Service> services;
    public List<Teacher> teachers;

    //    public List<String> services;
//    public List<TeachersBean> teachers;
//
    public static class Service implements Serializable {
        private String short_name;
        private String full_name;
        private String summary;
    }

    public static class Teacher implements Serializable {
        public String id;
        public String nickname;
        public String title;
    }
}
