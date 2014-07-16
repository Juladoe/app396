package com.edusoho.kuozhi.core.listener;

import com.edusoho.kuozhi.core.model.MessageModel;

/**
 * Created by howzhi on 14-7-14.
 */
public interface CoreEngineMsgCallback {
    public void invoke(MessageModel obj);
}
