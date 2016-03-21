package com.edusoho.kuozhi.imserver;

import android.os.SystemClock;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by su on 2016/3/18.
 */
public class PingManager {

    public static final int GSM = 0001;
    public static final int WCDMA = 0002;
    public static final int LTE = 0003;
    public static final int WIFI = 0004;
    public static final int NONE = 0005;

    public static final int PONG_FAIL = 0010;
    public static final int PONG_SUCCESS = 0011;
    public static final int PONG_TIMEOUT = 0012;

    public static final int PING_SLOW = 0101;
    public static final int PING_FAST = 0102;
    public static final int PING_NORMAL = 0103;

    private int pingType;
    private int pingSpeed;
    private int pingSuccessCount;
    private int pingFailCount;
    private long lasterPingTime;
    private int pingTotalCount;

    private PingCallback mPingCallback;
    private Thread mPingThread;
    private Timer mPingTimer;

    public PingManager() {
        this.pingFailCount = 0;
        this.pingSuccessCount = 0;
        switchPingType(WCDMA);
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
            case WCDMA:
                pingSpeed = PING_NORMAL;
                break;
            case GSM:
                pingSpeed = PING_FAST;
                break;
            case LTE:
            case WIFI:
                pingSpeed = PING_SLOW;
                break;
        }
    }

    private int getPingTime() {
        switch (pingSpeed) {
            case PING_FAST:
                return 5000;
            case PING_NORMAL:
                return 5000 * 4;
            case PING_SLOW:
                return 5000 * 10;
        }
        return 5000;
    }

    private int getPingMaxCount() {
        switch (pingSpeed) {
            case PING_FAST:
                return 5;
            case PING_NORMAL:
                return 5 * 4;
            case PING_SLOW:
                return 5 * 10;
        }
        return 5;
    }

    public void start() {
        mPingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mPingTimer = new Timer();
                mPingTimer.schedule(getPingTimerTask(), 0);
            }
        });
        mPingThread.run();
    }

    private TimerTask getPingTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                lasterPingTime = SystemClock.currentThreadTimeMillis();
                pingTotalCount ++;
                if (pingSuccessCount > getPingMaxCount()) {
                    pingSuccessCount = 0;
                    upgradePingSpeed();
                    Log.d(getClass().getSimpleName(), "upgradePingSpeed:" + pingSpeed);
                }
                mPingCallback.onPing();
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
                pingFailCount ++;
                break;
        }
    }

    public void setPingCallback(PingCallback pingCallback) {
        this.mPingCallback = pingCallback;
    }

    public interface PingCallback
    {
        void onPing();
    }
}
