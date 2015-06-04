package com.edusoho.kuozhi.v3.plugin;

import android.os.Bundle;
import android.util.Log;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.ui.fragment.ChatFragment;
import com.edusoho.kuozhi.v3.util.Const;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by JesseHuang on 15/6/2.
 */
public class ChatClickPlugin extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d("ChatClickPlugin-->", "ChatClickPlugin");
        if (action.equals("open")) {
            Bundle bundle = new Bundle();
            bundle.putString(ChatFragment.COURSE_ID, "1");
            EdusohoApp.app.sendMsgToTarget(Const.OPEN_COURSE_CHAT, bundle, DefaultPageActivity.class);
        }
        return super.execute(action, args, callbackContext);
    }
}
