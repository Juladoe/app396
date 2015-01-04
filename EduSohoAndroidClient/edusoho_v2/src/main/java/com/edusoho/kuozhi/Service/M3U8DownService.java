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
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.course.LocalCoruseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.M3U8Uitl;
import com.edusoho.kuozhi.util.SqliteUtil;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import cn.trinea.android.common.util.SqliteUtils;
import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by howzhi on 14-10-11.
 */
public class M3U8DownService extends Service {

    private Context mContext;

    public static final int UPDATE = 0001;

    private NotificationManager notificationManager;
    private SparseArray<Notification> notificationList;
    private SparseArray<M3U8Uitl> mM3U8UitlList;
    private ScheduledThreadPoolExecutor mThreadPoolExecutor;

    private DownLoadStatusReceiver mDownLoadStatusReceiver;
    private static M3U8DownService mService;

    private static final String TAG = "M3U8DownService";

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDownLoadStatusReceiver);
        Log.d(TAG, "m3u8 download_service destory");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "m3u8 download_service create");
        mService = this;
        mContext = this;

        notificationList = new SparseArray<Notification>();
        mM3U8UitlList = new SparseArray<M3U8Uitl>();
        mThreadPoolExecutor = new ScheduledThreadPoolExecutor(3);
        mThreadPoolExecutor.setMaximumPoolSize(4);
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        mDownLoadStatusReceiver = new DownLoadStatusReceiver(mStatusCallback);
        registerReceiver(mDownLoadStatusReceiver, new IntentFilter(DownLoadStatusReceiver.ACTION));
    }

    public static M3U8DownService getService()
    {
        return mService;
    }

    /**
     * todo suju
     * @param lessonId
     * @return
     */
    public boolean isExistsDownTask(int lessonId)
    {
        return mM3U8UitlList.indexOfKey(lessonId) >= 0;
    }

    public void cancleDownloadTask(int lessonId)
    {
        M3U8Uitl m3U8Uitl = mM3U8UitlList.get(lessonId);
        if (m3U8Uitl != null) {
            m3U8Uitl.cancelDownload();
            mM3U8UitlList.remove(lessonId);
            notificationList.remove(lessonId);
            notificationManager.cancel(lessonId);
        }
    }

    public void cancelAllDownloadTask()
    {
        int size = mM3U8UitlList.size();
        for (int i=0; i<size; i++) {
            cancleDownloadTask(mM3U8UitlList.keyAt(i));
        }
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
        if (intent == null) {
            return 0;
        }
        final int lessonId = intent.getIntExtra("lessonId", 0);
        final int courseId = intent.getIntExtra("courseId", 0);
        final String lessonTitle = intent.getStringExtra("title");

        startTask(lessonId, courseId, lessonTitle);
        return super.onStartCommand(intent, flags, startId);
    }

    private void startTask(
            final int lessonId, final int courseId,final String lessonTitle)
    {
        mThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (EdusohoApp.app.loginUser == null) {
                    return;
                }
                Log.d(TAG, "m3u8 download_service onStartCommand");
                if (mM3U8UitlList.size() > 2) {
                    Log.d(TAG, "mM3U8UitlList list is full " + mM3U8UitlList.size());
                    return;
                }

                M3U8Uitl m3U8Uitl = new M3U8Uitl(mContext);
                mM3U8UitlList.put(lessonId, m3U8Uitl);
                createNotification(lessonId, lessonTitle);
                m3U8Uitl.download(lessonId, courseId, EdusohoApp.app.loginUser.id);
            }
        });
    }

    private void createNotification(int lessonId, String title) {
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        // 定义Notification的各种属性
        Notification notification = new Notification(R.drawable.notification_download_icon,
                "正在下载 " + title, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_LIGHTS;

        Intent notificationIntent = new Intent(this, LocalCoruseActivity.class);
        PendingIntent contentItent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = contentItent;
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_layout);

        remoteViews.setTextViewText(R.id.notify_title, title);
        remoteViews.setTextViewText(R.id.notify_percent, "0%");
        notification.contentView = remoteViews;

        // 把Notification传递给NotificationManager
        notificationManager.notify(lessonId, notification);
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
        notificationManager.notify(lessonId, notification);
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
        notificationManager.cancel(lessonId);
        notificationManager.notify(lessonId, notification);
        mM3U8UitlList.remove(lessonId);
    }

    private StatusCallback mStatusCallback = new StatusCallback() {
        @Override
        public void invoke(Intent intent) {
            int lessonId = intent.getIntExtra(Const.LESSON_ID, 0);
            M3U8Uitl m3U8Uitl = mM3U8UitlList.get(lessonId);
            User loginUser = EdusohoApp.app.loginUser;
            if (m3U8Uitl == null || loginUser == null) {
                return;
            }
            String title = m3U8Uitl.getLessonTitle();
            M3U8DbModle m3U8DbModle = M3U8Uitl.queryM3U8Modle(
                    mContext, loginUser.id, lessonId, EdusohoApp.app.domain, M3U8Uitl.ALL);
            if (m3U8DbModle == null) {
                return;
            }
            if (m3U8DbModle.downloadNum == m3U8DbModle.totalNum) {
                Log.d(TAG, "showComplteNotification " + lessonId);
                showComplteNotification(lessonId, title);
                startDownloadLasterTask();
            } else {
                Log.d(TAG, "updateNotification " + lessonId);
                updateNotification(lessonId, title, m3U8DbModle.totalNum, m3U8DbModle.downloadNum);
            }
        }
    };

    public void startDownloadLasterTask()
    {
        User loginUser = EdusohoApp.app.loginUser;
        if (loginUser == null) {
            return;
        }
        ArrayList<M3U8DbModle> m3U8DbModles = M3U8Uitl.queryM3U8DownTasks(
                mContext, EdusohoApp.app.domain, loginUser.id);
        int size = m3U8DbModles.size();
        size = size > 3 ? 3 : size;

        for (int i =0; i < size; i++) {
            M3U8DbModle m3U8DbModle = m3U8DbModles.get(i);
            LessonItem lessonItem = SqliteUtil.getUtil(mContext).queryForObj(
                    new TypeToken<LessonItem>(){},
                    "where type=? and key=?",
                    Const.CACHE_LESSON_TYPE,
                    "lesson-" + m3U8DbModle.lessonId
            );
            if (mM3U8UitlList.indexOfKey(m3U8DbModle.lessonId) >= 0) {
                continue;
            }
            startTask(m3U8DbModle.lessonId, lessonItem.courseId, lessonItem.title);
        }
    }
}
