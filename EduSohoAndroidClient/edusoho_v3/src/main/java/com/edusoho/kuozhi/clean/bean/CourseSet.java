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
    private String summary;
    private CoverBean cover;
    private String ratingNum;
    private String rating;
    private String noteNum;
    private String studentNum;
    private String recommended;
    private String recommendedSeq;
    private String recommendedTime;
    private String orgId;
    private String orgCode;
    private String discountId;
    private String discount;
    private String hitNum;
    private String maxRate;
    private String materialNum;
    private String parentId;
    private String locked;
    private String maxCoursePrice;
    private String minCoursePrice;
    private CreatorBean creator;
    private String createdTime;
    private String updatedTime;
    private List<?> tags;
    private List<?> goals;
    private List<?> audiences;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSerializeMode() {
        return serializeMode;
    }

    public void setSerializeMode(String serializeMode) {
        this.serializeMode = serializeMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public CoverBean getCover() {
        return cover;
    }

    public void setCover(CoverBean cover) {
        this.cover = cover;
    }

    public String getRatingNum() {
        return ratingNum;
    }

    public void setRatingNum(String ratingNum) {
        this.ratingNum = ratingNum;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getNoteNum() {
        return noteNum;
    }

    public void setNoteNum(String noteNum) {
        this.noteNum = noteNum;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getRecommended() {
        return recommended;
    }

    public void setRecommended(String recommended) {
        this.recommended = recommended;
    }

    public String getRecommendedSeq() {
        return recommendedSeq;
    }

    public void setRecommendedSeq(String recommendedSeq) {
        this.recommendedSeq = recommendedSeq;
    }

    public String getRecommendedTime() {
        return recommendedTime;
    }

    public void setRecommendedTime(String recommendedTime) {
        this.recommendedTime = recommendedTime;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getHitNum() {
        return hitNum;
    }

    public void setHitNum(String hitNum) {
        this.hitNum = hitNum;
    }

    public String getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(String maxRate) {
        this.maxRate = maxRate;
    }

    public String getMaterialNum() {
        return materialNum;
    }

    public void setMaterialNum(String materialNum) {
        this.materialNum = materialNum;
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

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getMaxCoursePrice() {
        return maxCoursePrice;
    }

    public void setMaxCoursePrice(String maxCoursePrice) {
        this.maxCoursePrice = maxCoursePrice;
    }

    public String getMinCoursePrice() {
        return minCoursePrice;
    }

    public void setMinCoursePrice(String minCoursePrice) {
        this.minCoursePrice = minCoursePrice;
    }

    public CreatorBean getCreator() {
        return creator;
    }

    public void setCreator(CreatorBean creator) {
        this.creator = creator;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
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

    public void setGoals(List<?> goals) {
        this.goals = goals;
    }

    public List<?> getAudiences() {
        return audiences;
    }

    public void setAudiences(List<?> audiences) {
        this.audiences = audiences;
    }

    public static class CoverBean {

        private String large;
        private String middle;
        private String small;

        public String getLarge() {
            return large;
        }

        public void setLarge(String large) {
            this.large = large;
        }

        public String getMiddle() {
            return middle;
        }

        public void setMiddle(String middle) {
            this.middle = middle;
        }

        public String getSmall() {
            return small;
        }

        public void setSmall(String small) {
            this.small = small;
        }
    }

    public static class CreatorBean {

        private String id;
        private String nickname;
        private String title;
        private String smallAvatar;
        private String mediumAvatar;
        private String largeAvatar;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSmallAvatar() {
            return smallAvatar;
        }

        public void setSmallAvatar(String smallAvatar) {
            this.smallAvatar = smallAvatar;
        }

        public String getMediumAvatar() {
            return mediumAvatar;
        }

        public void setMediumAvatar(String mediumAvatar) {
            this.mediumAvatar = mediumAvatar;
        }

        public String getLargeAvatar() {
            return largeAvatar;
        }

        public void setLargeAvatar(String largeAvatar) {
            this.largeAvatar = largeAvatar;
        }
    }
}
