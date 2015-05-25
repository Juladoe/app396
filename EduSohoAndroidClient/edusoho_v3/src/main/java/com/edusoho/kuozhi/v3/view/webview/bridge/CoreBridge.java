package com.edusoho.kuozhi.v3.view.webview.bridge;

import android.content.Context;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by howzhi on 15/4/17.
 */
public class CoreBridge {

    public static final String TAG = "CoreBridge";

    protected HashMap<String, Method> mMethodList;
    protected Context mContext;

    public CoreBridge(Context context)
    {
        this.mContext = context;
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
            e.printStackTrace();
        }
    }

    public CallbackStatus invoke(String action, JSONArray args)
    {
        CallbackStatus callbackStatus = new CallbackStatus();
        Method method = mMethodList.get(action);
        if (method != null) {
            try {
                Object object = method.invoke(this, args);
                callbackStatus.setSuccess(object);
            } catch (Exception e) {
                callbackStatus.setError(e);
                e.printStackTrace();
            }
        }

        return callbackStatus;
    }
}
