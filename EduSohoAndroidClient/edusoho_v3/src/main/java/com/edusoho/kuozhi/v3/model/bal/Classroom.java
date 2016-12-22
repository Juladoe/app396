package com.edusoho.kuozhi.v3.model.bal;

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
    public String middlePicture;
    public String largePicture;
    public int studentNum;
    public String postNum;
    public String rating;
    public String createdTime;
    public Object service;
    public Teacher[] teachers;
    public String convNo;

    public String getLargePicture() {
        int schemIndex = largePicture.lastIndexOf("http://");
        if (schemIndex != -1) {
            return largePicture.substring(schemIndex);
        }
        return largePicture;
    }
}
