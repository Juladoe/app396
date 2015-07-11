package com.edusoho.kuozhi.v3.model.bal.push;

/**
 * Created by JesseHuang on 15/7/2.
 */
public class CustomContent {
    private int chatId;
    private int id;
    private int fromId;
    private String nickname;
    private String imgUrl;
    /**
     * 信息对象
     */
    private String typeObject;
    /**
     * 资源类型：text, image, audio
     */
    private String typeMsg;
    /**
     * 消息类型：normal, bulletin, verified, course
     */
    private String typeBusiness;
    private int createdTime;

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTypeObject() {
        return typeObject;
    }

    public void setTypeObject(String typeObject) {
        this.typeObject = typeObject;
    }

    public String getTypeMsg() {
        return typeMsg;
    }

    public void setTypeMsg(String typeMsg) {
        this.typeMsg = typeMsg;
    }

    public String getTypeBusiness() {
        return typeBusiness;
    }

    public void setTypeBusiness(String typeBusiness) {
        this.typeBusiness = typeBusiness;
    }

    public int getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(int createdTime) {
        this.createdTime = createdTime;
    }

    public static enum TypeBusiness {
        NORMAL("normal", 1), BULLETIN("bulletin", 2), COURSE("course", 3), VERIFIED("verified", 4);
        private String name;
        private int index;

        private TypeBusiness(String n, int i) {
            this.name = n;
            this.index = i;
        }

        public String getName() {
            return this.name;
        }

        public int getIndex() {
            return this.index;
        }

        public static TypeBusiness getType(String name) {
            for (TypeBusiness typeObject : TypeBusiness.values()) {
                if (name.equals(typeObject.getName())) {
                    return typeObject;
                }
            }
            return null;
        }
    }
}
