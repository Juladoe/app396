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
        super(xgMessage.getV2CustomContent().getMsgId(),
                xgMessage.getV2CustomContent().getBody().getContent(),
                xgMessage.getV2CustomContent().getFrom().getImage(),
                PushUtil.MsgDeliveryType.UPLOADING,
                xgMessage.getV2CustomContent().getBody().getType(),
                xgMessage.getV2CustomContent().getCreatedTime());

        V2CustomContent v2CustomContent = xgMessage.getV2CustomContent();
        courseId = v2CustomContent.getTo().getId();
        fromId = v2CustomContent.getFrom().getId();
        nickname = v2CustomContent.getFrom().getNickname();
        belongId = EdusohoApp.app.loginUser.id;
        type = v2CustomContent.getBody().getType();
    }
}
