package com.edusoho.kuozhi.v3.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by JesseHuang on 15/6/17.
 */
public class OpenWebViewPlugin extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("")) {

        }

        return super.execute(action, args, callbackContext);
    }
}
