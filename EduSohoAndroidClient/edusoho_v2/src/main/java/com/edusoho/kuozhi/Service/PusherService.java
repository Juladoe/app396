package com.edusoho.kuozhi.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.broadcast.AlarmReceiver;
import com.edusoho.kuozhi.model.Push.PushMsg;
import com.edusoho.kuozhi.ui.fragment.SchoolRoomFragment;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.PushUtil;

import org.ddpush.im.v1.client.appuser.Message;
import org.ddpush.im.v1.client.appuser.TCPClientBase;

/**
 * Created by JesseHuang on 14/12/17.
 */
public class PusherService extends Service {
    private static final String TAG = "PusherService";
    private PowerManager.WakeLock mWakeLock;
    private TcpClient mTcpClient;
    protected PendingIntent tickPendIntent;
    private EdusohoApp mApp;

    public static final String WAKE = "WAKE";
    public static final String RESET = "RESET";
    public static final String NOTIFY = "NOTIFY";

    public PusherService() {

    }

    public class TcpClient extends TCPClientBase {

        public TcpClient(byte[] uuid, int appid, String serverAddress, int serverPort) throws Exception {
            super(uuid, appid, serverAddress, serverPort, 10);
        }

        /**
         * 该函数主要用于智能终端，判断当前是否有可用的网络连接，以达到省电的目的（若无网络连接则不尝试网络操作）。
         *
         * @return
         */
        @Override
        public boolean hasNetworkConnection() {
            return PushUtil.hasNetwork(PusherService.this);
        }

        /**
         * 该函数同样主要用于智能终端，在客户端不需要发送心跳包，并且服务端无推送信息的时候，尝试系统休眠，达到省电的目的。
         */
        @Override
        public void trySystemSleep() {
            tryReleaseWakeLock();
        }

        @Override
        public void onPushMessage(Message message) {
            if (message == null) {
                return;
            }
            if (message.getData() == null || message.getData().length == 0) {
                return;
            }
            if (message.getCmd() == Const.PUSH_CODE) {

                String str = null;
                try {
                    str = new String(message.getData(), 5, message.getContentLength(), "UTF-8");
                } catch (Exception e) {
                    str = PushUtil.convert(message.getData(), 5, message.getContentLength());
                }
                String[] msgs = str.split("[|]");
                PushMsg pushMsg;
                if (msgs != null && msgs.length > 1) {
                    pushMsg = new PushMsg(msgs, PusherService.this);
                    if (pushMsg.getTypeId().equals("2")) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(SchoolRoomFragment.PUSH_MODEL, pushMsg);
                        mApp.sendMsgToTarget(SchoolRoomFragment.PUSH_ITEM, bundle, SchoolRoomFragment.class);
                    }
                    notifyUser(Const.PUSH_CODE, pushMsg.getNotificationTitle(), pushMsg.getNotificationContent(), "收到推送信息", pushMsg.getIntent());
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = (EdusohoApp) getApplication();
        Log.d(TAG, "PusherService is onCreate!");
        setTickAlarm();

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PusherService");
        resetClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "PusherService is onStartCommand!");
        if (intent == null) {
            return START_STICKY;
        }

        String cmd = intent.getStringExtra(Const.PUSH_CMD_CODE);
        if (cmd == null) {
            cmd = "";
        }
        if (cmd.equals(WAKE)) {
            if (mWakeLock != null && mWakeLock.isHeld() == false) {
                mWakeLock.acquire();
            }
        }
        if (cmd.equals(RESET)) {
            if (mWakeLock != null && mWakeLock.isHeld() == false) {
                mWakeLock.acquire();
            }
            resetClient();
        }
        if (cmd.equals(NOTIFY)) {
            String text = intent.getStringExtra("Text");
            if (text != null && text.trim().length() > 0) {
                Log.d(TAG, text);
            }
        }

        setPkgsInfo();

        return START_STICKY;
    }

    private void tryReleaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld() == false) {
            mWakeLock.acquire();
        }
    }

    /**
     * 出现通知栏通知用户
     *
     * @param id
     * @param title
     * @param content
     * @param tickerText
     */
    private void notifyUser(int id, String title, String content, String tickerText, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new NotificationCompat.Builder(this).setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().
                bigText(content)).setContentIntent(pi).build();
        notification.contentIntent = pi;
        notification.setLatestEventInfo(this, title, content, pi);


        notification.defaults = Notification.DEFAULT_ALL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.icon = R.drawable.icon;
        notification.when = System.currentTimeMillis();
        notification.tickerText = tickerText;

        notificationManager.notify(id, notification);
    }

    /**
     * 获取sent，receive信息，写入SharedPreferences
     */
    private void setPkgsInfo() {
        if (mTcpClient == null) {
            return;
        }
//        long sent = mTcpClient.getSentPackets();
//        long receive = mTcpClient.getReceivedPackets();
        SharedPreferences shared = getSharedPreferences(Const.LOCAL_PUSH_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
//        editor.putString(Const.SENT_PKGS, sent + "");
//        editor.putString(Const.RECEIVE_PKGS, receive + "");
        editor.commit();
    }

    /**
     * 设置连接广播，每5分钟进行进行一次广播，测试连接
     */
    protected void setTickAlarm() {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        int requestCode = 0;
        tickPendIntent = PendingIntent.getBroadcast(this,
                requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //小米2s的MIUI操作系统，目前最短广播间隔为5分钟，少于5分钟的alarm会等到5分钟再触发！
        long triggerAtTime = System.currentTimeMillis();
        int interval = 300 * 1000;
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, interval, tickPendIntent);
    }

    /**
     * 重新初始化客户端信息
     */
    private void resetClient() {
        SharedPreferences shared = this.getSharedPreferences(Const.LOCAL_PUSH_DATA, Context.MODE_PRIVATE);
        String serverIp = shared.getString(Const.SERVER_IP, "");
        String serverPort = shared.getString(Const.SERVER_PORT, "");
        String userID = shared.getString(Const.USER_ID, "");
        if (serverIp == null || serverIp.trim().length() == 0
                || serverPort == null || serverPort.trim().length() == 0
                || userID == null || userID.trim().length() == 0) {
            return;
        }
        if (mTcpClient != null) {
            try {
                mTcpClient.stop();
            } catch (Exception ex) {

            }
        }
        try {
            mTcpClient = new TcpClient(PushUtil.md5Byte(userID), Const.APP_ID, serverIp, Integer.parseInt(serverPort));
            mTcpClient.setHeartbeatInterval(50);
            mTcpClient.start();
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(Const.SENT_PKGS, "0");
            editor.putString(Const.RECEIVE_PKGS, "0");
            editor.commit();
        } catch (Exception ex) {

        }
    }

    protected void cancelNotifyRunning() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelNotifyRunning();
        this.tryReleaseWakeLock();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
