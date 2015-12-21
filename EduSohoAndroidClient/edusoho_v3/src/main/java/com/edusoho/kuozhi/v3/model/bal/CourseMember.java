package com.edusoho.kuozhi.v3.model.bal;

import java.io.Serializable;

/**
 * Created by JesseHuang on 15/12/11.
 */
public class CourseMember implements Serializable {

    public int id;
    public int courseId;
    public int classroomId;
    public String joinedType;
    public int orderId;
    public String deadline;
    public int levelId;
    public int learnedNum;
    public String credit;
    public int noteNum;
    public String noteLastUpdateTime;
    public String isLearned;
    public String seq;
    public String remark;
    public String isVisible;
    public String role;
    public String locked;
    public String deadlineNotified;
    public String createdTime;

    public UserEntity user;

    public static class UserEntity {
        public String id;
        public String nickname;
        public String title;
        public String avatar;
    }
}
