package com.edusoho.kuozhi.v3.model.bal.push;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.PushUtil;

import java.util.HashMap;

/**
 * Created by JesseHuang on 15/12/14.
 */
public class CourseDiscussEntity extends BaseMsgEntity {
    public int discussId;
    public int courseId;
    public int fromId;
    public String nickname;
    public int belongId;
    public String upyunMediaPutUrl;
    public String upyunMediaGetUrl;
    public HashMap<String, String> headers;

    public CourseDiscussEntity() {
    }

    public CourseDiscussEntity(int id, int courseId, int fromId, String nickname, String headImgUrl, String content, int belongId,
                               String type, int delivery, int createdTime) {
        super(id, content, headImgUrl, delivery, type, createdTime);
        this.courseId = courseId;
        this.fromId = fromId;
        this.nickname = nickname;
        this.belongId = belongId;
        this.type = type;
    }

    public CourseDiscussEntity(WrapperXGPushTextMessage xgMessage) {
        this(xgMessage.getV2CustomContent());
    }

    public CourseDiscussEntity(V2CustomContent v2CustomContent) {
        super(v2CustomContent.getMsgId(),
                v2CustomContent.getBody().getContent(),
                v2CustomContent.getFrom().getImage(),
                PushUtil.MsgDeliveryType.UPLOADING,
                v2CustomContent.getBody().getType(),
                v2CustomContent.getCreatedTime());

        courseId = v2CustomContent.getTo().getId();
        fromId = v2CustomContent.getFrom().getId();
        nickname = v2CustomContent.getFrom().getNickname();
        belongId = EdusohoApp.app.loginUser.id;
        type = v2CustomContent.getBody().getType();
    }
}
