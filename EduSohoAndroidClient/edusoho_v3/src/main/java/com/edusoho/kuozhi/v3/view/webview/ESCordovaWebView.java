package com.edusoho.kuozhi.v3.view.webview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;

import org.apache.cordova.CordovaWebView;

/**
 * Created by howzhi on 15/7/15.
 */
public class ESCordovaWebView extends CordovaWebView{

    protected boolean mIsBackIng;
    protected int mOverScrollY;
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

    public boolean isGoBack() {
        return mIsBackIng;
    }

    public void setGoBackStatus(boolean status) {
        this.mIsBackIng = status;
    }

    public CordovaContext getCordovaContext() {
        return mCordovaContext;
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ViewGroup parent = (ViewGroup) getParent();
            return parent.onKeyDown(keyCode, event);
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

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        mOverScrollY = scrollY;
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    public int getOverScrollY() {
        return mOverScrollY;
    }

}
