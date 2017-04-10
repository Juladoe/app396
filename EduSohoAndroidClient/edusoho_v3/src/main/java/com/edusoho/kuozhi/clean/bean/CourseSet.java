package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JesseHuang on 2017/3/27.
 */

public class CourseSet implements Serializable {

    private String id;
    private String type;
    private String title;
    private String subtitle;
    private String serializeMode;
    private String status;
    public String summary;
    public Cover cover;
    private String ratingNum;
    private float rating;
    private String noteNum;
    private int studentNum;
    private String recommended;
    private String recommendedSeq;
    private String recommendedTime;
    private String orgId;
    private String orgCode;
    private int discountId;
    private float discount;
    private String hitNum;
    private String maxRate;
    private String materialNum;
    private String parentId;
    private String locked;
    private float maxCoursePrice;
    private float minCoursePrice;
    private CreatorBean creator;
    private String createdTime;
    private String updatedTime;
    private List<?> tags;
    private List<?> goals;
    private String[] audiences;
    private List<CreatorBean> teachers;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getSerializeMode() {
        return serializeMode;
    }

    public String getStatus() {
        return status;
    }

    public String getSummary() {
        return summary;
    }

    public Cover getCover() {
        return cover;
    }

    public String getRatingNum() {
        return ratingNum;
    }

    public float getRating() {
        return rating;
    }

    public String getNoteNum() {
        return noteNum;
    }

    public int getStudentNum() {
        return studentNum;
    }

    public String getRecommended() {
        return recommended;
    }

    public String getRecommendedSeq() {
        return recommendedSeq;
    }

    public String getRecommendedTime() {
        return recommendedTime;
    }


    public String getOrgId() {
        return orgId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public int getDiscountId() {
        return discountId;
    }

    public float getDiscount() {
        return discount;
    }

    public String getHitNum() {
        return hitNum;
    }

    public String getMaxRate() {
        return maxRate;
    }

    public String getMaterialNum() {
        return materialNum;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getLocked() {
        return locked;
    }

    public float getMaxCoursePrice() {
        return maxCoursePrice;
    }

    public float getMinCoursePrice() {
        return minCoursePrice;
    }

    public CreatorBean getCreator() {
        return creator;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public List<?> getTags() {
        return tags;
    }

    public void setTags(List<?> tags) {
        this.tags = tags;
    }

    public List<?> getGoals() {
        return goals;
    }

    public String[] getAudiences() {
        return audiences;
    }

    public List<CreatorBean> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<CreatorBean> teachers) {
        this.teachers = teachers;
    }

    public static class CreatorBean {

        private String id;
        private String nickname;
        private String title;
        private String smallAvatar;

        public String getId() {
            return id;
        }

        public String getNickname() {
            return nickname;
        }

        public String getTitle() {
            return title;
        }

        public String getSmallAvatar() {
            return smallAvatar;
        }
    }

    public static class Cover implements Serializable {
        public String large;
        public String middle;
        public String small;
    }
}
