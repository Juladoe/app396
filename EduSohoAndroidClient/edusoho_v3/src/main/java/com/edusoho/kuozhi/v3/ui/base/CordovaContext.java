package com.edusoho.kuozhi.v3.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;

import java.util.concurrent.ExecutorService;

/**
 * Created by JesseHuang on 15/5/14.
 */
public class CordovaContext extends ContextWrapper implements CordovaInterface {

    CordovaInterface cordova;

    public CordovaContext(Context base, CordovaInterface cordova) {
        super(base);
        this.cordova = cordova;
    }

    @Override
    public void startActivityForResult(CordovaPlugin cordovaPlugin, Intent intent, int i) {
        cordova.startActivityForResult(cordovaPlugin, intent, i);
    }

    @Override
    public void setActivityResultCallback(CordovaPlugin cordovaPlugin) {
        cordova.setActivityResultCallback(cordovaPlugin);
    }

    @Override
    public Activity getActivity() {
        return cordova.getActivity();
    }

    @Override
    public Object onMessage(String s, Object o) {
        return cordova.onMessage(s, o);
    }

    @Override
    public ExecutorService getThreadPool() {
        return cordova.getThreadPool();
    }
}
