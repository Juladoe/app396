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
import com.edusoho.kuozhi.v3.model.bal.article.Article;
import com.edusoho.kuozhi.v3.model.bal.article.ArticleModel;
import com.edusoho.kuozhi.v3.model.bal.push.Bulletin;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.ui.BulletinActivity;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.ClassroomDiscussActivity;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.ui.NewsCourseActivity;
import com.edusoho.kuozhi.v3.ui.ServiceProviderActivity;
import com.edusoho.kuozhi.v3.ui.ThreadDiscussActivity;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by JesseHuang on 15/7/4.
 */
public class NotificationUtil {
    public static WrapperXGPushTextMessage mMessage = null;
    public static final int DISCOUNT_ID = -1;

    public static void showMsgNotification(Context context, WrapperXGPushTextMessage xgMessage) {
        try {
            Chat chat = new Chat(xgMessage);
            New newModel = new New(xgMessage);
            switch (chat.type) {
                case PushUtil.ChatMsgType.IMAGE:
                    xgMessage.content = String.format("[%s]", Const.MEDIA_IMAGE);
                    break;
                case PushUtil.ChatMsgType.AUDIO:
                    xgMessage.content = String.format("[%s]", Const.MEDIA_AUDIO);
                    break;
                case PushUtil.ChatMsgType.MULTI:
                    RedirectBody redirectBody = new Gson().fromJson(xgMessage.content, RedirectBody.class);
                    xgMessage.content = redirectBody.content;
                    break;
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context).setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(xgMessage.title)
                            .setContentText(xgMessage.content).setAutoCancel(true);

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notifyIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notifyIntent.putExtra(ChatActivity.FROM_ID, chat.fromId);
            notifyIntent.putExtra(ChatActivity.HEAD_IMAGE_URL, chat.headImgUrl);
            notifyIntent.putExtra(Const.ACTIONBAR_TITLE, xgMessage.title);
            notifyIntent.putExtra(Const.NEWS_TYPE, newModel.type);
            notifyIntent.putExtra(Const.INTENT_TARGET, ChatActivity.class);
            if (isAppExit(context)) {
                mMessage = xgMessage;
            }
            PendingIntent pendIntent = PendingIntent.getActivity(context, chat.fromId,
                    notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendIntent);
            mBuilder.setDefaults(EdusohoApp.app.config.msgSound | EdusohoApp.app.config.msgVibrate);
            mNotificationManager.notify(chat.fromId, mBuilder.build());
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
            int bulletinId = bulletin.id;

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notifyIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notifyIntent.putExtra(Const.INTENT_TARGET, BulletinActivity.class);
            if (isAppExit(context)) {
                mMessage = xgMessage;
            }
            PendingIntent pendIntent = PendingIntent.getActivity(context, bulletinId,
                    notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendIntent);
            mBuilder.setDefaults(EdusohoApp.app.config.msgSound | EdusohoApp.app.config.msgVibrate);
            mNotificationManager.notify(bulletinId, mBuilder.build());
        } catch (Exception ex) {
            Log.d("BulletinNotification-->", ex.getMessage());
        }
    }

    public static void showNewsCourseNotification(Context context, WrapperXGPushTextMessage xgMessage) {
        V2CustomContent v2CustomContent = xgMessage.getV2CustomContent();
        switch (v2CustomContent.getBody().getType()) {
            case PushUtil.CourseType.HOMEWORK_REVIEWED:
                xgMessage.content = String.format("您的作业『%s』已经批阅完成", xgMessage.content);
                break;
            case PushUtil.CourseType.QUESTION_ANSWERED:
                xgMessage.content = String.format("您的问题『%s』有新的回复", xgMessage.content);
                break;
            case PushUtil.CourseType.LESSON_START:
                xgMessage.content = String.format("您的课时『%s』已经开始学习", xgMessage.content);
                break;
            case PushUtil.CourseType.LESSON_FINISH:
                xgMessage.content = String.format("您的课时『%s』学习完成", xgMessage.content);
                break;
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context).setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(xgMessage.title)
                        .setContentText(xgMessage.content).setAutoCancel(true);

        int courseId = v2CustomContent.getFrom().getId();

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifyIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        New newModel = new New();
        newModel.title = xgMessage.title;
        newModel.fromId = courseId;
        newModel.type = v2CustomContent.getFrom().getType();
        newModel.imgUrl = v2CustomContent.getFrom().getImage();
        notifyIntent.putExtra(Const.NEW_ITEM_INFO, newModel);
        notifyIntent.putExtra(Const.INTENT_TARGET, NewsCourseActivity.class);
        if (isAppExit(context)) {
            mMessage = xgMessage;
        }

        PendingIntent pendIntent = PendingIntent.getActivity(context, courseId,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendIntent);
        mBuilder.setDefaults((EdusohoApp.app.config.msgSound | EdusohoApp.app.config.msgVibrate) & EdusohoApp.app.getMsgDisturbFromCourseId(courseId));
        mNotificationManager.notify(courseId, mBuilder.build());
    }

    public static void showDiscountPass(Context context, WrapperXGPushTextMessage xgMessage) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context).setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(xgMessage.title)
                        .setContentText(xgMessage.content).setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifyIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (isAppExit(context)) {
            notifyIntent.putExtra(Const.SWITCH_NEWS_TAB, PushUtil.DiscountType.DISCOUNT);
        }

        PendingIntent pendIntent = PendingIntent.getActivity(context, DISCOUNT_ID,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendIntent);
        mBuilder.setDefaults((EdusohoApp.app.config.msgSound | EdusohoApp.app.config.msgVibrate));
        mNotificationManager.notify(DISCOUNT_ID, mBuilder.build());
    }

    public static void showArticleNotification(Context context, WrapperXGPushTextMessage xgMessage) {
        ArticleModel articleModel = new ArticleModel(xgMessage);
        int notificationId = articleModel.spId;

        List<Article> articleList = articleModel.articleList;
        String content = articleList.isEmpty() ? "有一条新资讯" : articleList.get(0).title;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context).setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(xgMessage.title)
                        .setContentText(content).setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifyIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notifyIntent.putExtra(Const.ACTIONBAR_TITLE, xgMessage.title);
        notifyIntent.putExtra(ServiceProviderActivity.SERVICE_TYPE, ServiceProviderActivity.ARTICLE);
        notifyIntent.putExtra(ServiceProviderActivity.SERVICE_ID, articleModel.spId);
        notifyIntent.putExtra(Const.INTENT_TARGET, ServiceProviderActivity.class);
        if (isAppExit(context)) {
            mMessage = xgMessage;
        }

        PendingIntent pendIntent = PendingIntent.getActivity(context, notificationId,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendIntent);
        mBuilder.setDefaults((EdusohoApp.app.config.msgSound | EdusohoApp.app.config.msgVibrate) & EdusohoApp.app.getMsgDisturbFromCourseId(articleModel.spId));
        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    public static void showClassroomDiscussMsg(Context context, WrapperXGPushTextMessage xgMessage) {
        try {
            V2CustomContent model = xgMessage.getV2CustomContent();
            switch (model.getBody().getType()) {
                case PushUtil.ChatMsgType.IMAGE:
                    xgMessage.content = String.format("[%s]", Const.MEDIA_IMAGE);
                    break;
                case PushUtil.ChatMsgType.AUDIO:
                    xgMessage.content = String.format("[%s]", Const.MEDIA_AUDIO);
                    break;
                case PushUtil.ChatMsgType.MULTI:
                    RedirectBody redirectBody = new Gson().fromJson(xgMessage.content, RedirectBody.class);
                    xgMessage.content = redirectBody.content;
                    break;
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context).setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(xgMessage.title)
                            .setContentText(model.getFrom().getNickname() + ": " + xgMessage.content).setAutoCancel(true);

            int fromId = model.getTo().getId();

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notifyIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notifyIntent.putExtra(ClassroomDiscussActivity.FROM_ID, model.getTo().getId());
            notifyIntent.putExtra(ClassroomDiscussActivity.CLASSROOM_IMAGE, model.getTo().getImage());
            notifyIntent.putExtra(Const.ACTIONBAR_TITLE, xgMessage.title);
            notifyIntent.putExtra(Const.INTENT_TARGET, ClassroomDiscussActivity.class);
            if (isAppExit(context)) {
                mMessage = xgMessage;
            }
            PendingIntent pendIntent = PendingIntent.getActivity(context, fromId,
                    notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendIntent);
            mBuilder.setDefaults(EdusohoApp.app.config.msgSound | EdusohoApp.app.config.msgVibrate);
            mNotificationManager.notify(fromId, mBuilder.build());
        } catch (Exception ex) {
            Log.d("Classroom-->", ex.getMessage());
        }
    }

    public static void showCourseDiscuss(Context context, WrapperXGPushTextMessage xgMessage) {
        try {
            V2CustomContent model = xgMessage.getV2CustomContent();
            switch (model.getBody().getType()) {
                case PushUtil.ChatMsgType.IMAGE:
                    xgMessage.content = String.format("[%s]", Const.MEDIA_IMAGE);
                    break;
                case PushUtil.ChatMsgType.AUDIO:
                    xgMessage.content = String.format("[%s]", Const.MEDIA_AUDIO);
                    break;
                case PushUtil.ChatMsgType.MULTI:
                    RedirectBody redirectBody = new Gson().fromJson(xgMessage.content, RedirectBody.class);
                    xgMessage.content = redirectBody.content;
                    break;
            }


            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context).setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(xgMessage.title)
                            .setContentText(model.getFrom().getNickname() + ": " + xgMessage.content).setAutoCancel(true);

            int fromId = model.getTo().getId();

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notifyIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            New newModel = new New();
            newModel.title = xgMessage.title;
            newModel.fromId = model.getTo().getId();
            newModel.type = model.getTo().getType();
            newModel.imgUrl = model.getTo().getImage();
            notifyIntent.putExtra(Const.NEW_ITEM_INFO, newModel);
            notifyIntent.putExtra(Const.INTENT_TARGET, NewsCourseActivity.class);
            if (isAppExit(context)) {
                mMessage = xgMessage;
            }
            PendingIntent pendIntent = PendingIntent.getActivity(context, fromId,
                    notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendIntent);
            mBuilder.setDefaults(EdusohoApp.app.config.msgSound | EdusohoApp.app.config.msgVibrate);
            mNotificationManager.notify(fromId, mBuilder.build());
        } catch (Exception ex) {
            Log.d("Classroom-->", ex.getMessage());
        }
    }

    public static void showThreadPost(Context context, WrapperXGPushTextMessage xgMessage) {
        try {
            V2CustomContent model = xgMessage.getV2CustomContent();
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(EdusohoApp.app.defaultSchool.name).setContentText("你收到一个问题的回复").setAutoCancel(true);
            int threadId = model.getBody().getThreadId();
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notifyIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notifyIntent.putExtra(ThreadDiscussActivity.THREAD_ID, model.getBody().getThreadId());
            notifyIntent.putExtra(ThreadDiscussActivity.COURSE_ID, model.getBody().getCourseId());
            notifyIntent.putExtra(ThreadDiscussActivity.LESSON_ID, model.getBody().getLessonId());
            notifyIntent.putExtra(ThreadDiscussActivity.ACTIVITY_TYPE, model.getType());
            notifyIntent.putExtra(Const.INTENT_TARGET, ThreadDiscussActivity.class);
            if (isAppExit(context)) {
                mMessage = xgMessage;
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(context, threadId, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setDefaults(EdusohoApp.app.config.msgSound | EdusohoApp.app.config.msgVibrate);
            mNotificationManager.notify(threadId, mBuilder.build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
