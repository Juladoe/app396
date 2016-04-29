package com.edusoho.kuozhi.imserver.listener;

/**
 * Created by 菊 on 2016/4/22.
 */
public interface IConnectManagerListener {

    int CLOSE = 0x01;
    int OPEN = 0x02;
    int END = 0x03;
    int ERROR = 0x04;
    int CONNECTING = 0x05;

    void onStatusChange(int status, String error);
}
