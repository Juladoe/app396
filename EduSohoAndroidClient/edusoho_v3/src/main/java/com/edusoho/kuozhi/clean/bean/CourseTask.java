package com.edusoho.kuozhi.clean.bean;

import com.edusoho.kuozhi.clean.bean.innerbean.TaskResult;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/3/23.
 * 计划任务
 */

public class CourseTask implements Serializable {
    public int id;
    public int seq;
    public String categoryId;
    public String activityId;
    public String title;
    public int isFree;
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
    public boolean lock;
    public String copyId;
    public String createdUserId;
    public String createdTime;
    public String updatedTime;
    public Activity activity;
    public TaskResult result;

    public static class Activity implements Serializable {
        public String id;
        public String title;
        public Object remark;
        public int mediaId;
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
        /**
         * finish, end
         */
        public String finishType;
        public int finishDetail;
    }

    public String toTaskItemSequence() {
        if (seq != 0 && number != 0) {
            return seq + " - " + number;
        } else {
            return number + "";
        }
    }
}
