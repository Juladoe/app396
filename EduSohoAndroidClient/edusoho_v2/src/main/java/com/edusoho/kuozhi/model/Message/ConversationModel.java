package com.edusoho.kuozhi.model.Message;

import java.io.Serializable;

/**
 * Created by JesseHuang on 15/1/14.
 */
public class ConversationModel implements Serializable {
    public int id;
    public int fromId;
    public int toId;
    public int messageNum;
    public int latestMessageUserId;
    public String latestMessageTime;
    public String latestMessageContent;
    public int unreadNum;
    public String createdTime;
    public String fromUserName;
}
