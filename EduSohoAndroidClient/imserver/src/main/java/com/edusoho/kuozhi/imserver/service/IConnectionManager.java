package com.edusoho.kuozhi.imserver.service;

import com.edusoho.kuozhi.imserver.listener.IChannelReceiveListener;
import com.edusoho.kuozhi.imserver.listener.IConnectManagerListener;
import java.util.List;

/**
 * Created by 菊 on 2016/4/22.
 */
public interface IConnectionManager {

    void setServerHostList(List<String> hostList);

    void addIConnectStatusListener(IConnectManagerListener iConnectStatusListener);

    void addIChannelReceiveListener(IChannelReceiveListener listener);

    void accept();

    void close();

    void send(String content);

    boolean isConnected();

    int getStatus();
}
