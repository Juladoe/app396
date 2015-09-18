package com.edusoho.kuozhi.v3.model.bal.push;

import com.edusoho.kuozhi.v3.EdusohoApp;

/**
 * Created by JesseHuang on 15/9/16.
 */
public class NewsCourseEntity {
    private int id;
    private int courseId;
    private int lessonId;
    private String title;
    private String content;
    private String fromType;
    private String bodyType;
    private String lessonType;
    private int userId;
    private int createdTime;

    public NewsCourseEntity() {

    }

    public NewsCourseEntity(WrapperXGPushTextMessage xgMessage) {
        V2CustomContent v2CustomContent = xgMessage.getV2CustomContent();
        this.id = v2CustomContent.getMsgId();
        this.courseId = v2CustomContent.getFrom().getId();
        this.lessonId = v2CustomContent.getBody().getLessonId();
        this.title = xgMessage.getTitle();
        this.content = xgMessage.getContent();
        this.fromType = v2CustomContent.getFrom().getType();
        this.bodyType = v2CustomContent.getBody().getType();
        this.lessonType = v2CustomContent.getBody().getLessonType();
        this.userId = EdusohoApp.app.loginUser.id;
        this.createdTime = v2CustomContent.getCreatedTime();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromType() {
        return fromType == null ? "" : fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getBodyType() {
        return bodyType == null ? "" : bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getLessonType() {
        return lessonType == null ? "" : lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(int createdTime) {
        this.createdTime = createdTime;
    }
}
