package com.edusoho.kuozhi.model;

import java.util.HashMap;

/**
 * Created by howzhi on 14-5-25.
 */
public class Notify {
    public int id;
    public int userId;
    public String createdTime;
    public int isRead;
    public HashMap<String, Object> content;
    public String message;
    public String type;

    public enum NotifyEnum {
        QUESTION, EMPTY;

        public static NotifyEnum cover(String name)
        {
            NotifyEnum item = EMPTY;
            try {
                item = valueOf(name);
            } catch (Exception e) {
                return EMPTY;
            }
            return item;
        }
    }
}
