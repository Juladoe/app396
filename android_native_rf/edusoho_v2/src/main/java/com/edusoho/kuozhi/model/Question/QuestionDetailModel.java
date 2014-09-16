package com.edusoho.kuozhi.model.Question;

import java.io.Serializable;

/**
 * Created by hby on 14-9-15.
 */
public class QuestionDetailModel implements Serializable {
    public int id;
    public int courseId;
    public int lessonId;
    public int userId;
    public String type;
    public int isStick;
    public int isElite;
    public int isClosed;
    public String title;
    public String content;
    public int postNum;
    public int hitNum;
    public int followNum;
    public int latestPostUserId;
    public String latestPostTime;
    public String createdTime;
    public String courseTitle;
    public QuestionDetailModel[] threads;
}
