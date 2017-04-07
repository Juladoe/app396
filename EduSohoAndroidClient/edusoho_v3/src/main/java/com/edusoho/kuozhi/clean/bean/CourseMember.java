package com.edusoho.kuozhi.clean.bean;

import com.edusoho.kuozhi.v3.model.bal.User;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/4/1.
 */

public class CourseMember implements Serializable {
    public String id;
    public String courseId;
    public User user;
}
