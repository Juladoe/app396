package com.edusoho.kuozhi.imserver.service.Impl;

import android.os.SystemClock;
import android.util.Log;

import com.edusoho.kuozhi.imserver.listener.IHeartStatusListener;
import com.edusoho.kuozhi.imserver.service.IHeartManager;
import com.edusoho.kuozhi.imserver.util.NetTypeConst;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 菊 on 2016/4/22.
 */
public class HeartManagerImpl implements IHeartManager {

    private static final String TAG = "HeartManager";

    private int pingType;
    private int pingSpeed;
    private int pingSuccessCount;
    private int pingFailCount;
    private long lasterPingTime;
    private int pingTotalCount;

    private IHeartStatusListener mIHeartStatusListener;
    private Thread mPingThread;
    private Timer mPingTimer;

    public HeartManagerImpl() {
        init();
    }

    private void init() {
        this.pingFailCount = 0;
        this.pingTotalCount = 0;
        this.pingSuccessCount = 0;
        switchPingType(NetTypeConst.WCDMA);
    }

    private void upgradePingSpeed() {
        if (pingSpeed == PING_FAST) {
            return;
        }
        pingSpeed ++;
    }

    public void switchPingType(int type) {
        this.pingType = type;
        switch (type) {
            case NetTypeConst.WCDMA:
                pingSpeed = PING_NORMAL;
                break;
            case NetTypeConst.GSM:
                pingSpeed = PING_FAST;
                break;
            case NetTypeConst.LTE:
            case NetTypeConst.WIFI:
                pingSpeed = PING_SLOW;
                break;
        }
    }

    private int getPingTime() {
        switch (pingSpeed) {
            case PING_FAST:
                return 6000;
            case PING_NORMAL:
                return 6000 * 10;
            case PING_SLOW:
                return 6000 * 30;
        }
        return 6000;
    }

    private int getPingMaxCount() {
        switch (pingSpeed) {
            case PING_FAST:
                return 6;
            case PING_NORMAL:
                return 6 * 5;
            case PING_SLOW:
                return 6 * 10;
        }
        return 6;
    }

    @Override
    public void start() {
        mPingTimer = new Timer();
        mPingTimer.schedule(getPingTimerTask(), 1);
    }

    private TimerTask getPingTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                lasterPingTime = SystemClock.currentThreadTimeMillis();
                pingTotalCount ++;
                Log.d(TAG, "lasterPingTime:" + pingTotalCount);
                Log.d(TAG, "current PingSpeed:" + pingSpeed);
                if (pingFailCount >= 10) {
                    stop();
                    mIHeartStatusListener.onPong(IHeartStatusListener.TIMEOUT);
                    return;
                }
                if (pingSuccessCount > getPingMaxCount()) {
                    pingSuccessCount = 0;
                    upgradePingSpeed();
                }
                mIHeartStatusListener.onPing();
                mPingTimer.schedule(getPingTimerTask(), getPingTime());
            }
        };
    }

    public void setPongResult(int pongType) {
        switch (pongType) {
            case PONG_SUCCESS:
                pingSuccessCount ++;
                break;
            case PONG_FAIL:
                Log.d(TAG, "pingFail:" + pingFailCount);
                pingFailCount ++;
                break;
        }
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop");
        if (mPingTimer != null) {
            mPingTimer.cancel();
        }
        if (mPingThread != null) {
            mPingThread.interrupt();
            mPingThread = null;
        }

        init();
    }

    @Override
    public void addHeartStatusListener(IHeartStatusListener listener) {
        this.mIHeartStatusListener = listener;
    }
}
