package com.edusoho.kuozhi.model;

/**
 * Created by howzhi on 14-8-19.
 */
public class MessageType {
    public int code;
    public String type;

    public static int NONE = -1;

    @Override
    public String toString() {
        if (code == NONE) {
            return type;
        }
        return type + "_" + code;
    }

    public MessageType(String type)
    {
        this.type = type;
    }

    public MessageType(int code, String type)
    {
        this.code = code;
        this.type = type;
    }
}
