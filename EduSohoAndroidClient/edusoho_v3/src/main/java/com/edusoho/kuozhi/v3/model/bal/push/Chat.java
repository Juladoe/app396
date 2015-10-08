package com.edusoho.kuozhi.v3.model.bal.push;

import android.text.TextUtils;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by JesseHuang on 15/7/2.
 */
public class Chat implements Serializable {
    public int chatId;
    public int id;
    public int userId;
    public int fromId;
    public int toId;
    public String nickName;
    public String headimgurl;
    public String content;
    public String type;
    public int delivery = 2;
    public int createdTime;

    public Direct direct;
    public FileType fileType;

    private String upyunMediaPutUrl;
    private String upyunMediaGetUrl;

    public String custom;

    private HashMap<String, String> headers;

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

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

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(int createdTime) {
        this.createdTime = createdTime;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public String getUpyunMediaPutUrl() {
        return upyunMediaPutUrl;
    }

    public void setUpyunMediaPutUrl(String upyunMediaPutUrl) {
        this.upyunMediaPutUrl = upyunMediaPutUrl;
    }

    public String getUpyunMediaGetUrl() {
        return upyunMediaGetUrl;
    }

    public void setUpyunMediaGetUrl(String upyunMediaGetUrl) {
        this.upyunMediaGetUrl = upyunMediaGetUrl;
    }

    public Direct getDirect() {
        if (fromId != 0 && EdusohoApp.app.loginUser != null) {
            return Direct.getDirect(fromId == EdusohoApp.app.loginUser.id);
        }
        return direct;
    }

    public void setDirect(Direct direct) {
        this.direct = direct;
    }

    public Delivery getDelivery() {
        return Delivery.getDelivery(delivery);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = Delivery.getIndex(delivery);
    }

    public FileType getFileType() {
        if (TextUtils.isEmpty(type)) {
            return FileType.getType(type);
        }
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public Chat() {

    }

    public Chat(int fId, int tId, String name, String url, String content, String t, int cTime) {
        this.fromId = fId;
        this.toId = tId;
        this.nickName = name;
        this.headimgurl = url;
        this.content = content;
        this.type = t;
        this.createdTime = cTime;
        this.direct = Direct.getDirect(fromId == EdusohoApp.app.loginUser.id);
        this.fileType = FileType.getType(type);
    }

    public Chat(int chatId, int id, int fId, int tId, String name, String url, String content, String t, int d, int cTime) {
        this.chatId = chatId;
        this.id = id;
        this.fromId = fId;
        this.toId = tId;
        this.nickName = name;
        this.headimgurl = url;
        this.content = content;
        this.type = t;
        this.delivery = d;
        this.createdTime = cTime;
        this.direct = Direct.getDirect(fromId == EdusohoApp.app.loginUser.id);
        this.fileType = FileType.getType(type);
    }

    public Chat(WrapperXGPushTextMessage message) {
        CustomContent customContent = EdusohoApp.app.parseJsonValue(message.getCustomContentJson(), new TypeToken<CustomContent>() {
        });
        id = customContent.getId();
        fromId = customContent.getFromId();
        toId = EdusohoApp.app.loginUser.id;
        nickName = customContent.getNickname();
        headimgurl = customContent.getImgUrl();
        content = message.getContent();
        type = customContent.getTypeMsg();
        createdTime = customContent.getCreatedTime();
        direct = Direct.getDirect(fromId == EdusohoApp.app.loginUser.id);
        fileType = FileType.getType(type);
    }

    public Chat(OfflineMsgEntity offlineMsgModel) {
        V2CustomContent v2CustomContent = offlineMsgModel.getCustom();
        id = v2CustomContent.getMsgId();
        fromId = v2CustomContent.getFrom().getId();
        toId = EdusohoApp.app.loginUser.id;
        nickName = v2CustomContent.getFrom().getNickname();
        headimgurl = v2CustomContent.getFrom().getImage();
        content = v2CustomContent.getBody().getContent();
        type = v2CustomContent.getBody().getType();
        createdTime = v2CustomContent.getCreatedTime();
        direct = Direct.getDirect(fromId == EdusohoApp.app.loginUser.id);
        fileType = FileType.getType(type);
    }

    public Chat serializeCustomContent(Chat chat) {
        CustomContent customContent = EdusohoApp.app.parseJsonValue(this.custom, new TypeToken<CustomContent>() {
        });
        this.fromId = customContent.getFromId();
        this.toId = EdusohoApp.app.loginUser.id;
        this.nickName = customContent.getNickname();
        this.content = chat.content;
        this.headimgurl = customContent.getImgUrl();
        this.type = customContent.getTypeMsg();
        this.createdTime = chat.createdTime;
        this.direct = Direct.getDirect(fromId == EdusohoApp.app.loginUser.id);
        this.fileType = FileType.getType(type);
        if (this.fileType == FileType.TEXT) {
            this.delivery = Delivery.SUCCESS.getIndex();
        } else {
            this.delivery = Delivery.UPLOADING.getIndex();
        }
        return chat;
    }

    public CustomContent getCustomContent() {
        return TextUtils.isEmpty(custom) ? null : EdusohoApp.app.parseJsonValue(this.custom, new TypeToken<CustomContent>() {
        });
    }

    public static enum Direct {
        SEND, RECEIVE;

        public static Direct getDirect(boolean n) {
            if (n) {
                return SEND;
            } else {
                return RECEIVE;
            }
        }
    }

    /**
     * 资源类型
     */
    public static enum FileType {
        TEXT("text"), IMAGE("image"), AUDIO("audio"), VIDEO("video"), MULTI("multi");

        private String name;

        private FileType(String n) {
            this.name = n;
        }

        public String getName() {
            return this.name;
        }

        public static FileType getType(String name) {
            for (FileType type : FileType.values()) {
                if (type.getName().equals(name)) {
                    return type;
                }
            }
            return TEXT;
        }
    }

    public static enum Delivery {
        SUCCESS(1), FAILED(0), UPLOADING(2);

        private int index;

        private Delivery(int i) {
            this.index = i;
        }

        public int getIndex() {
            return this.index;
        }

        public static Delivery getDelivery(int i) {
            for (Delivery type : Delivery.values()) {
                if (type.index == i) {
                    return type;
                }
            }
            return null;
        }

        public static int getIndex(Delivery delivery) {
            for (Delivery type : Delivery.values()) {
                if (type == delivery) {
                    return type.getIndex();
                }
            }
            return -1;
        }
    }
}
