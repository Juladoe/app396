package com.edusoho.kuozhi.v3.model.provider;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
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

    public IMServiceProvider(Context context) {
        super(context);
    }

    public void unBindServer() {
        IMClient.getClient().destory();
    }

    public void reConnectServer(String clientName) {
        int status = IMClient.getClient().getIMConnectStatus();
        if (status != IMConnectStatus.CONNECTING && status != IMConnectStatus.OPEN) {
            IMClient.getClient().removeGlobalIMMessageReceiver();
            connectServer(clientName);
            return;
        }

        if (status == IMConnectStatus.OPEN) {
            IMClient.getClient().sendCmd("requestOfflineMsg");
        }
    }

    private void connectServer(final String clientName) {
        IMClient.getClient().setIMConnectStatus(IMConnectStatus.CONNECTING);
        new SystemProvider(mContext).getImServerHosts().success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap hostMap) {
                Log.d("IMServiceProvider", "init im service" + hostMap.size());
                if (TextUtils.isEmpty(clientName)
                        || hostMap == null
                        || hostMap.isEmpty()
                        || hostMap.values().isEmpty()
                        || hostMap.keySet().isEmpty()) {
                    errorBindImServer();
                    return;
                }
                successBindImserver(clientName, hostMap);

            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                errorBindImServer();
            }
        });
    }

    private void successBindImserver(String clientName, LinkedHashMap hostMap) {
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
                MessageBody messageBody = new MessageBody(extr);
                if (messageBody == null) {
                    return;
                }
                updateMessageStatus(messageBody);
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

    private void errorBindImServer() {
        Log.d("IMServiceProvider", "bindServer error");
        IMClient.getClient().setIMConnectStatus(IMConnectStatus.ERROR);
        IMClient.getClient().init(mContext.getApplicationContext(), getHost());
    }

    public synchronized void bindServer(String clientName) {
        int status = IMClient.getClient().getIMConnectStatus();
        if (status == IMConnectStatus.CONNECTING || status == IMConnectStatus.OPEN) {
            Log.d("IMServiceProvider", "serice is runing");
            return;
        }
        connectServer(clientName);
    }

    protected void updateMessageStatus(MessageBody messageBody) {
        ContentValues cv = new ContentValues();
        cv.put("status", MessageEntity.StatusType.SUCCESS);
        IMClient.getClient().getMessageManager().updateMessageFieldByUid(messageBody.getMessageId(), cv);
    }

    private void handlerMessage(IMMessageReceiver receiver, MessageEntity messageEntity) {
        MessageBody messageBody = new MessageBody(messageEntity);
        if (messageBody == null) {
            return;
        }
        CommandFactory.create(mContext, receiver, messageBody).invoke();
    }
}
