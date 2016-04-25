package com.edusoho.kuozhi.v3.model.bal.push;

import com.edusoho.kuozhi.v3.util.PushUtil;

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
        if (fromId != 0 && toId != 0) {
            return Direct.getDirect(fromId == toId);
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
    }

    public Chat(int chatId, int id, int fromId, int toId, String nickname, String headImgUrl, String content, String type, int delivery, int createdTime) {
        super(id, content, headImgUrl, delivery, type, createdTime);
        this.chatId = chatId;
        this.fromId = fromId;
        this.toId = toId;
        this.nickname = nickname;
    }

    public Chat(WrapperXGPushTextMessage wrapperXGPushTextMessage) {
        this(wrapperXGPushTextMessage.getV2CustomContent());
    }

    public Chat(V2CustomContent v2CustomContent) {
        super(v2CustomContent.getMsgId(),
                v2CustomContent.getBody().getContent(),
                v2CustomContent.getFrom().getImage(),
                PushUtil.MsgDeliveryType.UPLOADING,
                v2CustomContent.getBody().getType(),
                v2CustomContent.getCreatedTime());

        id = v2CustomContent.getMsgId();
        fromId = v2CustomContent.getFrom().getId();
        toId = v2CustomContent.getTo().getId();
        nickname = v2CustomContent.getFrom().getNickname();
        headImgUrl = v2CustomContent.getFrom().getImage();
        content = v2CustomContent.getBody().getContent();
        type = v2CustomContent.getBody().getType();
        createdTime = v2CustomContent.getCreatedTime();
        if (type == PushUtil.ChatMsgType.TEXT) {
            delivery = PushUtil.MsgDeliveryType.SUCCESS;
        }
    }

    public Chat(OffLineMsgEntity offlineMsgModel) {
        V2CustomContent v2CustomContent = offlineMsgModel.getCustom();
        id = v2CustomContent.getMsgId();
        fromId = v2CustomContent.getFrom().getId();
        toId = v2CustomContent.getTo().getId();
        nickname = v2CustomContent.getFrom().getNickname();
        headImgUrl = v2CustomContent.getFrom().getImage();
        content = v2CustomContent.getBody().getContent();
        type = v2CustomContent.getBody().getType();
        createdTime = v2CustomContent.getCreatedTime();
        direct = Direct.getDirect(fromId == toId);
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
