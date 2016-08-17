package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;
import android.util.Log;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.ReceiverInfo;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.imserver.util.IMConnectStatus;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.service.message.CommandFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by 菊 on 2016/4/25.
 */
public class IMServiceProvider extends ModelProvider {

    public IMServiceProvider(Context context)
    {
        super(context);
    }

    public void unBindServer() {
        IMClient.getClient().destory();
    }

    public void reConnectServer(String clientName) {
        int status = IMClient.getClient().getIMConnectStatus();
        if (status != IMConnectStatus.CONNECTING && status != IMConnectStatus.OPEN) {
            IMClient.getClient().removeGlobalIMMessageReceiver();
            bindServer(clientName);
            return;
        }

        if (status == IMConnectStatus.OPEN) {
            IMClient.getClient().sendCmd("requestOfflineMsg");
        }
    }

    public synchronized void bindServer(final String clientName) {
        IMClient.getClient().setIMConnectStatus(IMConnectStatus.CONNECTING);
        new SystemProvider(mContext).getImServerHosts().success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap hostMap) {
                Log.d("IMServiceProvider", "init im service" + hostMap.size());
                IMClient.getClient().init(mContext.getApplicationContext(), getDomain());
                IMClient.getClient().start(
                        clientName,
                        new ArrayList<String>(hostMap.keySet()),
                        new ArrayList<String>(hostMap.values())
                );

                IMClient.getClient().addGlobalIMMessageReceiver(new IMMessageReceiver() {
                    @Override
                    public boolean onReceiver(MessageEntity msg) {
                        handlerMessage(this, msg);
                        return false;
                    }

                    @Override
                    public void onSuccess(String extr) {
                        Log.d(getClass().getSimpleName(), "onSuccess:" + extr);
                    }

                    @Override
                    public boolean onOfflineMsgReceiver(List<MessageEntity> messageEntities) {
                        return false;
                    }

                    @Override
                    public ReceiverInfo getType() {
                        return new ReceiverInfo("global", "1");
                    }
                });
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                Log.d("IMServiceProvider", "bindServer error");
                IMClient.getClient().setIMConnectStatus(IMConnectStatus.NO_READY);
                IMClient.getClient().init(mContext.getApplicationContext(), getHost());
                IMClient.getClient().start(null, null, null);
            }
        });
    }

    private void handlerMessage(IMMessageReceiver receiver, MessageEntity messageEntity) {
        MessageBody messageBody = new MessageBody(messageEntity);
        if (messageBody == null) {
            return;
        }
        CommandFactory.create(mContext, receiver, messageBody).invoke();
    }
}
