package com.edusoho.kuozhi.clean.bean;

import com.edusoho.kuozhi.clean.bean.innerbean.Cover;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/3/26.
 * 教学计划
 */

public class CourseProject implements Serializable {

    public int id;
    public String title;
    public String status;
    public String summary;
    public int studentNum;
    public String[] audiences;
    public String rating;
    public float price;
    public float originPrice;
    public String learnMode;
    public String isFree;
    public int taskNum;
    public String buyable;
    public int vipLevelId;
    public int publishedTaskNum;
    public Access access;
    public Service[] services;
    public Teacher[] teachers;
    public SimpleCourseSet courseSet;
    public LearningExpiryDate learningExpiryDate;

    public static class Access{
        public String code;
    }

    public static class Service implements Serializable {
        public String shortName;
        public String fullName;
        public String summary;
    }

    public static class SimpleCourseSet implements Serializable {
        public int id;
        public String type;
        public String title;
        public String subtitle;
        public Cover cover;
        public int studentNum;
        public String discount;
        public String maxCoursePrice;
        public String minCoursePrice;
    }

    public static class LearningExpiryDate implements Serializable {
        public String expiryMode;
        public String expiryStartDate;
        public String expiryEndDate;
        public String expiryDays;
        public boolean expired;
    }

    public static enum CourseLearnMode {
        FREE, LOCK
    }

    public static enum CourseStatus {
        CLOSED, PUBLISHED, DRAFT
    }
}
