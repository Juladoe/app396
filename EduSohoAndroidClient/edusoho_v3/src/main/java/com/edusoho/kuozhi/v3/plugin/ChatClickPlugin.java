package com.edusoho.kuozhi.v3.plugin;

import android.util.Log;

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

        }
        return super.execute(action, args, callbackContext);
    }
}
