package com.edusoho.kuozhi.v3.view.webview.cordova;

import com.edusoho.kuozhi.v3.view.webview.bridge.CallbackStatus;
import com.edusoho.kuozhi.v3.view.webview.bridge.CordovaBridge;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by howzhi on 14/11/28.
 */
public class ESNativeCore extends CordovaPlugin {

    public ESNativeCore(){
    }

    @Override
    public boolean execute(
            String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        CallbackStatus callbackStatus = new CordovaBridge(cordova.getActivity()).invoke(action, args);

        Object message = callbackStatus.getMessage();
        switch (callbackStatus.getStatus()) {
            case CallbackStatus.ERROR:
                if (message instanceof String) {
                    callbackContext.error((String)message);
                } else if (message instanceof Integer) {
                    callbackContext.error((Integer)message);
                }else if (message instanceof JSONObject) {
                    callbackContext.error((JSONObject)message);
                }

                break;
            case CallbackStatus.SUCCESS:
                if (message instanceof String) {
                    callbackContext.success((String)message);
                } else if (message instanceof Integer) {
                    callbackContext.success((Integer)message);
                }else if (message instanceof JSONObject) {
                    callbackContext.success((JSONObject)message);
                }else if (message instanceof JSONArray) {
                    callbackContext.success((JSONArray)message);
                }else if (message instanceof byte[]) {
                    callbackContext.success((byte[])message);
                }
                break;
        }

        return super.execute(action, args, callbackContext);
    }
}
