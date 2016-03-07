package com.edusoho.kuozhi.v3.entity.discovery;

/**
 * Created by JesseHuang on 16/3/4.
 */
public interface DiscoveryCardProperty {

    String getPicture();

    String getTitle();

    double getPrice();

    int getStudentNum();

    String getType();

    long getStartTime();

    long getEndTime();

    String getTeacherAvatar();

    String getTeacherNickname();

    boolean isEmpty();
}
