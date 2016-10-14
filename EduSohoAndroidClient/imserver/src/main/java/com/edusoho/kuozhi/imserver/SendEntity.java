package com.edusoho.kuozhi.imserver;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 菊 on 2016/4/27.
 */
public class SendEntity implements Parcelable {

    private String toId;
    private String convNo;
    private String msg;
    private String cmd;

    public SendEntity() {
    }

    public SendEntity(Parcel in)
    {
        this.toId = in.readString();
        this.convNo = in.readString();
        this.msg = in.readString();
        this.cmd = in.readString();
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
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


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toId);
        dest.writeString(convNo);
        dest.writeString(msg);
        dest.writeString(cmd);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<SendEntity> CREATOR = new Parcelable.Creator<SendEntity>() {

        public SendEntity createFromParcel(Parcel in) {
            return new SendEntity(in);
        }

        public SendEntity[] newArray(int size) {
            return new SendEntity[size];
        }
    };
}
