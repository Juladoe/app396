package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;
import android.util.Log;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.ReceiverInfo;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.UtilFactory;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
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

    public void bindServer() {
        new SystemProvider(mContext).getImServerHosts().success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap hostMap) {
                Log.d("IMServiceProvider", "init im service" + hostMap.size());
                IMClient.getClient().init(mContext);
                IMClient.getClient().start(
                        new ArrayList<String>(hostMap.keySet()),
                        new ArrayList<String>(hostMap.values())
                );

                IMClient.getClient().addMessageReceiver(new IMMessageReceiver() {
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
                        return new ReceiverInfo("global", 1);
                    }
                });
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                Log.d("IMServiceProvider", "bindServer error");
                IMClient.getClient().init(mContext);
                IMClient.getClient().start(null, null);
            }
        });
    }

    private void handlerMessage(IMMessageReceiver receiver, MessageEntity messageEntity) {
        V2CustomContent v2CustomContent = getUtilFactory().getJsonParser().fromJson(messageEntity.getMsg(), V2CustomContent.class);
        if (v2CustomContent == null) {
            return;
        }
        CommandFactory.create(mContext, receiver, v2CustomContent).invoke();
    }

    protected UtilFactory getUtilFactory() {
        return FactoryManager.getInstance().create(UtilFactory.class);
    }
}
