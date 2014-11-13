package com.edusoho.kuozhi.model;

import android.os.Bundle;

import com.edusoho.listener.NormalCallback;

/**
 * Created by howzhi on 14-8-20.
 */
public class WidgetMessage {
    public MessageType type;
    public Bundle data;
    public Object target;
    public NormalCallback callback;

    public WidgetMessage(MessageType type, Bundle body)
    {
        this.type = type;
        this.data = body;
    }

    public WidgetMessage(
            MessageType type, Bundle body, NormalCallback callback)
    {
        this(type, body);
        this.callback = callback;
    }
}
