package com.edusoho.kuozhi.v3.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by JesseHuang on 15/5/4.
 */
public abstract class BaseActivityWithCordova extends BaseActivity implements CordovaInterface {

    protected final ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void startActivityForResult(CordovaPlugin cordovaPlugin, Intent intent, int i) {

    }

    @Override
    public void setActivityResultCallback(CordovaPlugin cordovaPlugin) {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Object onMessage(String s, Object o) {
        return null;
    }

    @Override
    public ExecutorService getThreadPool() {
        return threadPool;
    }
}
