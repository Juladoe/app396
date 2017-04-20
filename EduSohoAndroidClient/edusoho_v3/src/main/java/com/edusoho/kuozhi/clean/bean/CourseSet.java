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
    public String status;
    public String summary;
    public Cover cover;
    public float rating;
    public int studentNum;
    public int discountId;
    public float discount;
    public String parentId;
    public String locked;
    public float maxCoursePrice;
    public float minCoursePrice;
    public Creator creator;

    public String createdTime;
    public String updatedTime;
    public List<?> tags;
    public String[] audiences;
    public List<Creator> teachers;

    public static class Creator implements Serializable {

        public String id;
        public String nickname;
        public String title;
        public String smallAvatar;
    }
}
