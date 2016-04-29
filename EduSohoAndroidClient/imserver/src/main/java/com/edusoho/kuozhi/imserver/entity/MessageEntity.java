package com.edusoho.kuozhi.imserver.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 菊 on 2016/4/27.
 */
public class MessageEntity implements Parcelable {

    private String fromId;
    private String fromName;
    private String toName;
    private String time;
    private String msgNo;
    private String toId;
    private String convNo;
    private String msg;
    private String cmd;

    public MessageEntity() {
    }

    public MessageEntity(Parcel in)
    {
        this.fromId = in.readString();
        this.fromName = in.readString();
        this.toName = in.readString();
        this.time = in.readString();
        this.msgNo = in.readString();
        this.toId = in.readString();
        this.convNo = in.readString();
        this.msg = in.readString();
        this.cmd = in.readString();
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getConvNo() {
        return convNo;
    }

    public void setConvNo(String convNo) {
        this.convNo = convNo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMsgNo() {
        return msgNo;
    }

    public void setMsgNo(String msgNo) {
        this.msgNo = msgNo;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromId);
        dest.writeString(fromName);
        dest.writeString(toName);
        dest.writeString(time);
        dest.writeString(msgNo);
        dest.writeString(toId);
        dest.writeString(convNo);
        dest.writeString(msg);
        dest.writeString(cmd);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<MessageEntity> CREATOR = new Parcelable.Creator<MessageEntity>() {

        public MessageEntity createFromParcel(Parcel in) {
            return new MessageEntity(in);
        }

        public MessageEntity[] newArray(int size) {
            return new MessageEntity[size];
        }
    };
}
