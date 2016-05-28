package com.edusoho.kuozhi.v3.service.message;

import android.content.Context;
import android.util.Log;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;

/**
 * Created by 菊 on 2016/4/25.
 */
public class DiscussMsgCommand extends AbstractCommand {

    private static final String TAG = "DiscussMsgCommand";

    public DiscussMsgCommand(Context context, IMMessageReceiver receiver, MessageBody messageBody)
    {
        super(context, receiver, messageBody);
    }

    @Override
    public void invoke() {
        Destination destination = mMessageBody.getDestination();
        if (destination == null) {
            Log.d(TAG, "no destination");
            return;
        }
        if (! IMClient.getClient().isHandleMessageInFront(destination.getType(), mMessageBody.getConvNo())) {
            getNotificationProvider().showNotification(mMessageBody);
        }
        String type = mMessageBody.getDestination().getType();
        int targetId = mMessageBody.getDestination().getId();
        new IMProvider(mContext).updateConvInfo(mMessageBody.getConvNo(), type, targetId);
    }
}
