package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/3/23.
 * 计划任务
 */

public class CourseTask implements Serializable {
    public String id;
    public String courseId;
    public String seq;
    public String categoryId;
    public String activityId;
    public String title;
    public String isFree;
    public String isOptional;
    public String startTime;
    public String endTime;
    public String mode;
    public String status;
    public int number;
    public String type;
    public String mediaSource;
    public String maxOnlineNum;
    public String fromCourseSetId;
    public String length;
    public String copyId;
    public String createdUserId;
    public String createdTime;
    public String updatedTime;
    public Activity activity;

    public static class Activity {
        public String id;
        public String title;
        public Object remark;
        public String mediaId;
        public String mediaType;
        public String content;
        public String length;
        public String fromCourseId;
        public String fromCourseSetId;
        public String fromUserId;
        public String copyId;
        public String startTime;
        public String endTime;
        public String createdTime;
        public String updatedTime;
        public Ext ext;

        public static class Ext {

            public String id;
            public String finishType;
            public String finishDetail;
            public String createdTime;
            public String createdUserId;
            public String updatedTime;
        }
    }
}
