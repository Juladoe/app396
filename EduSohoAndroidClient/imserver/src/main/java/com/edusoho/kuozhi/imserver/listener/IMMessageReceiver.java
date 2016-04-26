package com.edusoho.kuozhi.imserver.listener;

import com.edusoho.kuozhi.imserver.entity.ReceiverInfo;

/**
 * Created by su on 2016/3/22.
 */
public interface IMMessageReceiver {

    boolean onReceiver(String msg);

    ReceiverInfo getType();
}
