package com.edusoho.kuozhi.v3.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.ui.ChatActivity;

/**
 * Created by JesseHuang on 15/7/4.
 */
public class NotificationUtil {
    public static void showNotification(WrapperXGPushTextMessage xgMessage) {
        try {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(EdusohoApp.app.mContext).setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(xgMessage.title)
                            .setContentText(xgMessage.content).setAutoCancel(true);
            NotificationManager mNotificationManager =
                    (NotificationManager) EdusohoApp.app.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notifyIntent = new Intent(EdusohoApp.app.mContext, ChatActivity.class);
            Chat chat = new Chat(xgMessage);
            int notificationId = chat.fromId;
            notifyIntent.putExtra(ChatActivity.FROM_ID, chat.fromId);
            notifyIntent.putExtra(ChatActivity.TITLE, xgMessage.title);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //int requestCode = (int) SystemClock.uptimeMillis();
            PendingIntent pendIntent = PendingIntent.getActivity(EdusohoApp.app.mContext, notificationId,
                    notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendIntent);
            mNotificationManager.notify(notificationId, mBuilder.build());
        } catch (Exception ex) {
            Log.d("showNotification-->", ex.getMessage());
        }
    }

    public static void cancelById(int id) {
        NotificationManager mNotificationManager =
                (NotificationManager) EdusohoApp.app.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }
}
