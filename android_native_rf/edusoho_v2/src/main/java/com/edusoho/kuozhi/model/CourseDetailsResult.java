package com.edusoho.kuozhi.model;

import java.io.Serializable;

/**
 * Created by howzhi on 14-8-26.
 */
public class CourseDetailsResult implements Serializable {
    public Course course;
    public boolean userIsStudent;
    public boolean userFavorited;
    public Object member;
    public Vip vip;
    public VipLevel[] vipLevels;
}
