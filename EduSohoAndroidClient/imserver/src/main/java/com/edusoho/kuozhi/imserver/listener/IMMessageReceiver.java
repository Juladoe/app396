package com.edusoho.kuozhi.imserver.listener;

import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.ReceiverInfo;

/**
 * Created by su on 2016/3/22.
 */
public interface IMMessageReceiver {

    boolean onReceiver(MessageEntity msg);

    void onSuccess(String extr);

    ReceiverInfo getType();
}
