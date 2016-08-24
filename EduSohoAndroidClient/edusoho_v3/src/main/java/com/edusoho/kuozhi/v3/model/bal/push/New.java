package com.edusoho.kuozhi.v3.model.bal.push;

import android.text.TextUtils;

import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.UtilFactory;
import com.edusoho.kuozhi.v3.model.bal.article.ArticleMessageBody;
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

    public void setContent(MessageBody messageBody) {
        String type = messageBody.getType();
        String body = messageBody.getBody();
        switch (type) {
            case PushUtil.ChatMsgType.AUDIO:
                content = String.format("[%s]", Const.MEDIA_AUDIO);
                break;
            case PushUtil.ChatMsgType.IMAGE:
                content = String.format("[%s]", Const.MEDIA_IMAGE);
                break;
            case PushUtil.ChatMsgType.MULTI:
                RedirectBody redirectBody = getUtilFactory().getJsonParser().fromJson(body, RedirectBody.class);
                content = redirectBody.content;
                break;
            case PushUtil.ChatMsgType.PUSH:
                content = handlePushMessageBody(messageBody);
                break;
            default:
                content = body;
        }
        this.content = TextUtils.isEmpty(content) ? "" : AppUtil.coverCourseAbout(content);
    }

    private String handlePushMessageBody(MessageBody messageBody) {
        String fromType = messageBody.getSource().getType();
        switch (fromType) {
            case PushUtil.ArticleType.TYPE:
                ArticleMessageBody articleMessageBody = getUtilFactory().getJsonParser().
                        fromJson(messageBody.getBody(), ArticleMessageBody.class);
                return articleMessageBody.getContent();

        }

        return "";
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
        setContent(messageBody);
        createdTime = messageBody.getCreatedTime();
        type = messageBody.getSource().getType();
        title = getTitleNameByType(messageBody);
    }

    private int getFromIdByType(MessageBody messageBody) {
        type = messageBody.getDestination().getType();
        if (TextUtils.isEmpty(type)) {
            return 0;
        }
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
        String dType = messageBody.getDestination().getType();
        if (TextUtils.isEmpty(dType)) {
            return "";
        }
        switch (dType) {
            case Destination.USER:
                return messageBody.getSource().getNickname();
            case Destination.COURSE:
            case Destination.CLASSROOM:
                return messageBody.getDestination().getNickname();
            case Destination.ARTICLE:
                return "资讯";
        }

        return "";
    }

    public New(ConvEntity convEntity) {
        id = convEntity.getId();
        convNo = convEntity.getConvNo();
        fromId = convEntity.getTargetId();
        MessageBody messageBody = new MessageBody(convEntity.getLaterMsg());

        setContent(messageBody);
        unread = convEntity.getUnRead();
        title = convEntity.getTargetName();
        createdTime = convEntity.getUpdatedTime();
        imgUrl = convEntity.getAvatar();
        type = convEntity.getType() == null ? "" : convEntity.getType();
    }
}
