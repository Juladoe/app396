package com.edusohoapp.app.model;

/**
 * Created by howzhi on 14-5-25.
 */
public class Notify {
    public int id;
    public int userId;
    public String createdTime;
    public int isRead;
    public Content content;

    public class Content{
        public int threadId;
        public int threadUserId;
        public String threadUserNickname;
        public String threadTitle;
        public String threadType;
        public int courseId;
        public String courseTitle;
        public String message;
    }

    public enum NotifyEnum {
        QUESTION, EMPTY;

        public static NotifyEnum cover(String name)
        {
            NotifyEnum item = EMPTY;
            try {
                item = valueOf(name.toUpperCase());
            } catch (Exception e) {
                return EMPTY;
            }
            return item;
        }
    }
}
