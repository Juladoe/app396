package com.edusoho.kuozhi.v3.view.webview;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by howzhi on 15/7/15.
 */
public class CordovaContext extends ContextWrapper implements CordovaInterface {

    protected Activity mActivity;
    protected CordovaPlugin mActivityResultCallback = null;
    protected boolean mKeepRunning = true;
    protected boolean mActivityResultKeepRunning;
    protected final ExecutorService mThreadPool = Executors.newCachedThreadPool();

    public CordovaContext(Activity activity) {
        super(activity);
        this.mActivity = activity;
    }

    public CordovaPlugin getActivityResultCallback() {
        return this.mActivityResultCallback;
    }

    public void startActivityForResult(CordovaPlugin cordovaPlugin, Intent intent, int requestCode) {
        this.mActivityResultCallback = cordovaPlugin;
        this.mActivityResultKeepRunning = this.mKeepRunning;
        if (cordovaPlugin != null) {
            this.mKeepRunning = false;
        }

        mActivity.startActivityForResult(intent, requestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CordovaPlugin callback = getActivityResultCallback();
        if (callback != null) {
            callback.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void setActivityResultCallback(CordovaPlugin cordovaPlugin) {
        this.mActivityResultCallback = cordovaPlugin;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public Object onMessage(String id, Object data) {
        return null;
    }

    public ExecutorService getThreadPool() {
        return mThreadPool;
    }
}