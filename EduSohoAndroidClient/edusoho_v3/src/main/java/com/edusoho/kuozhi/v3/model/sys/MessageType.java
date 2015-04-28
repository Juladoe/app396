package com.edusoho.kuozhi.v3.model.sys;

/**
 * Created by JesseHuang on 15/4/23.
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

    public MessageType(String type) {
        this.code = NONE;
        this.type = type;
    }

    public MessageType(int code, String type) {
        this.code = code;
        this.type = type;
    }
}
