package com.edusoho.kuozhi.v3.model.bal.push;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.UtilFactory;
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

    public int sendUserId;

    public int belongId;
    public int isTop = 0;
    public int parentId;

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

    public int getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(int sendUserId) {
        this.sendUserId = sendUserId;
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

    public New(OffLineMsgEntity offlineMsgModel) {
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

        belongId = v2CustomContent.getTo().getId();
    }

    public New(Chat chat) {
        fromId = chat.fromId;
        title = chat.nickname;
        createdTime = chat.createdTime;
        imgUrl = chat.headImgUrl;

        CustomContent customContent = getUtilFactory().getJsonParser().fromJson(
                chat.custom, CustomContent.class
        );
        type = customContent.getTypeBusiness();
        if (customContent.getTypeMsg().equals(PushUtil.ChatMsgType.TEXT)) {
            content = chat.content;
        } else if (customContent.getTypeMsg().equals(PushUtil.ChatMsgType.IMAGE)) {
            content = String.format("[%s]", Const.MEDIA_IMAGE);
        } else if (customContent.getTypeMsg().equals(PushUtil.ChatMsgType.AUDIO)) {
            content = String.format("[%s]", Const.MEDIA_AUDIO);
        }
        belongId = chat.toId;
    }

    protected UtilFactory getUtilFactory() {
        return FactoryManager.getInstance().create(UtilFactory.class);
    }

    public New(WrapperXGPushTextMessage message)
    {
        this(message.getV2CustomContent());
    }

    public New(V2CustomContent v2CustomContent) {
        //新格式
        fromId = v2CustomContent.getFrom().getId();
        title = v2CustomContent.getFrom().getNickname();
        switch (v2CustomContent.getBody().getType()) {
            case PushUtil.ChatMsgType.AUDIO:
                content = String.format("[%s]", Const.MEDIA_AUDIO);
                break;
            case PushUtil.ChatMsgType.IMAGE:
                content = String.format("[%s]", Const.MEDIA_IMAGE);
                break;
            case PushUtil.ChatMsgType.MULTI:
                RedirectBody body = EdusohoApp.app.parseJsonValue(v2CustomContent.getBody().getContent(), new TypeToken<RedirectBody>() {
                });
                content = body.content;
                break;
            default:
                content = v2CustomContent.getBody().getContent();
        }
        createdTime = v2CustomContent.getCreatedTime();
        imgUrl = v2CustomContent.getFrom().getImage();
        type = v2CustomContent.getFrom().getType();
        belongId = v2CustomContent.getTo().getId();
    }
}
