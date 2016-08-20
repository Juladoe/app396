package com.edusoho.kuozhi.v3.factory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.factory.provider.AbstractProvider;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.ui.ClassroomDiscussActivity;
import com.edusoho.kuozhi.v3.ui.ImChatActivity;
import com.edusoho.kuozhi.v3.ui.NewsCourseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.google.gson.Gson;

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

    public void showNotification(MessageBody messageBody)  {
        String content = "", title = "你有一条新消息";
        String type = messageBody.getType();
        switch (type) {
            case PushUtil.ChatMsgType.IMAGE:
                content = String.format("[%s]", Const.MEDIA_IMAGE);
                break;
            case PushUtil.ChatMsgType.AUDIO:
                content = String.format("[%s]", Const.MEDIA_AUDIO);
                break;
            case PushUtil.ChatMsgType.MULTI:
                RedirectBody redirectBody = new Gson().fromJson(messageBody.getBody(), RedirectBody.class);
                 content = redirectBody.content;
                break;
            case PushUtil.ChatMsgType.TEXT:
                content = String.format("%s:%s", messageBody.getSource().getNickname(), messageBody.getBody());
                break;
            default:
                title = "你有一条新消息";
        }

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageBody.getConvNo().hashCode(), createNotification(title, content, getNotifyIntent(messageBody)));
    }

    private Intent getNotifyIntent(MessageBody messageBody) {
        Intent notifyIntent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        String type = messageBody.getDestination().getType();
        switch (type) {
            case Destination.USER:
                notifyIntent.putExtra(ImChatActivity.FROM_ID, messageBody.getSource().getId());
                notifyIntent.putExtra(Const.INTENT_TARGET, ImChatActivity.class);
                break;
            case Destination.CLASSROOM:
                notifyIntent.putExtra(ImChatActivity.FROM_ID, messageBody.getDestination().getId());
                notifyIntent.putExtra(Const.INTENT_TARGET, ClassroomDiscussActivity.class);
                break;
            case Destination.COURSE:
                notifyIntent.putExtra(NewsCourseActivity.COURSE_ID, messageBody.getDestination().getId());
                notifyIntent.putExtra(Const.INTENT_TARGET, NewsCourseActivity.class);
                notifyIntent.putExtra(NewsCourseActivity.SHOW_TYPE, NewsCourseActivity.DISCUSS_TYPE);
                break;
        }
        return notifyIntent;
    }

    private Notification createNotification(String title, String content, Intent notifyIntent) {
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
        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | EdusohoApp.app.config.msgSound | EdusohoApp.app.config.msgVibrate);
        return mBuilder.build();
    }
}
