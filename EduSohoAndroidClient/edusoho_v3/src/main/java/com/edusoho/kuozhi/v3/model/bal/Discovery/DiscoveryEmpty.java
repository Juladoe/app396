package com.edusoho.kuozhi.v3.model.bal.Discovery;

/**
 * Created by JesseHuang on 16/3/4.
 */
public class DiscoveryEmpty implements DiscoveryCardProperty {
    @Override
    public String getPicture() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public double getPrice() {
        return 0;
    }

    @Override
    public int getStudentNum() {
        return 0;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getStartTime() {
        return null;
    }

    @Override
    public String getEndTime() {
        return null;
    }

    @Override
    public String getTeacherAvatar() {
        return null;
    }

    @Override
    public String getTeacherNickname() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
