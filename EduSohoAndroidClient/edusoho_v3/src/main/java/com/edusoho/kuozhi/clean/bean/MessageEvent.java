package com.edusoho.kuozhi.clean.bean;

/**
 * Created by JesseHuang on 2017/4/25.
 */

public class MessageEvent<T> {

    private final T mMessage;

    public MessageEvent(T message) {
        mMessage = message;
    }

    public T getMessageBody() {
        return mMessage;
    }
}
