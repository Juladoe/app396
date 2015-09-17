package com.edusoho.kuozhi.v3.model.bal.push;

/**
 * Created by JesseHuang on 15/9/15.
 */
public class V2CustomContent {
    private int msgId;
    private int v;
    private FromEntity from;
    private ToEntity to;
    private BodyEntity body;
    private int createdTime;

    public void setTo(ToEntity to) {
        this.to = to;
    }

    public void setBody(BodyEntity body) {
        this.body = body;
    }

    public void setV(int v) {
        this.v = v;
    }

    public void setCreatedTime(int createdTime) {
        this.createdTime = createdTime;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public void setFrom(FromEntity from) {
        this.from = from;
    }

    public ToEntity getTo() {
        return to;
    }

    public BodyEntity getBody() {
        return body;
    }

    public int getV() {
        return v;
    }

    public int getCreatedTime() {
        return createdTime;
    }

    public int getMsgId() {
        return msgId;
    }

    public FromEntity getFrom() {
        return from;
    }

    public static class ToEntity {
        /**
         * id : 268
         * type : user
         */
        private String id;
        private String type;

        public void setId(String id) {
            this.id = id;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }
    }

    public static class BodyEntity {
        /**
         * content : bd
         * type : text
         */
        private String content;
        private String type;
        private int lessonId;

        public void setContent(String content) {
            this.content = content;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public String getType() {
            return type;
        }

        public int getLessonId() {
            return lessonId;
        }

        public void setLessonId(int lessonId) {
            this.lessonId = lessonId;
        }
    }

    public static class FromEntity {

        private int id;
        private String nickname;
        private String image;
        private String type;

        public void setId(int id) {
            this.id = id;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public String getNickname() {
            return nickname;
        }

        public String getImage() {
            return image;
        }

        public String getType() {
            return type;
        }
    }
}
