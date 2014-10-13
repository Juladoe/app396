package com.edusoho.kuozhi.Service;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.RemoteViews;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.LessonMaterial;
import com.edusoho.kuozhi.ui.lesson.LessonResourceActivity;
import com.edusoho.kuozhi.util.AppUtil;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import cn.trinea.android.common.util.FileUtils;


/**
 * Created by howzhi on 14-10-11.
 */
public class DownLoadService extends Service {

    private String mNotifiTitle;
    private int mFileSize;
    private String mFileUrl;
    private Context mContext;
    private AQuery aQuery;
    private int mCount;

    public static final int UPDATE = 0001;

    private HashMap<Long, Notification> notificationHashMap;
    private Timer mTimer;
    private DownloadManager downloadManager;
    private NotificationManager notificationManager;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE:
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        aQuery = new AQuery(mContext);
        notificationManager = (NotificationManager)
                this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        notificationHashMap = new HashMap<Long, Notification>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startDown(Context context, LessonMaterial lessonMaterial, String url)
    {
        Intent intent = new Intent();
        intent.putExtra("title", lessonMaterial.title);
        intent.putExtra("size", lessonMaterial.fileSize);
        intent.putExtra("url", url);
        intent.setClass(context, DownLoadService.class);
        context.startService(intent);
    }

    public static void startDown(
            Context context, ArrayList<LessonMaterial> lessonMaterials)
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            mNotifiTitle = intent.getStringExtra("title");
            mFileUrl = intent.getStringExtra("url");
            mFileSize = intent.getIntExtra("size", 0);
        }
        downLoadFile();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startTimer()
    {
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
                    queryDownloadStatus(index);
                }
            }
        }, 0, 100);
    }


    private void downLoadFile()
    {
        Log.d(null, "file->"+ mFileUrl);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mFileUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(mNotifiTitle));
        request.setMimeType(mimeString);

        //在通知栏中显示
        request.setShowRunningNotification(false);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir("edusoho", mNotifiTitle);

        long id = downloadManager.enqueue(request);
        createNotification(id);
        startTimer();
    }

    private void createNotification(long id)
    {
        notificationManager = (NotificationManager)
                this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        // 定义Notification的各种属性
        Notification notification = new Notification(R.drawable.notification_download_icon,
                "正在下载 " + mNotifiTitle, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.defaults = Notification.DEFAULT_LIGHTS;

        Intent notificationIntent = new Intent();
        PendingIntent contentItent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = contentItent;
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification_layout);

        remoteViews.setTextViewText(R.id.notify_title, mNotifiTitle);
        remoteViews.setProgressBar(R.id.notify_progress, mFileSize, 0, false);
        remoteViews.setTextViewText(R.id.notify_percent, "0%");
        notification.contentView = remoteViews;

        // 把Notification传递给NotificationManager
        notificationManager.notify((int)id, notification);
        notificationHashMap.put(id, notification);
    }

    private void finishNotification(long id, String filename)
    {
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
        notificationManager.notify((int)id, notification);
        EdusohoApp.app.sendMsgToTarget(LessonResourceActivity.INIT_STATUS, null, LessonResourceActivity.class);
    }

    private void updateNotification(long id, int total, int download)
    {
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
        notificationManager.notify((int)id, notification);
    }

    private void queryDownloadStatus(long id) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor c = downloadManager.query(query);
        if(c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch(status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.v("down", "STATUS_PAUSED");
                case DownloadManager.STATUS_PENDING:
                    Log.v("down", "STATUS_PENDING");
                case DownloadManager.STATUS_RUNNING:
                    //正在下载，不做任何事情
                    int total = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    int download = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));

                    Log.v("down", "total->" + total + " download->" + download);
                    updateNotification(id, total, download);
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //完成
                    downloadManager.remove(id);
                    String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                    Log.v("down", "下载完成->" + filename);
                    moveToCache(new File(filename));
                    finishNotification(id, filename);
                    notificationHashMap.remove(id);
                    break;
                case DownloadManager.STATUS_FAILED:
                    //清除已下载的内容，重新下载
                    Log.v("down", "STATUS_FAILED");
                    downloadManager.remove(id);
                    notificationManager.cancel((int)id);
                    notificationHashMap.remove(id);
                    break;
            }

            c.close();
        }
    }


    private void moveToCache(File file)
    {
        File cacheDir = AQUtility.getCacheDir(mContext);
        File targetFile = new File(cacheDir, file.getName());
        try {
            FileUtils.copyFile(file.getAbsolutePath(), targetFile.getAbsolutePath());
            file.delete();

            Log.v("move", "移动完成->" + targetFile);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
