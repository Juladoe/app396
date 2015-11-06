package com.edusoho.kuozhi.v3.service;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.service.push.CommandFactory;
import com.edusoho.kuozhi.v3.service.push.PushCommand;
import com.edusoho.kuozhi.v3.service.push.Pusher;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.google.gson.Gson;
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

    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        Log.d("XGPushTextMessage", message.toString());
        try {
            Bundle bundle = new Bundle();
            WrapperXGPushTextMessage wrapperMessage = new WrapperXGPushTextMessage(message);
            bundle.putSerializable(Const.GET_PUSH_DATA, wrapperMessage);
            JSONObject jsonObject = new JSONObject(wrapperMessage.getCustomContentJson());
            if (jsonObject.has("typeBusiness")) {
                String typeBusiness = jsonObject.getString("typeBusiness");
                Pusher pusher = new Pusher(bundle, wrapperMessage);
                PushCommand pushCommand = CommandFactory.Make(typeBusiness, pusher);
                if (pushCommand != null) {
                    pushCommand.execute();
                }
            } else {
                Gson gson = new Gson();
                V2CustomContent v2CustomContent = gson.fromJson(wrapperMessage.getCustomContentJson(), V2CustomContent.class);
                Pusher pusher = new Pusher(bundle, wrapperMessage);
                pusher.setV2CustomContent(v2CustomContent);
                PushCommand pushCommand = CommandFactory.V2Make(pusher);
                if (pushCommand != null) {
                    pushCommand.execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult message) {

    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

    }
}
