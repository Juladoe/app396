package com.edusoho.kuozhi.v3.entity.discovery;

import android.text.TextUtils;

import com.edusoho.kuozhi.v3.entity.user.UserEntity;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.util.Const;

import java.io.Serializable;

/**
 * Created by JesseHuang on 16/3/4.
 */
public class DiscoveryCourse extends Course implements DiscoveryCardProperty, Serializable {
    public boolean mEmpty = false;
    public UserEntity teacherInfo = new UserEntity();

    @Override
    public String getPicture() {
        return middlePicture;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public int getStudentNum() {
        return studentNum;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public long getStartTime() {
        if (!TextUtils.isEmpty(createdTime)) {
            return Long.parseLong(createdTime) * 1000;
        } else {
            return System.currentTimeMillis();
        }
    }

    @Override
    public long getEndTime() {
        return getStartTime() + Const.ONE_DAY_MILLISECOND * expiryDay;
    }

    @Override
    public String getTeacherAvatar() {
        return;
    }

    @Override
    public String getTeacherNickname() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return mEmpty;
    }

    public DiscoveryCourse() {
    }

    public DiscoveryCourse(boolean isEmpty) {
        this.mEmpty = isEmpty;
    }
}
