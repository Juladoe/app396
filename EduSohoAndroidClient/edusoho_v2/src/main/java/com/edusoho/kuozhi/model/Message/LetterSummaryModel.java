package com.edusoho.kuozhi.model.Message;

import com.edusoho.kuozhi.model.User;

import java.io.Serializable;

/**
 * Created by JesseHuang on 14/11/24.
 */
public class LetterSummaryModel implements Serializable {
    public int id;
    public int fromId;
    public int toId;
    public int messageNum;
    public int latestMessageUserId;
    public String latestMessageTime;
    public String latestMessageContent;
    public int unreadNum;
    public String createdTime;
    public User user;
}
