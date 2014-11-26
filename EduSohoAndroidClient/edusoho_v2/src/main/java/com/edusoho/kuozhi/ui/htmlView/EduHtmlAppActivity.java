package com.edusoho.kuozhi.ui.htmlView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by howzhi on 14/11/25.
 */
public class EduHtmlAppActivity extends ActionBarBaseActivity implements CordovaInterface {

    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private CordovaWebView cordovaWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.html_app_layout);
        initView();
    }

    protected void initView() {
        setBackMode(BACK, "标题");
        cordovaWebView = (CordovaWebView) findViewById(R.id.htmlapp_webView);
        Config.init();
        cordovaWebView.loadUrl("file:///android_asset/www/index.html");

        cordovaWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitle(title);
            }
        });
    }

    public Context getContext() {
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

    @Override
    public Activity getActivity() {
        return this;
    }

    public void startActivityForResult(CordovaPlugin command, Intent intent,
                                       int requestCode) {

    }

    @Override
    public void setActivityResultCallback(CordovaPlugin cordovaPlugin) {

    }

    @Override
    /**
     * The final call you receive before your activity is destroyed.
     */
    public void onDestroy() {
        super.onDestroy();
        if (cordovaWebView != null) {
            // Send destroy event to JavaScript
            cordovaWebView.handleDestroy();
        }
    }
}
