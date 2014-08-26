package com.edusoho.kuozhi.model;

import android.os.Bundle;

/**
 * Created by howzhi on 14-8-20.
 */
public class WidgetMessage {
    public MessageType type;
    public Bundle data;
    public Object target;

    public WidgetMessage(MessageType type, Bundle body)
    {
        this.type = type;
        this.data = body;
    }

    public WidgetMessage(MessageType type, Bundle body, Object target)
    {
        this(type, body);
        this.target = target;
    }
}
