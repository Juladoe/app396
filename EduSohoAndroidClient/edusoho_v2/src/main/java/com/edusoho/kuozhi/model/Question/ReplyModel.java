package com.edusoho.kuozhi.model.Question;

import com.edusoho.kuozhi.model.User;

import java.io.Serializable;

/**
 * Created by hby on 14-9-17.
 * 回复实体类
 */
public class ReplyModel implements Serializable {
    public int id;
    public int courseId;
    public int lessonId;
    public int threadId;
    public int userId;
    public int isElite;
    public String content;
    public String createdTime;
    public User user;
    public String following;
    public String follower;

}
