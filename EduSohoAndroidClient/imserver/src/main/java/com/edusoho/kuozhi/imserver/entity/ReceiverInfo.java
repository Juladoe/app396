package com.edusoho.kuozhi.imserver.entity;

/**
 * Created by 菊 on 2016/4/26.
 */
public class ReceiverInfo {

    public int msgId;

    public String msgType;

    public boolean isProcessed;

    public ReceiverInfo(String msgType, int msgId)
    {
        this.msgId = msgId;
        this.msgType = msgType;
    }
}
