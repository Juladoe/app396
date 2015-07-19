package com.edusoho.kuozhi.v3.service;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.TypeBusinessEnum;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.ui.BulletinActivity;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.fragment.FriendFragment;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONObject;

/**
 * Created by JesseHuang on 15/5/16.
 */
public class PushMessageReceiver extends XGPushBaseReceiver {

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
        try {
            Bundle bundle = new Bundle();
            WrapperXGPushTextMessage wrapperMessage = new WrapperXGPushTextMessage(message);
            bundle.putSerializable(Const.CHAT_DATA, wrapperMessage);

            JSONObject jsonObject = new JSONObject(wrapperMessage.getCustomContent());
            String typeBusiness = jsonObject.getString("typeBusiness");
            if (typeBusiness.equals(TypeBusinessEnum.FRIEND.getName()) || typeBusiness.equals(TypeBusinessEnum.TEACHER.getName())) {
                bundle.putInt(Const.ADD_CHAT_MSG_TYPE, NewsFragment.HANDLE_RECEIVE_MSG);
                boolean isForeground = EdusohoApp.app.isForeground(ChatActivity.class.getName());
                if (isForeground) {
                    wrapperMessage.isForeground = true;
                    EdusohoApp.app.sendMsgToTarget(Const.ADD_CHAT_MSG, bundle, ChatActivity.class);
                }
                EdusohoApp.app.sendMsgToTarget(Const.ADD_CHAT_MSG, bundle, NewsFragment.class);
                EdusohoMainService.getService().sendMessage(Const.ADD_CHAT_MSG, wrapperMessage);
            } else if (typeBusiness.equals(TypeBusinessEnum.BULLETIN.getName())) {
                boolean isForeground = EdusohoApp.app.isForeground(BulletinActivity.class.getName());

                if (isForeground) {
                    wrapperMessage.isForeground = true;
                    EdusohoApp.app.sendMsgToTarget(Const.ADD_BULLETIT_MSG, bundle, BulletinActivity.class);
                }
                EdusohoApp.app.sendMsgToTarget(Const.ADD_BULLETIT_MSG, bundle, NewsFragment.class);
                EdusohoMainService.getService().sendMessage(Const.ADD_BULLETIT_MSG, wrapperMessage);
            } else if (typeBusiness.equals(TypeBusinessEnum.VERIFIED.getName())) {
                EdusohoApp.app.sendMsgToTarget(Const.NEW_FANS, bundle, FriendFragment.class);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
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


}
