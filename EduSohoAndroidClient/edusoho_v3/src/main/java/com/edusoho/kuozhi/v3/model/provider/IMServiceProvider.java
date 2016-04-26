package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;
import android.util.Log;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.ReceiverInfo;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.UtilFactory;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.service.message.CommandFactory;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by 菊 on 2016/4/25.
 */
public class IMServiceProvider extends ModelProvider {

    public IMServiceProvider(Context context)
    {
        super(context);
    }

    public void bindServer() {
        new SystemProvider(mContext).getImServerHosts().success(new NormalCallback<ArrayList<String>>() {
            @Override
            public void success(ArrayList<String> hostList) {
                Log.d("IMServiceProvider", "init im service" + Arrays.toString(hostList.toArray()));
                IMClient.getClient().init(mContext);
                IMClient.getClient().start(hostList);

                IMClient.getClient().addMessageReceiver(new IMMessageReceiver() {
                    @Override
                    public boolean onReceiver(String msg) {
                        handlerMessage(this, msg);
                        return false;
                    }

                    @Override
                    public ReceiverInfo getType() {
                        return new ReceiverInfo("global", 1);
                    }
                });
            }
        });
    }

    private void handlerMessage(IMMessageReceiver receiver, String msg) {
        V2CustomContent v2CustomContent = getUtilFactory().getJsonParser().fromJson(msg, V2CustomContent.class);
        CommandFactory.create(mContext, receiver, v2CustomContent).invoke();
    }

    protected UtilFactory getUtilFactory() {
        return FactoryManager.getInstance().create(UtilFactory.class);
    }
}
