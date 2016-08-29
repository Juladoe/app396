package com.edusoho.kuozhi.v3.factory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.factory.provider.AbstractProvider;

/**
 * Created by 菊 on 2016/5/15.
 */
public class NotificationProvider extends AbstractProvider {

    public NotificationProvider(Context context)
    {
        super(context);
    }

    public void cancelNotification(int convNoHashCode) {
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(convNoHashCode);
    }

    public void showNotification(boolean mute, int notifiId, String title, String content, Intent notifyIntent)  {
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifiId, createNotification(mute, title, content, notifyIntent));
    }

    private Notification createNotification(boolean mute, String title, String content, Intent notifyIntent) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext).setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content).setAutoCancel(true);

        PendingIntent pendIntent = PendingIntent.getActivity(mContext, 0,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendIntent);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setCategory(Notification.CATEGORY_MESSAGE);
        int config = Notification.DEFAULT_LIGHTS;
        if (!mute) {
            config = config | EdusohoApp.app.config.msgSound | EdusohoApp.app.config.msgVibrate;
        }
        mBuilder.setDefaults(config);
        return mBuilder.build();
    }
}
