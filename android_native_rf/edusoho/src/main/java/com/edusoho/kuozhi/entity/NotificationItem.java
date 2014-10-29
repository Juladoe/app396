package com.edusoho.kuozhi.entity;

public class NotificationItem {

	public String id;
	public String userId;
    public String createdTime;
    public int isRead;
    public Content content;

    public class Content{
        public String message;
    }
}
