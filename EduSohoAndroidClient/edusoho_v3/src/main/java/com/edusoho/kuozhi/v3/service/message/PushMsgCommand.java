package com.edusoho.kuozhi.v3.service.message;

import android.content.Context;
import android.content.Intent;

import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.google.gson.Gson;

import java.util.LinkedHashMap;

/**
 * Created by suju on 16/8/24.
 */
public class PushMsgCommand extends AbstractCommand {

    private static final String TAG = "PushMsgCommand";

    public PushMsgCommand(Context context, IMMessageReceiver receiver, MessageBody messageBody) {
        super(context, receiver, messageBody);
    }

    @Override
    public void invoke() {
        showNotification();
    }

    @Override
    protected Intent getNotifyIntent() {
        Intent notifyIntent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return notifyIntent;
    }

    @Override
    protected String[] getNotificationContent() {
        String fromType = mMessageBody.getSource().getType();
        switch (fromType) {
            case "news":
                return handlerArticleMessage(mMessageBody.getBody());
        }

        return super.getNotificationContent();
    }

    private String[] handlerArticleMessage(String body) {
        LinkedHashMap<String, String> linkedHashMap = new Gson().fromJson(body, LinkedHashMap.class);

        return new String[]{ linkedHashMap.get("title"), AppUtil.coverCourseAbout(linkedHashMap.get("content"))};
    }
}
