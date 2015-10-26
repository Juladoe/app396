package com.edusoho.kuozhi.v3.model.bal.push;

import android.text.TextUtils;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.google.gson.reflect.TypeToken;

/**
 * Created by JesseHuang on 15/7/2.
 */
public class Chat extends BaseMsgEntity {
    public int chatId;
    public int userId;
    public int fromId;
    public int toId;
    public String nickname;

    public Direct direct;

    public String custom;

    public Direct getDirect() {
        if (fromId != 0 && EdusohoApp.app.loginUser != null) {
            return Direct.getDirect(fromId == EdusohoApp.app.loginUser.id);
        }
        return direct;
    }

    public void setDirect(Direct direct) {
        this.direct = direct;
    }


    public Chat() {

    }

    public Chat(int fromId, int toId, String nickname, String headImgUrl, String content, String type, int createdTime) {
        super(0, content, headImgUrl, 2, type, createdTime);
        this.fromId = fromId;
        this.toId = toId;
        this.nickname = nickname;
        this.direct = Direct.getDirect(this.fromId == EdusohoApp.app.loginUser.id);
    }

    public Chat(int chatId, int id, int fromId, int toId, String nickname, String headImgUrl, String content, String type, int delivery, int createdTime) {
        super(id, content, headImgUrl, delivery, type, createdTime);
        this.chatId = chatId;
        this.fromId = fromId;
        this.toId = toId;
        this.nickname = nickname;
        this.direct = Direct.getDirect(this.fromId == EdusohoApp.app.loginUser.id);
    }

    public Chat(WrapperXGPushTextMessage message) {
        CustomContent customContent = EdusohoApp.app.parseJsonValue(message.getCustomContentJson(), new TypeToken<CustomContent>() {
        });
        id = customContent.getId();
        fromId = customContent.getFromId();
        toId = EdusohoApp.app.loginUser.id;
        nickname = message.getTitle();
        headImgUrl = customContent.getImgUrl();
        content = message.getContent();
        type = customContent.getTypeMsg();
        createdTime = customContent.getCreatedTime();
        direct = Direct.getDirect(fromId == EdusohoApp.app.loginUser.id);
        if (type == PushUtil.ChatMsgType.TEXT) {
            delivery = PushUtil.MsgDeliveryType.SUCCESS;
        }
    }

    public Chat(OffLineMsgEntity offlineMsgModel) {
        V2CustomContent v2CustomContent = offlineMsgModel.getCustom();
        id = v2CustomContent.getMsgId();
        fromId = v2CustomContent.getFrom().getId();
        toId = EdusohoApp.app.loginUser.id;
        nickname = v2CustomContent.getFrom().getNickname();
        headImgUrl = v2CustomContent.getFrom().getImage();
        content = v2CustomContent.getBody().getContent();
        type = v2CustomContent.getBody().getType();
        createdTime = v2CustomContent.getCreatedTime();
        direct = Direct.getDirect(fromId == EdusohoApp.app.loginUser.id);
    }

    public CustomContent getCustomContent() {
        return TextUtils.isEmpty(custom) ? null : EdusohoApp.app.parseJsonValue(this.custom, new TypeToken<CustomContent>() {
        });
    }

    public enum Direct {
        SEND, RECEIVE;

        public static Direct getDirect(boolean n) {
            if (n) {
                return SEND;
            } else {
                return RECEIVE;
            }
        }
    }
}
