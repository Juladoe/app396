package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DF on 2017/4/1.
 */

public class CourseStudyPlan implements Serializable{

    public int id;
    public String courseSetId;
    public String title;
    public String learnMode;
    public String expiryMode;
    public String expiryDays;
    public Object expiryStartDate;
    public Object expiryEndDate;
    public String maxStudentNum;
    public String status;
    public String isFree;
    public float price;
    public int vipLevelId;
    public String buyable;
    public int taskNum;
    public int studentNum;
    public String parentId;
    public String ratingNum;
    public String rating;
    public String originPrice;
    public String publishedTaskNum;
    public List<ServicesBean> services;

    public static class ServicesBean implements Serializable{

        public String code;
        public String short_name;
        public String full_name;
        public String summary;
        public int active;
    }

}
