package com.edusoho.kuozhi.v3.service.message;

import android.content.Context;
import android.content.Intent;

import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.imserver.managar.IMBlackListManager;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.NotificationProvider;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.ui.ClassroomDiscussActivity;
import com.edusoho.kuozhi.v3.ui.ImChatActivity;
import com.edusoho.kuozhi.v3.ui.NewsCourseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.google.gson.Gson;

/**
 * Created by 菊 on 2016/4/25.
 */
public abstract class AbstractCommand {

    protected MessageBody mMessageBody;
    protected Context mContext;
    protected IMMessageReceiver mReceiver;

    public AbstractCommand(Context context, IMMessageReceiver receiver, MessageBody messageBody) {
        this.mContext = context;
        this.mReceiver = receiver;
        this.mMessageBody = messageBody;
    }

    public abstract void invoke();

    protected boolean isInBlackList(String convNo) {
        int status = IMClient.getClient().getIMBlackListManager().getBlackListByConvNo(convNo);
        return status == IMBlackListManager.NO_DISTURB;
    }

    protected NotificationProvider getNotificationProvider() {
        return FactoryManager.getInstance().create(NotificationProvider.class);
    }

    protected void showNotification() {
        String[] content = getNotificationContent();
        getNotificationProvider().showNotification(
                mMessageBody.getConvNo().hashCode(), content[0], content[1], getNotifyIntent());
    }

    protected String getNotificationTitle() {
        return "你有一条新消息";
    }

    protected Intent getNotifyIntent() {
        Intent notifyIntent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        String type = mMessageBody.getDestination().getType();
        switch (type) {
            case Destination.USER:
                notifyIntent.putExtra(ImChatActivity.FROM_ID, mMessageBody.getSource().getId());
                notifyIntent.putExtra(Const.INTENT_TARGET, ImChatActivity.class);
                break;
            case Destination.CLASSROOM:
                notifyIntent.putExtra(ImChatActivity.FROM_ID, mMessageBody.getDestination().getId());
                notifyIntent.putExtra(Const.INTENT_TARGET, ClassroomDiscussActivity.class);
                break;
            case Destination.COURSE:
                notifyIntent.putExtra(NewsCourseActivity.COURSE_ID, mMessageBody.getDestination().getId());
                notifyIntent.putExtra(Const.INTENT_TARGET, NewsCourseActivity.class);
                notifyIntent.putExtra(NewsCourseActivity.SHOW_TYPE, NewsCourseActivity.DISCUSS_TYPE);
                break;
        }
        return notifyIntent;
    }

    protected String[] getNotificationContent() {
        String content = "";
        String type = mMessageBody.getType();
        switch (type) {
            case PushUtil.ChatMsgType.IMAGE:
                content = String.format("[%s]", Const.MEDIA_IMAGE);
                break;
            case PushUtil.ChatMsgType.AUDIO:
                content = String.format("[%s]", Const.MEDIA_AUDIO);
                break;
            case PushUtil.ChatMsgType.MULTI:
                RedirectBody redirectBody = new Gson().fromJson(mMessageBody.getBody(), RedirectBody.class);
                content = redirectBody.content;
                break;
            case PushUtil.ChatMsgType.TEXT:
                content = String.format("%s:%s", mMessageBody.getSource().getNickname(), mMessageBody.getBody());
                break;
            default:
                content = "你有一条新消息";
        }

        return new String[]{"你有一条新消息", content};
    }
}
