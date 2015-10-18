package com.edusoho.kuozhi.v3.model.bal.push;

import com.edusoho.kuozhi.v3.EdusohoApp;

/**
 * Created by JesseHuang on 15/10/15.
 */
public class ClassroomDiscussEntity {
    private int id;
    private int classroomId;
    private int fromId;
    private String nickname;
    private String headImgUrl;
    private String content;
    private int belongId;
    private String type;
    private int delivery = 2;
    private int createdTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(int classroomId) {
        this.classroomId = classroomId;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getBelongId() {
        return belongId;
    }

    public void setBelongId(int belongId) {
        this.belongId = belongId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDelivery() {
        return delivery;
    }

    public void setDelivery(int delivery) {
        this.delivery = delivery;
    }

    public int getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(int createdTime) {
        this.createdTime = createdTime;
    }

    public ClassroomDiscussEntity() {
    }

    public ClassroomDiscussEntity(int id, int classroomId, int fromId, String nickname, String headImgUrl, String content, int belongId,
                                  String type, int delivery, int createdTime) {
        this.id = id;
        this.classroomId = classroomId;
        this.fromId = fromId;
        this.nickname = nickname;
        this.headImgUrl = headImgUrl;
        this.content = content;
        this.belongId = belongId;
        this.type = type;
        this.delivery = delivery;
        this.createdTime = createdTime;
    }

    public ClassroomDiscussEntity(WrapperXGPushTextMessage xgMessage) {
        V2CustomContent v2CustomContent = xgMessage.getV2CustomContent();
        id = v2CustomContent.getMsgId();
        classroomId = v2CustomContent.getTo().getId();
        fromId = v2CustomContent.getFrom().getId();
        nickname = v2CustomContent.getFrom().getNickname();
        headImgUrl = v2CustomContent.getFrom().getImage();
        content = v2CustomContent.getBody().getContent();
        belongId = EdusohoApp.app.loginUser.id;
        type = v2CustomContent.getBody().getType();
        createdTime = v2CustomContent.getCreatedTime();
    }

}
