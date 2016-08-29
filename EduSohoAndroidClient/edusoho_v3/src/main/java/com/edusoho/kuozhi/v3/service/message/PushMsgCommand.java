package com.edusoho.kuozhi.v3.service.message;

import android.content.Context;
import android.content.Intent;

import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.ui.BulletinActivity;
import com.edusoho.kuozhi.v3.ui.ServiceProviderActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.google.gson.Gson;

import java.util.LinkedHashMap;

/**
 * Created by suju on 16/8/24.
 */
public class PushMsgCommand extends AbstractCommand {

    private static final String TAG = "PushMsgCommand";

    public PushMsgCommand(Context context, IMMessageReceiver receiver, MessageBody messageBody) {
        super(context, "push", receiver, messageBody);
    }

    @Override
    public void invoke() {
        showNotification();
        String type = mMessageBody.getSource().getType();
        int targetId = mMessageBody.getSource().getId();
        new IMProvider(mContext).updateConvInfo(type, type, targetId);
        Role role = IMClient.getClient().getRoleManager().getRole(type, targetId);
        ConvEntity convEntity = IMClient.getClient().getConvManager().getSingleConv(type);
        if (convEntity != null && role.getRid() != 0) {
            convEntity.setAvatar(role.getAvatar());
            convEntity.setTargetName(role.getNickname());
            IMClient.getClient().getConvManager().updateConv(convEntity);
        }
    }

    @Override
    protected Intent getNotifyIntent() {
        String fromType = mMessageBody.getSource().getType();
        switch (fromType) {
            case Destination.ARTICLE:
                return getArticleIntent();
            case Destination.GLOBAL:
                return getBulletinIntent();
        }

        return super.getNotifyIntent();
    }

    private Intent getBulletinIntent() {
        Intent notifyIntent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        notifyIntent.putExtra(Const.INTENT_TARGET, BulletinActivity.class);
        return notifyIntent;
    }

    private Intent getArticleIntent() {
        Intent notifyIntent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        notifyIntent.putExtra(ServiceProviderActivity.SERVICE_TYPE, PushUtil.ArticleType.TYPE);
        notifyIntent.putExtra(ServiceProviderActivity.SERVICE_ID, mMessageBody.getSource().getId());
        notifyIntent.putExtra(Const.ACTIONBAR_TITLE, "资讯");
        notifyIntent.putExtra(Const.INTENT_TARGET, ServiceProviderActivity.class);
        return notifyIntent;
    }

    @Override
    protected String[] getNotificationContent() {
        String fromType = mMessageBody.getSource().getType();
        switch (fromType) {
            case Destination.ARTICLE:
                return handlerArticleMessage(mMessageBody.getBody());
            case Destination.GLOBAL:
                return handlerBulletinMessage(mMessageBody.getBody());
        }

        return super.getNotificationContent();
    }

    private String[] handlerBulletinMessage(String body) {
        LinkedHashMap<String, String> linkedHashMap = new Gson().fromJson(body, LinkedHashMap.class);
        return new String[]{ "网校公告", AppUtil.coverCourseAbout(linkedHashMap.get("title"))};
    }

    private String[] handlerArticleMessage(String body) {
        LinkedHashMap<String, String> linkedHashMap = new Gson().fromJson(body, LinkedHashMap.class);

        return new String[]{ linkedHashMap.get("title"), AppUtil.coverCourseAbout(linkedHashMap.get("content"))};
    }
}
