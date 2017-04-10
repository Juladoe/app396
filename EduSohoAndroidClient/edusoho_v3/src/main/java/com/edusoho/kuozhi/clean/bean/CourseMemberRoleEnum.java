package com.edusoho.kuozhi.clean.bean;

/**
 * Created by JesseHuang on 2017/4/7.
 */

public enum CourseMemberRoleEnum {
    STUDENT("student"), TEACHER("teacger");

    private String mName;

    CourseMemberRoleEnum(String name) {
        this.mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }
}
