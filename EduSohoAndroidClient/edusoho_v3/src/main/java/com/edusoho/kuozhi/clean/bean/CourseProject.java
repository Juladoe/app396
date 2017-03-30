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
    public String learnMode;
    public String expiryMode;
    public String expiryDays;
    public Object expiryStartDate;
    public Object expiryEndDate;
    public String summary;
    public String isDefault;
    public String maxStudentNum;
    public String status;
    public String isFree;
    public String price;
    public String vipLevelId;
    public String buyable;
    public String tryLookable;
    public String tryLookLength;
    public String watchLimit;
    public String taskNum;
    public String studentNum;
    public String parentId;
    public String publishedTaskNum;
    public String createdTime;
    public String updatedTime;
    public CreatorBean creator;
    public List<?> goals;
    public List<String> audiences;
    public List<String> services;
    public List<TeachersBean> teachers;

    public static class CreatorBean {
        public String id;
        public String nickname;
        public String title;
    }

    public static class TeachersBean {

        public String id;
        public String nickname;
        public String title;
    }
}
