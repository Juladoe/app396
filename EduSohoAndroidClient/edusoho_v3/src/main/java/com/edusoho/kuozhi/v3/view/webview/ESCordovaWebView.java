package com.edusoho.kuozhi.v3.view.webview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import org.apache.cordova.CordovaWebView;

/**
 * Created by howzhi on 15/7/15.
 */
public class ESCordovaWebView extends CordovaWebView{

    private CordovaContext mCordovaContext;

    public ESCordovaWebView(Context context) {
        super(context);
        mCordovaContext = (CordovaContext) context;
    }

    public ESCordovaWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCordovaContext = (CordovaContext) context;
    }

    public ESCordovaWebView(Context context, AttributeSet attrs, int defstyle) {
        super(context, attrs, defstyle);
        mCordovaContext = (CordovaContext) context;
    }

    public CordovaContext getCordovaContext() {
        return mCordovaContext;
    }

    public void updateCordovaActivity(Activity activity) {
        mCordovaContext.updateTargetActivity(activity);
    }

    public static ESCordovaWebView create(Activity activity, AttributeSet attrs) {
        CordovaContext cordovaContext = new CordovaContext(activity);
        if (attrs == null) {
            return new ESCordovaWebView(cordovaContext);
        }

        return new ESCordovaWebView(cordovaContext, attrs);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && canGoBack()) {
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }
}
