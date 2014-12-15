package com.edusoho.kuozhi.Service;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.LessonMaterial;
import com.edusoho.kuozhi.ui.lesson.LessonResourceActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.M3U8Uitl;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import cn.trinea.android.common.util.FileUtils;


/**
 * Created by howzhi on 14-10-11.
 */
public class M3U8DownService extends Service {

    private String mNotifiTitle;
    private Context mContext;

    public static final int UPDATE = 0001;

    private HashMap<Long, Notification> notificationHashMap;
    private Timer mTimer;
    private NotificationManager notificationManager;
    private ScheduledThreadPoolExecutor mThreadPoolExecutor;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE:
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(null, "m3u8 download_service destory");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(null, "m3u8 download_service create");
        mContext = this;
        mThreadPoolExecutor = new ScheduledThreadPoolExecutor(3);
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationHashMap = new HashMap<Long, Notification>();
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

    public static void startDown(Context context, int lessonId, int courseId) {
        Intent intent = new Intent();
        intent.putExtra("lessonId", lessonId);
        intent.putExtra("courseId", courseId);
        intent.setClass(context, M3U8DownService.class);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int lessonId = intent.getIntExtra("lessonId", 0);
        final int courseId = intent.getIntExtra("courseId", 0);

        new Thread(){
            @Override
            public void run() {
                Log.d(null, "m3u8 download_service onStartCommand");
                M3U8Uitl m3U8Uitl = new M3U8Uitl(mContext);
                m3U8Uitl.download(lessonId, courseId);
            }
        }.start();

        return super.onStartCommand(intent, flags, startId);
    }


    private void startTimer() {
        if (mTimer != null) {
            return;
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(null, "notificationHashMap->" + notificationHashMap.size());
                if (notificationHashMap.isEmpty()) {
                    mTimer.cancel();
                    mTimer = null;
                    return;
                }
                for (Long index : notificationHashMap.keySet()) {

                }
            }
        }, 0, 100);
    }

    private void createNotification(long id) {
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        // 定义Notification的各种属性
        Notification notification = new Notification(R.drawable.notification_download_icon,
                "正在下载 " + mNotifiTitle, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_LIGHTS;

        Intent notificationIntent = new Intent();
        PendingIntent contentItent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = contentItent;
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_layout);

        remoteViews.setTextViewText(R.id.notify_title, mNotifiTitle);
        remoteViews.setTextViewText(R.id.notify_percent, "0%");
        notification.contentView = remoteViews;

        // 把Notification传递给NotificationManager
        notificationManager.notify((int) id, notification);
        notificationHashMap.put(id, notification);
    }

    private void showComplteNotification(File file) {
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        // 定义Notification的各种属性
        Notification notification = new Notification(R.drawable.notification_download_icon,
                "下载完成 " + mNotifiTitle, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.tickerText = "下载完成";

        Intent notificationIntent = AppUtil.getViewFileIntent(file);
        PendingIntent contentItent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        notification.contentIntent = contentItent;

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_layout);

        remoteViews.setTextViewText(R.id.notify_title, mNotifiTitle);
        remoteViews.setViewVisibility(R.id.notify_progress, View.GONE);
        remoteViews.setViewVisibility(R.id.notify_finish, View.VISIBLE);
        remoteViews.setTextViewText(R.id.notify_finish, "下载完成");
        remoteViews.setTextViewText(R.id.notify_percent, "100%");
        notification.contentView = remoteViews;

        // 把Notification传递给NotificationManager
        notificationManager.notify(0, notification);
    }

    private void finishNotification(long id, String filename) {
        Notification notification = notificationHashMap.get(id);
        if (notification == null) {
            return;
        }
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.tickerText = "下载完成";

        Intent notificationIntent = AppUtil.getViewFileIntent(new File(filename));
        PendingIntent contentItent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        notification.contentIntent = contentItent;

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_layout);

        remoteViews.setTextViewText(R.id.notify_title, mNotifiTitle);
        remoteViews.setViewVisibility(R.id.notify_progress, View.GONE);
        remoteViews.setViewVisibility(R.id.notify_finish, View.VISIBLE);
        remoteViews.setTextViewText(R.id.notify_finish, "下载完成");
        remoteViews.setTextViewText(R.id.notify_percent, "100%");
        notification.contentView = remoteViews;
        notificationManager.notify((int) id, notification);
        EdusohoApp.app.sendMsgToTarget(LessonResourceActivity.INIT_STATUS, null, LessonResourceActivity.class);
    }

    private void updateNotification(long id, int total, int download) {
        Notification notification = notificationHashMap.get(id);
        if (notification == null) {
            return;
        }

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_layout);

        remoteViews.setTextViewText(R.id.notify_title, mNotifiTitle);
        remoteViews.setProgressBar(R.id.notify_progress, total, download, false);
        float percent = (download / (float) total);
        remoteViews.setTextViewText(R.id.notify_percent, (int) (percent * 100) + "%");
        notification.contentView = remoteViews;
        notificationManager.notify((int) id, notification);
    }

    public String getFilePathFromUri(Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.MediaColumns.DATA};
            ContentResolver contentResolver = mContext.getContentResolver();

            Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                    null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if ("file".equals(uri.getScheme())) {
            filePath = new File(uri.getPath()).getAbsolutePath();
        }
        Log.d(null, "filePath=" + filePath);
        return filePath;
    }
}
