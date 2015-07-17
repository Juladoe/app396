package com.edusoho.kuozhi.v3.view.webview;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by howzhi on 15/7/15.
 */
public class CordovaContext extends ContextWrapper implements CordovaInterface {

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
        return mTargetActivity == null ? mActivity : mTargetActivity;
    }

    public Object onMessage(String id, Object data) {
        return null;
    }

    public ExecutorService getThreadPool() {
        return mThreadPool;
    }
}