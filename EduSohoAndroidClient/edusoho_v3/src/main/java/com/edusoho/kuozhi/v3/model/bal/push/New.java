package com.edusoho.kuozhi.v3.model.bal.push;

import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.UtilFactory;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;

import java.io.Serializable;

/**
 * Created by JesseHuang on 15/7/2.
 * 动态主页listview数据对象
 */
public class New implements Serializable {

    public int id;
    public int fromId;
    public String convNo;
    public String title;
    public String content;
    public long createdTime;

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

    public void setContent(String type, String content) {
        switch (type) {
            case PushUtil.ChatMsgType.AUDIO:
                content = String.format("[%s]", Const.MEDIA_AUDIO);
                break;
            case PushUtil.ChatMsgType.IMAGE:
                content = String.format("[%s]", Const.MEDIA_IMAGE);
                break;
            case PushUtil.ChatMsgType.MULTI:
                RedirectBody body = getUtilFactory().getJsonParser().fromJson(content, RedirectBody.class);
                content = body.content;
                break;
        }
        this.content = content;
    }

    public long getCreatedTime() {
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

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    public New() {
    }

    protected UtilFactory getUtilFactory() {
        return FactoryManager.getInstance().create(UtilFactory.class);
    }

    public New(MessageEntity messageEntity)
    {
        MessageBody messageBody = new MessageBody(messageEntity);
        convNo = messageEntity.getConvNo();

        fromId = getFromIdByType(messageBody);
        setContent(messageBody.getType(), messageBody.getBody());
        title = messageEntity.getFromName();
        createdTime = messageBody.getCreatedTime();
        type = messageBody.getDestination().getType();
        title = getTitleNameByType(messageBody);
    }

    private int getFromIdByType(MessageBody messageBody) {
        type = messageBody.getDestination().getType();
        switch (type) {
            case Destination.USER:
                return messageBody.getSource().getId();
            case Destination.COURSE:
            case Destination.CLASSROOM:
                return messageBody.getDestination().getId();
        }

        return 0;
    }

    private String getTitleNameByType(MessageBody messageBody) {
        type = messageBody.getDestination().getType();
        switch (type) {
            case Destination.USER:
                return messageBody.getSource().getNickname();
            case Destination.COURSE:
            case Destination.CLASSROOM:
                return messageBody.getDestination().getNickname();
        }

        return "";
    }

    public New(ConvEntity convEntity) {
        id = convEntity.getId();
        convNo = convEntity.getConvNo();
        fromId = convEntity.getTargetId();
        MessageBody messageBody = new MessageBody(convEntity.getLaterMsg());

        setContent(messageBody.getType(), messageBody.getBody());
        unread = convEntity.getUnRead();
        title = convEntity.getTargetName();
        createdTime = convEntity.getUpdatedTime() == 0 ? convEntity.getCreatedTime() : convEntity.getUpdatedTime();
        imgUrl = convEntity.getAvatar();
        type = convEntity.getType() == null ? "" : convEntity.getType();
    }
}
