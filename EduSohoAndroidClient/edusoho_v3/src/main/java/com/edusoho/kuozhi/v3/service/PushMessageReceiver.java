package com.edusoho.kuozhi.v3.service;

import android.content.Context;
import android.util.Log;

import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

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
        String text = "收到消息:" + message.toString();
        CommonUtil.longToast(context, text);
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
        CommonUtil.longToast(context, message.toString());
    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

    }
}
