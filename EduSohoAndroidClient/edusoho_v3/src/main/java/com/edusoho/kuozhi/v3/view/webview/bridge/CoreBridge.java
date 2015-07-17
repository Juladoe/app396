package com.edusoho.kuozhi.v3.view.webview.bridge;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by howzhi on 15/4/17.
 */
public class CoreBridge extends CordovaPlugin {

    public static final String TAG = "CoreBridge";

    private HashMap<String, Method> mMethodList;
    protected Context mContext;
    protected ActionBarBaseActivity mActivity;


    public CoreBridge() {
        super();
        initMethods();
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        mActivity = (ActionBarBaseActivity) cordova.getActivity();
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(
            String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        CallbackStatus<JSONObject> callbackStatus = invoke(action, args, callbackContext);

        JSONObject message = callbackStatus.getMessage();
        switch (callbackStatus.getStatus()) {
            case CallbackStatus.ERROR:
                callbackContext.error(message);
                break;
            case CallbackStatus.SUCCESS:
                if (message != null) {
                    callbackContext.success(message);
                }
                break;
            case CallbackStatus.ASYN:
        }

        return true;
    }

    private void initMethods() {
        mMethodList = new HashMap<String, Method>();
        try {
            Method[] methods = this.getClass().getMethods();
            for (Method method : methods) {
                method.setAccessible(true);
                JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
                if (annotation != null) {
                    mMethodList.put(method.getName(), method);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public CallbackStatus<JSONObject> invoke(String action, JSONArray args, CallbackContext callbackContext) {
        CallbackStatus callbackStatus = new CallbackStatus();
        Method method = mMethodList.get(action);
        if (method != null) {
            try {
                JSONObject object = (JSONObject) method.invoke(this, args, callbackContext);
                callbackStatus.setSuccess(object);
            } catch (Exception e) {
                e.printStackTrace();
                callbackStatus.setError(getInvokeError(e));
            }
        }

        return callbackStatus;
    }

    private JSONObject getInvokeError(Exception error) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("error", error.getMessage());
        } catch (Exception e) {
        }

        return jsonObject;
    }

    protected String[] JsonArrayToStringArray(JSONArray images) {
        int length = images.length();
        String[] strs = new String[length];
        try {
            for (int i = 0; i < length; i++) {
                strs[i] = images.getString(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return strs;
    }
}
