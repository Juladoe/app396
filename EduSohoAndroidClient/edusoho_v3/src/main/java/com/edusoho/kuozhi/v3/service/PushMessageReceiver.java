package com.edusoho.kuozhi.v3.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JesseHuang on 15/5/16.
 */
public class PushMessageReceiver extends XGPushBaseReceiver {
    private static final String TAG = "PushMessageReceiver";

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    //消息传递
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        String text = "收到消息:" + message.toString();
        Bundle bundle = new Bundle();
        boolean isForeground = EdusohoApp.app.isForeground("com.edusoho.kuozhi.v3.ui.ChatActivity");
        Log.d(TAG, isForeground + "");
        if (isForeground) {
            EdusohoApp.app.sendMsgToTarget(Const.CHAT_MSG, bundle, ChatActivity.class);
        } else {
            //showNotification(sn);
        }
    }

    private void showNotification(New newModel) {
        try {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(EdusohoApp.app.mContext).setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(newModel.title)
                            .setContentText(newModel.content).setAutoCancel(true);
            NotificationManager mNotificationManager =
                    (NotificationManager) EdusohoApp.app.mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notifyIntent = new Intent(EdusohoApp.app.mContext, ChatActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            int requestCode = (int) SystemClock.uptimeMillis();
            PendingIntent pendIntent = PendingIntent.getActivity(EdusohoApp.app.mContext, requestCode,
                    notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendIntent);

            mNotificationManager.notify(requestCode, mBuilder.build());
        } catch (Exception ex) {
            Log.d("showNotification-->", ex.getMessage());
        }
    }

    //通知展示
    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult message) {
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            text = "通知被打开 :" + message;
        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除 :" + message;
        }
        Log.d("PushMessageReceiver", text);
        //CommonUtil.longToast(context, message.toString());
    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        if (context == null || xgPushShowedResult == null) {

            return;
        }
    }

    private Chat convert2ChatModel(XGPushTextMessage message) throws JSONException {
        Chat chat = new Chat();
//        chat.setNewId();
        JSONObject jsonObject = new JSONObject(message.getCustomContent());
        CustomContent customContent = EdusohoApp.app.parseJsonValue(jsonObject.getString("key"), new TypeToken<CustomContent>() {
        });
        chat.setFromId(Integer.valueOf(customContent.fromId));
        chat.setToId(EdusohoApp.app.loginUser.id);
        chat.setNickName(customContent.nickname);
        chat.setHeadimgurl(customContent.imgUrl);
        chat.setContent(message.getContent());
        chat.setType(customContent.type);
        chat.setCreatedTime(customContent.createdTime);
        return chat;
    }

}
