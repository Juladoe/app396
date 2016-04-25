package com.edusoho.kuozhi.imserver.service;

import com.edusoho.kuozhi.imserver.listener.IHeartStatusListener;

/**
 * Created by 菊 on 2016/4/22.
 */
public interface IHeartManager {

    int PONG_FAIL = 0010;
    int PONG_SUCCESS = 0011;
    int PONG_TIMEOUT = 0012;

    int PING_SLOW = 0101;
    int PING_FAST = 0102;
    int PING_NORMAL = 0103;

    void start();

    void stop();

    void setPongResult(int pongType);

    void switchPingType(int netType);

    void addHeartStatusListener(IHeartStatusListener listener);
}
