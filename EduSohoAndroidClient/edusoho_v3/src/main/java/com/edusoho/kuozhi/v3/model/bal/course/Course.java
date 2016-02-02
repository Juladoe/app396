package com.edusoho.kuozhi.v3.model.bal.course;

import com.edusoho.kuozhi.v3.model.bal.Teacher;

import java.io.Serializable;

/**
 * Created by JesseHuang on 15/6/14.
 */
public class Course implements Serializable {
    public String title;
    public String studentNum;
    public double rating;
    public String smallPicture;
    public double price;
    public int id;
    public int parentId;
    public String subtitle;
    public int expiryDay;
    public String showStudentNumType;
    public String income;
    public String status;
    public int lessonNum;
    public int giveCredit;
    public int maxStudentNum;
    public String ratingNum;
    public String categoryId;
    public String serializeMode;
    public String[] tags;
    public String middlePicture;
    public String largePicture;
    public String about;
    public String[] goals;
    public String[] audiences;
    public String recommended;
    public String recommendedSeq;
    public String recommendedTime;
    public String locationId;
    public String address;
    public String hitNum;
    public String userId;
    public int vipLevelId;
    public String createdTime;
    public Teacher[] teachers;
    public String type;
}
