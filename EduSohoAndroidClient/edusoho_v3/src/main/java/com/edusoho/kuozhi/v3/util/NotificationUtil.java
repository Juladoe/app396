package com.edusoho.kuozhi.v3.util;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.Bulletin;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.NewsCourseEntity;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.ui.BulletinActivity;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.ui.NewsCourseActivity;

import java.util.List;

/**
 * Created by JesseHuang on 15/7/4.
 */
public class NotificationUtil {
    public static WrapperXGPushTextMessage mMessage = null;

    public static void showMsgNotification(Context context, WrapperXGPushTextMessage xgMessage) {
        try {
            Chat chat = new Chat(xgMessage);
            switch (chat.getFileType()) {
                case IMAGE:
                    xgMessage.content = String.format("[%s]", Const.MEDIA_IMAGE);
                    break;
                case AUDIO:
                    xgMessage.content = String.format("[%s]", Const.MEDIA_AUDIO);
                    break;

            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context).setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(xgMessage.title)
                            .setContentText(xgMessage.content).setAutoCancel(true);

            int notificationId = chat.fromId;

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notifyIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notifyIntent.putExtra(ChatActivity.FROM_ID, chat.fromId);
            notifyIntent.putExtra(Const.INTENT_TARGET, ChatActivity.class);
            if (isAppExit(context)) {
                mMessage = xgMessage;
            }
            notifyIntent.putExtra(ChatActivity.NICKNAME, xgMessage.title);
            PendingIntent pendIntent = PendingIntent.getActivity(context, notificationId,
                    notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendIntent);
            mBuilder.setDefaults(EdusohoApp.app.config.msgSound | EdusohoApp.app.config.msgVibrate);
            mNotificationManager.notify(notificationId, mBuilder.build());
        } catch (Exception ex) {
            Log.d("showMsgNotification-->", ex.getMessage());
        }
    }

    public static void showBulletinNotification(Context context, WrapperXGPushTextMessage xgMessage) {
        try {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context).setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(xgMessage.title)
                            .setContentText(xgMessage.content).setAutoCancel(true);
            Bulletin bulletin = new Bulletin(xgMessage);
            int notificationId = bulletin.id;

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notifyIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notifyIntent.putExtra(Const.INTENT_TARGET, BulletinActivity.class);
            PendingIntent pendIntent = PendingIntent.getActivity(context, notificationId,
                    notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendIntent);
            mBuilder.setDefaults(EdusohoApp.app.config.msgSound | EdusohoApp.app.config.msgVibrate);
            mNotificationManager.notify(notificationId, mBuilder.build());
        } catch (Exception ex) {
            Log.d("showNotification-->", ex.getMessage());
        }
    }

    public static void showNewsCourseNotification(Context context, WrapperXGPushTextMessage xgMessage) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context).setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(xgMessage.title)
                        .setContentText(xgMessage.content).setAutoCancel(true);
        NewsCourseEntity newsCourseEntity = new NewsCourseEntity();
        int notificationId = newsCourseEntity.getCourseId();

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifyIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notifyIntent.putExtra(NewsCourseActivity.COURSE_ID, newsCourseEntity.getCourseId());
        notifyIntent.putExtra(Const.INTENT_TARGET, NewsCourseActivity.class);
        if (isAppExit(context)) {
            mMessage = xgMessage;
        }
        notifyIntent.putExtra(ChatActivity.NICKNAME, xgMessage.title);
        PendingIntent pendIntent = PendingIntent.getActivity(context, notificationId,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendIntent);
        mBuilder.setDefaults(EdusohoApp.app.config.msgSound | EdusohoApp.app.config.msgVibrate);
        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    public static void cancelById(int id) {
        NotificationManager mNotificationManager =
                (NotificationManager) EdusohoApp.app.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }

    public static void cancelAll() {
        NotificationManager mNotificationManager =
                (NotificationManager) EdusohoApp.app.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    /**
     * 判断应用是否在退出状态下接收到消息
     *
     * @param context Context
     * @return 是 true ，否 false
     */
    public static boolean isAppExit(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(1);
        int size = task.size();
        for (int i = 0; i < size; i++) {
            if (task.get(i).baseActivity.getClassName().equals(DefaultPageActivity.class.getName()) ||
                    task.get(i).topActivity.getClassName().equals(DefaultPageActivity.class.getName())) {
                return false;
            }
        }
        return true;
    }
}
