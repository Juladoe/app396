package com.edusoho.kuozhi.v3.model.bal.push;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.reflect.TypeToken;
import com.tencent.android.tpush.XGPushTextMessage;

import java.io.Serializable;

/**
 * Created by JesseHuang on 15/7/2.
 * 动态主页listview数据对象
 */
public class New implements Serializable {
    public int id;
    public int fromId;
    public String title;
    public String content;
    public int createdTime;

    /**
     * 1.校友头像2.教师头像3.课程头像（图片需在本地做缓存，以便离线能显示）
     */
    public String imgUrl;

    /**
     * 1.friend 2.teacher 3.course
     */
    public String type;
    public int unread;

    public int belongId;
    public int isTop = 0;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(int createdTime) {
        this.createdTime = createdTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }


    public int getBelongId() {
        return belongId;
    }

    public void setBelongId(int belongId) {
        this.belongId = belongId;
    }

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    public New() {

    }

    public New(XGPushTextMessage message) {
        //JSONObject jsonObject = new JSONObject(message.getCustomContent());
        CustomContent customContent = EdusohoApp.app.parseJsonValue(message.getCustomContent(), new TypeToken<CustomContent>() {
        });
        fromId = customContent.getFromId();
        title = message.getTitle();
        if (customContent.getTypeMsg().equals(Chat.FileType.TEXT.toString().toLowerCase())) {
            content = message.getContent();
        } else {
            content = "[图片]";
        }
        createdTime = customContent.getCreatedTime();
        imgUrl = customContent.getImgUrl();
        //newModel.setUnread();
        type = customContent.getTypeObject();
        belongId = EdusohoApp.app.loginUser.id;
    }
}
