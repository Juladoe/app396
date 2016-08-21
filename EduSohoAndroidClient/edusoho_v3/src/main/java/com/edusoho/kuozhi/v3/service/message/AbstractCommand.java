package com.edusoho.kuozhi.v3.service.message;

import android.content.Context;

import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.imserver.managar.IMBlackListManager;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.NotificationProvider;

/**
 * Created by 菊 on 2016/4/25.
 */
public abstract class AbstractCommand {

    protected MessageBody mMessageBody;
    protected Context mContext;
    protected IMMessageReceiver mReceiver;

    public AbstractCommand(Context context, IMMessageReceiver receiver, MessageBody messageBody)
    {
        this.mContext = context;
        this.mReceiver = receiver;
        this.mMessageBody = messageBody;
    }

    public abstract void invoke();

    protected boolean isInBlackList(String convNo) {
        int status = IMClient.getClient().getIMBlackListManager().getBlackListByConvNo(convNo);
        return status == IMBlackListManager.NO_DISTURB;
    }

    protected NotificationProvider getNotificationProvider() {
        return FactoryManager.getInstance().create(NotificationProvider.class);
    }
}
