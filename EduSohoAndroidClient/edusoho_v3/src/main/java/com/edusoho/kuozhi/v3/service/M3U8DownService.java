package com.edusoho.kuozhi.v3.service;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.RemoteViews;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.broadcast.DownloadStatusReceiver;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.m3u8.DownloadModel;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModel;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.DownloadManagerActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
/**
 * Created by howzhi on 14-10-11.
 */
public class M3U8DownService extends Service {
    private Context mContext;
    private NotificationManager notificationManager;
    private SparseArray<Notification> notificationList;
    private SparseArray<M3U8Util> mM3U8UitlList = new SparseArray<M3U8Util>();
    private ScheduledThreadPoolExecutor mThreadPoolExecutor;
    private ScheduledThreadPoolExecutor mUpdateThreadPoolExecutor;
    private static M3U8DownService mService;
    private static final String TAG = "M3U8DownService";
    private Object mLock = new Object();
    private Handler mContentObserverHandler = new Handler();
    protected ContentObserver mDownloadContentObserver = new ContentObserver(mContentObserverHandler) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
        }
    };


    protected DownloadStatusReceiver mDownLoadStatusReceiver = new DownloadStatusReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            int status = intent.getIntExtra(Const.STATUS, M3U8Util.NONE);
            int lessonId = intent.getIntExtra(Const.LESSON_ID, 0);
            if (status == M3U8Util.ERROR) {
                cancelDownloadTask(lessonId, M3U8Util.ERROR);
            }
        }
    };
    private BroadcastReceiver mDownLoadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String url = intent.getStringExtra(HttpClientDownloadService.DOWNLOAD_COMPLETE_URL);
            int status = intent.getIntExtra(HttpClientDownloadService.DOWNLOAD_STATUS, DownloadManager.STATUS_FAILED);
            DownloadModel downloadModel = getDownloadModel(url);
            Log.d(TAG, "complete:" + downloadModel);
            if (downloadModel == null) {
                return;
            }
            mThreadPoolExecutor.submit(new UpdateRunnable(status, downloadModel));
        }
    };


    private DownloadModel getDownloadModel(String url) {
        SqliteUtil.QueryParser<DownloadModel> queryCallBack =
                new SqliteUtil.QueryParser<DownloadModel>() {
                    @Override
                    public DownloadModel parse(Cursor cursor) {
                        DownloadModel downloadModel = new DownloadModel();
                        downloadModel.url = cursor.getString(cursor.getColumnIndex("url"));
                        downloadModel.type = cursor.getString(cursor.getColumnIndex("type"));
                        downloadModel.targetId = cursor.getInt(cursor.getColumnIndex("targetId"));
                        downloadModel.reference = cursor.getInt(cursor.getColumnIndex("reference"));
                        downloadModel.id = cursor.getInt(cursor.getColumnIndex("id"));
                        return downloadModel;

                    }
                };
        return SqliteUtil.getUtil(mContext).query(
                queryCallBack,
                "select * from download_item where url=?",
                url
        );
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mThreadPoolExecutor.shutdown();
        mUpdateThreadPoolExecutor.shutdown();
        unregisterReceiver(mDownLoadCompleteReceiver);
        unregisterReceiver(mDownLoadStatusReceiver);
        getContentResolver().unregisterContentObserver(mDownloadContentObserver);
        Log.d(TAG, "m3u8 download_service destroy");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "m3u8 download_service create");
        mService = this;
        mContext = this;
        notificationList = new SparseArray<>();
        mUpdateThreadPoolExecutor = new ScheduledThreadPoolExecutor(5);
        mUpdateThreadPoolExecutor.setMaximumPoolSize(10);
        mThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        mThreadPoolExecutor.setMaximumPoolSize(1);
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        registerReceiver(mDownLoadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        registerReceiver(mDownLoadStatusReceiver, new IntentFilter(DownloadStatusReceiver.ACTION));
    }
    public static M3U8DownService getService() {
        return mService;
    }
    public void cancelDownloadTask(int lessonId) {
        cancelDownloadTask(lessonId, M3U8Util.PAUSE);
    }
    public void cancelDownloadTask(int lessonId, int status) {
        if (mM3U8UitlList.indexOfKey(lessonId) < 0) {
            return;
        }
        M3U8Util m3U8Util = mM3U8UitlList.get(lessonId);
        m3U8Util.cancelDownload();
        m3U8Util.setDownloadStatus(status);
        notificationList.remove(lessonId);
        notificationManager.cancel(lessonId);
    }

    public void cancelAllDownloadTask() {
        int size = mM3U8UitlList.size();
        for (int i = 0; i < size; i++) {
            cancelDownloadTask(mM3U8UitlList.keyAt(i), M3U8Util.PAUSE);
        }
    }

    public static Intent getIntent(Context context) {
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

    public void changeTaskState(int lessonId, int courseId, String lessonTitle) {
        M3U8Util m3U8Util = mM3U8UitlList.get(lessonId);
        if (m3U8Util == null) {
            Log.d(TAG, "changeTaskState: no task");
            startTask(lessonId, courseId, lessonTitle);
            return;
        }
        int state = m3U8Util.getDownloadStatus();
        switch (state) {
            case M3U8Util.NONE:
                if (hasDownloadingTask()) {
                    m3U8Util.setDownloadStatus(M3U8Util.PAUSE);
                    return;
                }
                startTask(lessonId, courseId, lessonTitle);
                break;
            case M3U8Util.DOWNING:
                cancelDownloadTask(lessonId, M3U8Util.PAUSE);
                break;
            case M3U8Util.PAUSE:
                if (hasDownloadingTask()) {
                    m3U8Util.setDownloadStatus(M3U8Util.NONE);
                    return;
                }
                m3U8Util.setDownloadStatus(M3U8Util.NONE);
                startTask(lessonId, courseId, lessonTitle);
                break;
            case M3U8Util.ERROR:
                if (hasDownloadingTask()) {
                    return;
                }
                startTask(lessonId, courseId, lessonTitle);
        }
    }

    public void startTask(int lessonId, int courseId, String lessonTitle) {
        User loginUser = getAppSettingProvider().getCurrentUser();
        if (loginUser == null) {
            MobclickAgent.reportError(mContext, String.format("token_lose lessonId:%d, courseId:%d", lessonId, courseId));
            CommonUtil.longToast(getBaseContext(), getString(R.string.token_lose_notice));
            return;
        }
        Log.d(TAG, "startTask m3u8");
        M3U8Util m3U8Util = mM3U8UitlList.get(lessonId);
        if (m3U8Util == null) {
            m3U8Util = new M3U8Util(mContext);
            mM3U8UitlList.put(lessonId, m3U8Util);
            Log.d(TAG, "add m3u8 download");
            MobclickAgent.reportError(mContext, String.format("add m3u8 download lessonId:%d, courseId:%d", lessonId, courseId));
        }

        if (m3U8Util.getDownloadStatus() == M3U8Util.PAUSE) {
            return;
        }
        synchronized (mLock) {
            if (hasDownloadingTask()) {
                Log.d(TAG, "has download");
                return;
            }
        }

        createNotification(courseId, lessonId, lessonTitle);
        m3U8Util.download(lessonId, courseId, loginUser.id);
    }

    private boolean hasDownloadingTask() {
        for (int i = 0; i < mM3U8UitlList.size(); i++) {
            M3U8Util m3U8Util = mM3U8UitlList.valueAt(i);
            if (m3U8Util.getDownloadStatus() == M3U8Util.DOWNING) {
                return true;
            }
        }
        return false;
    }

    public int getTaskStatus(int lessonId) {
        if (mM3U8UitlList == null || mM3U8UitlList.size() == 0) {
            return M3U8Util.NONE;
        }

        M3U8Util m3U8Util = mM3U8UitlList.get(lessonId);
        if (m3U8Util == null) {
            return M3U8Util.NONE;
        }

        return m3U8Util.getDownloadStatus();
    }

    private void createNotification(int courseId, int lessonId, String title) {
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        // 定义Notification的各种属性
        Notification notification = new Notification(R.drawable.notification_download_icon,
                "正在下载 " + title, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_LIGHTS;

        Intent notificationIntent = new Intent(this, DownloadManagerActivity.class);
        notificationIntent.putExtra(Const.COURSE_ID, courseId);
        notification.contentIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_layout);

        remoteViews.setTextViewText(R.id.notify_title, title);
        remoteViews.setTextViewText(R.id.notify_percent, "0%");
        notification.contentView = remoteViews;

        // 把Notification传递给NotificationManager
        notificationManager.notify(lessonId, notification);
        notificationList.put(lessonId, notification);
    }

    public void updateNotification(int lessonId, String title, int total, int download) {
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

    public void showCompleteNotification(int lessonId, String title) {
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        // 定义Notification的各种属性
        Notification notification = new Notification(R.drawable.notification_download_icon,
                "下载完成 " + title, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.tickerText = "下载完成";

        Intent notificationIntent = new Intent();
        notification.contentIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

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

    private void updateM3U8DownloadStatus(M3U8Util m3U8Util, int lessonId) {
        User loginUser = getAppSettingProvider().getCurrentUser();
        School school = getAppSettingProvider().getCurrentSchool();
        if (loginUser == null) {
            MobclickAgent.reportError(mContext, String.format("token_lose lessonId:%d", lessonId));
            CommonUtil.longToast(getBaseContext(), getString(R.string.token_lose_notice));
            return;
        }
        String title = m3U8Util.getLessonTitle();

        M3U8DbModel m3U8DbModel = M3U8Util.queryM3U8Model(
                mContext, loginUser.id, lessonId, school.getDomain(), M3U8Util.ALL);
        if (m3U8DbModel == null) {
            return;
        }

        if (m3U8DbModel.downloadNum == m3U8DbModel.totalNum) {
            Log.d(TAG, "showCompleteNotification " + lessonId);
            showCompleteNotification(lessonId, title);
            startDownloadLasterTask();
        } else {
            Log.d(TAG, "updateNotification " + lessonId);
            updateNotification(lessonId, title, m3U8DbModel.totalNum, m3U8DbModel.downloadNum);
        }
    }

    public void startDownloadLasterTask() {
        User loginUser = getAppSettingProvider().getCurrentUser();
        School school = getAppSettingProvider().getCurrentSchool();
        if (loginUser == null || school == null) {
            CommonUtil.longToast(getBaseContext(), getString(R.string.token_lose_notice));
            return;
        }
        ArrayList<M3U8DbModel> m3U8DbModels = M3U8Util.queryM3U8DownTasks(
                mContext, school.getDomain(), loginUser.id);
        int size = m3U8DbModels.size();
        size = size > 3 ? 3 : size;

        for (int i = 0; i < size; i++) {
            M3U8DbModel m3U8DbModel = m3U8DbModels.get(i);
            LessonItem lessonItem = SqliteUtil.getUtil(mContext).queryForObj(
                    new TypeToken<LessonItem>() {
                    },
                    "where type=? and key=?",
                    Const.CACHE_LESSON_TYPE,
                    "lesson-" + m3U8DbModel.lessonId
            );
            startTask(m3U8DbModel.lessonId, lessonItem.courseId, lessonItem.title);
        }
    }

    /*
        update thread task
     */
    class UpdateRunnable implements Runnable {

        private int status;
        private DownloadModel downloadModel;

        public UpdateRunnable(int status, DownloadModel downloadModel) {
            this.status = status;
            this.downloadModel = downloadModel;
        }

        @Override
        public void run() {
            M3U8Util m3U8Util = mM3U8UitlList.get(downloadModel.targetId);
            Log.d(TAG, "status:" + status + " m3U8Util: " + m3U8Util);
            if (m3U8Util == null) {
                return;
            }
            m3U8Util.updateDownloadStatus(downloadModel, status);
            updateM3U8DownloadStatus(m3U8Util, downloadModel.targetId);
        }
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }
}
