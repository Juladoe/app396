package com.edusoho.kuozhi.model.Message;

import com.edusoho.kuozhi.model.User;

import java.io.Serializable;

/**
 * Created by JesseHuang on 14/11/24.
 */
public class LetterModel implements Serializable {
    public int id;
    public int fromId;
    public int toId;
    public String content;
    public String createdTime;
    public User createdUser;
}
