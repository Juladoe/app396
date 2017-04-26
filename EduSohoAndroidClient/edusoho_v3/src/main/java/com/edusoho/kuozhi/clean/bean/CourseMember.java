package com.edusoho.kuozhi.clean.bean;

import com.edusoho.kuozhi.clean.bean.innerbean.Access;
import com.edusoho.kuozhi.clean.bean.innerbean.Avatar;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/4/13.
 */

public class CourseMember implements Serializable {
    public int id;
    public int courseId;
    public String deadline;
    public int levelId;
    public int learnedNum;
    public int noteNum;
    public String noteLastUpdateTime;
    public String isLearned;
    public String finishedTime;
    public String role;
    public String locked;
    public String createdTime;
    public String lastLearnTime;
    public String lastViewTime;
    public int courseSetId;
    public User user;
    public Access access;

    public static class User implements Serializable {
        public String id;
        public String nickname;
        public String title;
        public Avatar avatar;
    }
}
