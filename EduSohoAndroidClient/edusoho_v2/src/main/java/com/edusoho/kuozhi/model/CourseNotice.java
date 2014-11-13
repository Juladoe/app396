package com.edusoho.kuozhi.model;

/**
 * Created by onewoman on 14-11-10.
 */
public class CourseNotice {
    public int id;
    public String content;
    public String createdTime;

    @Override
    public String toString() {
        return "CourseNotice{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdTime='" + createdTime + '\'' +
                '}';
    }
}
