package com.edusoho.kuozhi.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.RemoteViews;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.broadcast.DownLoadStatusReceiver;
import com.edusoho.kuozhi.broadcast.callback.StatusCallback;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.M3U8Uitl;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by howzhi on 14-10-11.
 */
public class M3U8DownService extends Service {

    private Context mContext;

    public static final int UPDATE = 0001;

    private NotificationManager notificationManager;
    private SparseArray<Notification> notificationList;
    private ScheduledThreadPoolExecutor mThreadPoolExecutor;

    private DownLoadStatusReceiver mDownLoadStatusReceiver;

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDownLoadStatusReceiver);
        Log.d(null, "m3u8 download_service destory");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(null, "m3u8 download_service create");
        mContext = this;

        notificationList = new SparseArray<Notification>();
        mThreadPoolExecutor = new ScheduledThreadPoolExecutor(3);
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        mDownLoadStatusReceiver = new DownLoadStatusReceiver(mStatusCallback);
        registerReceiver(mDownLoadStatusReceiver, new IntentFilter(DownLoadStatusReceiver.ACTION));
    }

    public static Intent getIntent(Context context)
    {
        Intent intent = new Intent();
        intent.setClass(context, M3U8DownService.class);
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startDown(
            Context context, int lessonId, int courseId, String title) {
        Intent intent = new Intent();
        intent.putExtra("lessonId", lessonId);
        intent.putExtra("courseId", courseId);
        intent.putExtra("title", title);
        intent.setClass(context, M3U8DownService.class);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int lessonId = intent.getIntExtra("lessonId", 0);
        final int courseId = intent.getIntExtra("courseId", 0);
        final String lessonTitle = intent.getStringExtra("title");

        mThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(null, "m3u8 download_service onStartCommand");
                M3U8Uitl m3U8Uitl = new M3U8Uitl(mContext);
                createNotification(lessonId, lessonTitle);
                m3U8Uitl.download(lessonId, courseId);
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotification(int lessonId, String title) {
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        // 定义Notification的各种属性
        Notification notification = new Notification(R.drawable.notification_download_icon,
                "正在下载 " + title, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_LIGHTS;

        Intent notificationIntent = new Intent(this, null);
        PendingIntent contentItent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = contentItent;
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_layout);

        remoteViews.setTextViewText(R.id.notify_title, title);
        remoteViews.setTextViewText(R.id.notify_percent, "0%");
        notification.contentView = remoteViews;

        // 把Notification传递给NotificationManager
        notificationManager.notify(notificationList.size(), notification);
        notificationList.put(lessonId, notification);
    }

    private void updateNotification(int lessonId, String title, int total, int download) {
        Notification notification = notificationList.get(lessonId);
        if (notification == null) {
            return;
        }
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_layout);

        remoteViews.setTextViewText(R.id.notify_title, title);
        remoteViews.setProgressBar(R.id.notify_progress, total, download, false);
        float percent = (download / (float) total);
        remoteViews.setTextViewText(R.id.notify_percent, (int) (percent * 100) + "%");
        notification.contentView = remoteViews;
        notificationManager.notify(notificationList.indexOfKey(lessonId), notification);
    }

    private void showComplteNotification(int lessonId, String title) {
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        // 定义Notification的各种属性
        Notification notification = new Notification(R.drawable.notification_download_icon,
                "下载完成 " + title, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.tickerText = "下载完成";

        Intent notificationIntent = new Intent();
        PendingIntent contentItent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        notification.contentIntent = contentItent;

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_layout);

        remoteViews.setTextViewText(R.id.notify_title, title);
        remoteViews.setViewVisibility(R.id.notify_progress, View.GONE);
        remoteViews.setViewVisibility(R.id.notify_finish, View.VISIBLE);
        remoteViews.setTextViewText(R.id.notify_finish, "下载完成");
        remoteViews.setTextViewText(R.id.notify_percent, "100%");
        notification.contentView = remoteViews;

        // 把Notification传递给NotificationManager
        notificationManager.notify(0, notification);
    }

    private StatusCallback mStatusCallback = new StatusCallback() {
        @Override
        public void invoke(Intent intent) {
            int lessonId = intent.getIntExtra(Const.LESSON_ID, 0);
            String title = intent.getStringExtra(Const.ACTIONBAT_TITLE);
            M3U8DbModle m3U8DbModle = M3U8Uitl.queryM3U8Modle(
                    mContext, lessonId, EdusohoApp.app.domain);
            if (m3U8DbModle == null) {
                return;
            }
            if (m3U8DbModle.downloadNum == m3U8DbModle.totalNum) {
                showComplteNotification(lessonId, title);
            } else {
                updateNotification(lessonId, title, m3U8DbModle.totalNum, m3U8DbModle.downloadNum);
            }
        }
    };

}
