package com.edusoho.kuozhi.clean.bean;

/**
 * Created by JesseHuang on 2017/4/25.
 */

public class MessageEvent<T> {

    private T mMessage;
    private int mCode;

    public MessageEvent(T message) {
        mMessage = message;
        mCode = MessageEventCode.NO_CODE;
    }

    public MessageEvent(T message, int code) {
        mMessage = message;
        mCode = code;
    }

    public MessageEvent(int code) {
        mMessage = null;
        mCode = code;
    }

    public T getMessageBody() {
        return mMessage;
    }

    public int getType() {
        return mCode;
    }

    public static class MessageEventCode {
        public static final int NO_CODE = -1;
        public static final int COURSE_JOIN = 0;
        public static final int COURSE_EXIT = 1;
        public static final int LEARN_TASK = 2;
    }
}
