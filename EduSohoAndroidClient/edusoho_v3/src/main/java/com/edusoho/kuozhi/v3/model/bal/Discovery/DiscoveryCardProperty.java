package com.edusoho.kuozhi.v3.model.bal.Discovery;

/**
 * Created by JesseHuang on 16/3/4.
 */
public interface DiscoveryCardProperty {
    String getPicture();

    String getTitle();

    double getPrice();

    int getStudentNum();

    String getType();

    String getStartTime();

    String getEndTime();

    String getTeacherAvatar();

    String getTeacherNickname();

    boolean isEmpty();
}
