package com.edusoho.kuozhi.v3.view.webview;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by howzhi on 15/7/15.
 */
public class ESCordovaWebView extends CordovaWebView {

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

    public static class CordovaContext extends ContextWrapper implements CordovaInterface {

        protected Activity mActivity;
        protected Activity mTargetActivity;
        protected CordovaPlugin mActivityResultCallback = null;
        protected boolean mKeepRunning = true;
        protected boolean mActivityResultKeepRunning;
        protected final ExecutorService mThreadPool = Executors.newCachedThreadPool();

        public CordovaContext(Activity activity) {
            super(activity.getBaseContext());
            this.mActivity = activity;
        }

        public void updateTargetActivity(Activity activity) {
            this.mTargetActivity = activity;
        }

        public void startActivityForResult(CordovaPlugin cordovaPlugin, Intent intent, int requestCode) {
            this.mActivityResultCallback = cordovaPlugin;
            this.mActivityResultKeepRunning = this.mKeepRunning;

            // If multitasking turned on, then disable it for activities that return results
            if (cordovaPlugin != null) {
                this.mKeepRunning = false;
            }

            // Start activity
            mActivity.startActivityForResult(intent, requestCode);
        }

        public void setActivityResultCallback(CordovaPlugin plugin) {
        }

        public Activity getActivity() {
            Log.d("ESCordovaWebView", "getActivity");
            return mTargetActivity == null ? mActivity : mTargetActivity;
        }

        public Object onMessage(String id, Object data) {
            return null;
        }

        public ExecutorService getThreadPool() {
            return mThreadPool;
        }
    }
}
