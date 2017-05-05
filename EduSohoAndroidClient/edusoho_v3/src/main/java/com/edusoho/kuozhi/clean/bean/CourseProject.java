package com.edusoho.kuozhi.clean.bean;

import com.edusoho.kuozhi.clean.bean.innerbean.Access;
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
    public int isFree;
    public int taskNum;
    public String buyable;
    public int vipLevelId;
    public int publishedTaskNum;
    public int enableFinish;
    public Service[] services;
    public Teacher[] teachers;
    public SimpleCourseSet courseSet;
    public LearningExpiryDate learningExpiryDate;
    public Access access;

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

    public enum LearnMode {
        FREEMODE("freeMode"), LOCKMODE("lockMode");

        private String mName;

        LearnMode(String name) {
            this.mName = name;
        }

        public String getName() {
            return mName;
        }

        public static LearnMode getMode(String name) {
            for (LearnMode mode : values()) {
                if (mode.getName().equals(name))
                    return mode;
            }
            return null;
        }
    }

    public enum ExpiryMode {
        FOREVER("forever"), END_DATE("end_date"), DAYS("days"), DATE("date");

        private String mName;

        ExpiryMode(String name) {
            this.mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }
}
