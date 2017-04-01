package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DF on 2017/4/1.
 */

public class CourseStudyPlan implements Serializable{

    private String id;
    private String courseSetId;
    private String title;
    private String learnMode;
    private String expiryMode;
    private String expiryDays;
    private Object expiryStartDate;
    private Object expiryEndDate;
    private String summary;
    private String isDefault;
    private String maxStudentNum;
    private String status;
    private String isFree;
    private String price;
    private String vipLevelId;
    private String buyable;
    private String tryLookable;
    private String tryLookLength;
    private String watchLimit;
    private String taskNum;
    private String studentNum;
    private String parentId;
    private String ratingNum;
    private String rating;
    private String originPrice;
    private String publishedTaskNum;
    private List<ServicesBean> services;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseSetId() {
        return courseSetId;
    }

    public String getTitle() {
        return title;
    }

    public String getLearnMode() {
        return learnMode;
    }

    public String getExpiryMode() {
        return expiryMode;
    }

    public String getExpiryDays() {
        return expiryDays;
    }

    public Object getExpiryStartDate() {
        return expiryStartDate;
    }

    public Object getExpiryEndDate() {
        return expiryEndDate;
    }

    public String getSummary() {
        return summary;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public String getMaxStudentNum() {
        return maxStudentNum;
    }

    public String getStatus() {
        return status;
    }

    public String getIsFree() {
        return isFree;
    }

    public String getPrice() {
        return price;
    }

    public String getVipLevelId() {
        return vipLevelId;
    }

    public String getBuyable() {
        return buyable;
    }

    public String getTryLookable() {
        return tryLookable;
    }

    public String getTryLookLength() {
        return tryLookLength;
    }

    public String getWatchLimit() {
        return watchLimit;
    }

    public String getTaskNum() {
        return taskNum;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public String getParentId() {
        return parentId;
    }

    public String getRatingNum() {
        return ratingNum;
    }

    public String getRating() {
        return rating;
    }

    public String getOriginPrice() {
        return originPrice;
    }

    public String getPublishedTaskNum() {
        return publishedTaskNum;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public List<?> getGoals() {
        return goals;
    }

    public List<String> getAudiences() {
        return audiences;
    }

    public List<ServicesBean> getServices() {
        return services;
    }

    public static class ServicesBean {

        private String code;
        private String short_name;
        private String full_name;
        private String summary;
        private int active;

        public String getCode() {
            return code;
        }

        public String getShort_name() {
            return short_name;
        }

        public String getFull_name() {
            return full_name;
        }

        public String getSummary() {
            return summary;
        }

        public int getActive() {
            return active;
        }
    }

}
