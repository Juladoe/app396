package com.edusoho.kuozhi.v3.entity;

import java.util.List;

/**
 * Created by DF on 2016/12/21.
 */

public class ClassCatalogue {

    private String id;
    private String title;
    private String subtitle;
    private String status;
    private String buyable;
    private String buyExpiryTime;
    private String type;
    private String maxStudentNum;
    private String price;
    private String originPrice;
    private String coinPrice;
    private String originCoinPrice;
    private String expiryMode;
    private String expiryDay;
    private String showStudentNumType;
    private String serializeMode;
    private String income;
    private String lessonNum;
    private String giveCredit;
    private String rating;
    private String ratingNum;
    private String vipLevelId;
    private String useInClassroom;
    private String singleBuy;
    private String categoryId;
    private String smallPicture;
    private String middlePicture;
    private String largePicture;
    private String about;
    private String recommended;
    private String recommendedSeq;
    private String recommendedTime;
    private String locationId;
    private String parentId;
    private String address;
    private String studentNum;
    private String hitNum;
    private String noteNum;
    private String userId;
    private String deadlineNotify;
    private String daysOfNotifyBeforeDeadline;
    private String watchLimit;
    private String createdTime;
    private String updatedTime;
    private String freeStartTime;
    private String freeEndTime;
    private String discountId;
    private String discount;
    private String approval;
    private String locked;
    private String maxRate;
    private String tryLookable;
    private String tryLookTime;
    private String orgCode;
    private String orgId;
    private String classroom_course_id;
    private String priceType;
    private String coinName;
    private List<?> goals;
    private List<?> audiences;
    private List<TeachersBean> teachers;
    private List<?> tags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSmallPicture() {
        return smallPicture;
    }

    public String getMiddlePicture() {
        return middlePicture;
    }

    public void setMiddlePicture(String middlePicture) {
        this.middlePicture = middlePicture;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getClassroom_course_id() {
        return classroom_course_id;
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

    public List<TeachersBean> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeachersBean> teachers) {
        this.teachers = teachers;
    }

    public List<?> getTags() {
        return tags;
    }

    public void setTags(List<?> tags) {
        this.tags = tags;
    }

    public static class TeachersBean {
        private String id;
        private String nickname;
        private String title;
        private String following;
        private String follower;
        private String avatar;

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

        public String getFollowing() {
            return following;
        }

        public void setFollowing(String following) {
            this.following = following;
        }

        public String getFollower() {
            return follower;
        }

        public void setFollower(String follower) {
            this.follower = follower;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
