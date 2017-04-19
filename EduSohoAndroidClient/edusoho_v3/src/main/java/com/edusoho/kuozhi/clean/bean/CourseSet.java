package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JesseHuang on 2017/3/27.
 */

public class CourseSet implements Serializable {

    public int id;
    public String type;
    public String title;
    public String subtitle;
    public String serializeMode;
    public String status;
    public String summary;
    public Cover cover;
    public String ratingNum;
    public float rating;
    public String noteNum;
    public int studentNum;
    public String recommended;
    public String recommendedSeq;
    public String recommendedTime;
    public String orgId;
    public String orgCode;
    public int discountId;
    public float discount;
    public String hitNum;
    public String maxRate;
    public String materialNum;
    public String parentId;
    public String locked;
    public float maxCoursePrice;
    public float minCoursePrice;
    public CreatorBean creator;
    public String createdTime;
    public String updatedTime;
    public List<?> tags;
    public String[] audiences;
    public List<CreatorBean> teachers;

    public static class CreatorBean implements Serializable {

        public String id;
        public String nickname;
        public String title;
        public String smallAvatar;
    }

    public static class Cover implements Serializable {
        public String large;
        public String middle;
        public String small;
    }
}
