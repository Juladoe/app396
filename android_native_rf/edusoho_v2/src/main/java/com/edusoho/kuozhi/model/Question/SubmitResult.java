package com.edusoho.kuozhi.model.Question;

import java.io.Serializable;

/**
 * Created by hby on 14-9-24.
 */
public class SubmitResult implements Serializable {
    public int id;

    public int courseId;

    public int lessonId;

    public int threadId;

    public int userId;

    public int isElite;

    public String content;

    public Long createdTime;

}
