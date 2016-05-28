package com.edusoho.kuozhi.v3.service.message;

import android.content.Context;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;

/**
 * Created by 菊 on 2016/4/25.
 */
public class MessageCommand extends AbstractCommand {

    public MessageCommand(Context context, IMMessageReceiver receiver, MessageBody messageBody)
    {
        super(context, receiver, messageBody);
    }

    @Override
    public void invoke() {
        if (! IMClient.getClient().isHandleMessageInFront("chat", mMessageBody.getConvNo())) {
            getNotificationProvider().showNotification(mMessageBody);
        }

        String type = mMessageBody.getSource().getType();
        int targetId = mMessageBody.getSource().getId();
        new IMProvider(mContext).updateConvInfo(mMessageBody.getConvNo(), type, targetId);
    }

}
