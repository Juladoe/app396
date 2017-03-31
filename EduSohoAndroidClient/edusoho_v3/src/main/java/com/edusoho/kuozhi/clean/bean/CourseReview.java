package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DF on 2017/3/31.
 */

public class CourseReview implements Serializable {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public static class DataBean {

        private String content;
        private String rating;
        private String createdTime;
        private UserBean user;
        private CourseBean course;

        public String getContent() {
            return content;
        }

        public String getRating() {
            return rating;
        }

        public String getCreatedTime() {
            return createdTime;
        }

        public UserBean getUser() {
            return user;
        }

        public CourseBean getCourse() {
            return course;
        }

        public static class UserBean {

            private String nickname;
            private String mediumAvatar;

            public String getNickname() {
                return nickname;
            }

            public String getMediumlAvatar() {
                return mediumAvatar;
            }

        }

        public static class CourseBean {

            private String title;

            public String getTitle() {
                return title;
            }

        }
    }
}
