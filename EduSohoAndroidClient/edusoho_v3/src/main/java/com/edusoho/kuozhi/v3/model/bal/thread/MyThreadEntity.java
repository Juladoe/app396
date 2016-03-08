package com.edusoho.kuozhi.v3.model.bal.thread;

/**
 * Created by melomelon on 16/3/7.
 */
public class MyThreadEntity {
    private String id;
    private String threadId;
    private String courseId;
    private String courseTitle;
    private String smallPicture;
    private String middlePicture;
    private String lagerPicture;
    private String title;
    private String content;

    public String getId() {
        return id;
    }

    public String getThreadId() {
        return threadId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getSmallPicture() {
        return smallPicture;
    }

    public String getMiddlePicture() {
        return middlePicture;
    }

    public String getLagerPicture() {
        return lagerPicture;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getType() {
        return type;
    }

    private String createdTime;
    private String type;

}
