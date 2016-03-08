package com.edusoho.kuozhi.v3.model.bal;

import com.edusoho.kuozhi.v3.entity.user.UserEntity;

/**
 * Created by JesseHuang on 15/6/3.
 */
public class ChatMessage {
    public int id;
    public int fromId;
    public int toId;
    public String content;
    public String createdTime;
    public UserEntity createdUserEntity;
}
