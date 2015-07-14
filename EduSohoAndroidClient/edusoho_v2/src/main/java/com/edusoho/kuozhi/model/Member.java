package com.edusoho.kuozhi.model;

import java.io.Serializable;

/**
 * Created by howzhi on 14-9-15.
 */
public class Member implements Serializable {

    public int id;
    public int courseId;
    public int userId;
    public int orderId;
    public long deadline;
    public int classroomId;
    public String levelId;
    public String learnedNum;
    public String credit;
    public String noteNum;
    public String noteLastUpdateTime;
    public String isLearned;
    public String seq;
    public String remark;
    public int isVisible;
    public Role role;
    public int locked;
    public int createdTime;

    public enum Role
    {
        teacher, student, admin;
    }
}
