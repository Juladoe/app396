package com.edusoho.kuozhi.v3.model.bal.push;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;

/**
 * Created by JesseHuang on 15/7/2.
 */
public class Chat implements Serializable {
    public int id;
    public int fromId;
    public int toId;
    public String nickName;
    public String headimgurl;
    public String content;
    public String type;
    public int createdTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(int createdTime) {
        this.createdTime = createdTime;
    }

    public Chat() {

    }

    public Chat(WrapperXGPushTextMessage message) {
        CustomContent customContent = EdusohoApp.app.parseJsonValue(message.getCustomContent(), new TypeToken<CustomContent>() {
        });
        id = customContent.id;
        fromId = customContent.fromId;
        toId = EdusohoApp.app.loginUser.id;
        nickName = customContent.nickname;
        headimgurl = customContent.imgUrl;
        content = message.getContent();
        type = customContent.typeMsg;
        createdTime = customContent.createdTime;
    }
}
