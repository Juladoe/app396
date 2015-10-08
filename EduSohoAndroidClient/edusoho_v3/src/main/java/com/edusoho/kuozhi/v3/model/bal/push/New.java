package com.edusoho.kuozhi.v3.model.bal.push;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.google.gson.reflect.TypeToken;

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

    public New(OfflineMsgEntity offlineMsgModel) {
        V2CustomContent v2CustomContent = offlineMsgModel.getCustom();
        fromId = v2CustomContent.getFrom().getId();
        title = offlineMsgModel.getTitle();
        createdTime = v2CustomContent.getCreatedTime();
        imgUrl = v2CustomContent.getFrom().getImage();
        type = v2CustomContent.getFrom().getType();
        switch (v2CustomContent.getBody().getType()) {
            case PushUtil.ChatMsgType.TEXT:
                content = offlineMsgModel.getContent();
                break;
            case PushUtil.ChatMsgType.IMAGE:
                content = String.format("[%s]", Const.MEDIA_IMAGE);
                break;
            case PushUtil.ChatMsgType.AUDIO:
                content = String.format("[%s]", Const.MEDIA_AUDIO);
                break;
            case PushUtil.CourseType.TESTPAPER_REVIEWED:
                content = String.format("【%s】%s", type, offlineMsgModel.getContent());
                break;
        }

        belongId = EdusohoApp.app.loginUser.id;
    }

    public New(Chat chat) {
        fromId = chat.fromId;
        title = chat.nickName;
        createdTime = chat.createdTime;
        imgUrl = chat.headimgurl;
        CustomContent customContent = chat.getCustomContent();
        type = chat.getCustomContent().getTypeBusiness();
        if (customContent.getTypeMsg().equals(Chat.FileType.TEXT.getName())) {
            content = chat.content;
        } else if (customContent.getTypeMsg().equals(Chat.FileType.IMAGE.getName())) {
            content = String.format("[%s]", Const.MEDIA_IMAGE);
        } else if (customContent.getTypeMsg().equals(Chat.FileType.AUDIO.getName())) {
            content = String.format("[%s]", Const.MEDIA_AUDIO);
        }
        belongId = EdusohoApp.app.loginUser.id;
    }

    public New(WrapperXGPushTextMessage message) {
        CustomContent customContent = EdusohoApp.app.parseJsonValue(message.getCustomContentJson(), new TypeToken<CustomContent>() {
        });
        fromId = customContent.getFromId();
        title = message.getTitle();
        if (customContent.getTypeMsg().equals(Chat.FileType.IMAGE.getName())) {
            content = String.format("[%s]", Const.MEDIA_IMAGE);
        } else if (customContent.getTypeMsg().equals(Chat.FileType.AUDIO.getName())) {
            content = String.format("[%s]", Const.MEDIA_AUDIO);
        } else if (customContent.getTypeMsg().equals(Chat.FileType.MULTI.getName())) {
            RedirectBody body = EdusohoApp.app.parseJsonValue(message.getContent(), new TypeToken<RedirectBody>() {
            });
            content = body.content;
        } else {
            content = message.getContent();
        }
        createdTime = customContent.getCreatedTime();
        imgUrl = customContent.getImgUrl();
        //newModel.setUnread();
        type = customContent.getTypeBusiness();
        belongId = EdusohoApp.app.loginUser.id;
    }
}
