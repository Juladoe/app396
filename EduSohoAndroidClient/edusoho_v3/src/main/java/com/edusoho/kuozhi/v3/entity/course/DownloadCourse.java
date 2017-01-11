package com.edusoho.kuozhi.v3.entity.course;

import android.text.TextUtils;

import com.edusoho.kuozhi.v3.model.bal.course.Course;

/**
 * Created by suju on 17/1/10.
 */

public class DownloadCourse extends Course {

    private String source;

    private int cachedLessonNum;

    private long cachedSize;

    private String targetClassRoomName;

    public int getCachedLessonNum() {
        return cachedLessonNum;
    }

    public void setCachedLessonNum(int cachedLessonNum) {
        this.cachedLessonNum = cachedLessonNum;
    }

    public long getCachedSize() {
        return cachedSize;
    }

    public void setCachedSize(long cachedSize) {
        this.cachedSize = cachedSize;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTargetClassRoomName() {
        return targetClassRoomName;
    }

    public void setTargetClassRoomName(String targetClassRoomName) {
        this.targetClassRoomName = targetClassRoomName;
    }

    public String getPicture() {
        if (TextUtils.isEmpty(middlePicture)) {
            return "";
        }

        if (middlePicture.startsWith("//")) {
            return "http:" + middlePicture;
        }

        return middlePicture;
    }
}
