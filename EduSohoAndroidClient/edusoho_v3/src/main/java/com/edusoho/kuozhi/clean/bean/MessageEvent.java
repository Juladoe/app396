package com.edusoho.kuozhi.clean.bean;

/**
 * Created by JesseHuang on 2017/4/25.
 */

public class MessageEvent<T> {

    public static final int NO_CODE = -1;
    public static final int COURSE_EXIT = 0;
    public static final int LEARN_TASK = 1;
    public static final int SHOW_NEXT_TASK = 2;

    private T mMessage1;
    private int mCode;

    public MessageEvent(T message) {
        mMessage1 = message;
        mCode = NO_CODE;
    }

    public MessageEvent(T message, int code) {
        mMessage1 = message;
        mCode = code;
    }

    public MessageEvent(int code) {
        mMessage1 = null;
        mCode = code;
    }

    public T getMessageBody() {
        return mMessage1;
    }

    public int getType() {
        return mCode;
    }
}
