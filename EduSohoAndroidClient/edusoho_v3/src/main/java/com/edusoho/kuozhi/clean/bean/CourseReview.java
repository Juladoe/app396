package com.edusoho.kuozhi.clean.bean;

import com.edusoho.kuozhi.clean.bean.innerbean.Avatar;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DF on 2017/3/31.
 */

public class CourseReview implements Serializable {

    public List<DataBean> data;

    public static class DataBean {

        public String content;
        public String rating;
        public String createdTime;
        public UserBean user;
        public CourseBean course;

        public static class UserBean {
            public String nickname;
            public Avatar avatar;

        }

        public static class CourseBean {
            public String title;
        }
    }
}
