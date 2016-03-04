package com.edusoho.kuozhi.v3.model.bal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by JesseHuang on 16/3/4.
 */
public class Classroom implements Serializable {

    public int id;
    public String title;
    public String status;
    public Object about;
    public int categoryId;
    public Object description;
    public double price;
    public String vipLevelId;
    public String smallPicture;
    public String middlePicture;
    public String largePicture;
    public String headTeacherId;
    public Object teacherIds;
    public Object assistantIds;
    public String hitNum;
    public String auditorNum;
    public int studentNum;
    public String courseNum;
    public String lessonNum;
    public String threadNum;
    public String noteNum;
    public String postNum;
    public String rating;
    public String ratingNum;
    public String income;
    public String createdTime;
    public Object service;
    @SerializedName("public")
    public String publicX;
    public String recommended;
    public String recommendedSeq;
    public String recommendedTime;
    public String showable;
    public String buyable;
    public String maxRate;
}
