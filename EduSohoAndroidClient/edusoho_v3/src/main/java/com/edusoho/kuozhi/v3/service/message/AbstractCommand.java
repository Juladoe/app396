package com.edusoho.kuozhi.v3.service.message;

import android.content.Context;

import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;

/**
 * Created by 菊 on 2016/4/25.
 */
public abstract class AbstractCommand {

    protected V2CustomContent mV2CustomContent;
    protected Context mContext;
    protected IMMessageReceiver mReceiver;

    public AbstractCommand(Context context, IMMessageReceiver receiver, V2CustomContent v2CustomContent)
    {
        this.mContext = context;
        this.mReceiver = receiver;
        this.mV2CustomContent = v2CustomContent;
    }

    public abstract void invoke();
}
