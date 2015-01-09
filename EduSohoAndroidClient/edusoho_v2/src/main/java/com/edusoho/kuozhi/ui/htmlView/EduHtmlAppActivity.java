package com.edusoho.kuozhi.ui.htmlView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.HtmlApp.Menu;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.server.CacheServer;
import com.edusoho.kuozhi.util.server.handler.WebResourceHandler;

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

    public static final String ASSET_RES = "local://";
    public static final String APP_URL = "app_url";

    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private CordovaWebView cordovaWebView;
    private Menu mMenu;
    private CacheServer mResouceCacheServer;
    private String mUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.html_app_layout);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.html_app_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        MenuItem rootMenuItem = menu.findItem(R.id.html_app_menu);
        if (mMenu == null) {
            rootMenuItem.setVisible(false);
            return true;
        }
        rootMenuItem.setIcon(mMenu.icon);
        rootMenuItem.setTitle(mMenu.name);
        int index = 0;
        for (Menu menuItem : mMenu.item) {
            Log.d(null, "menuItem->" + menuItem);
            menu.addSubMenu(android.view.Menu.NONE, index++, 0, menuItem.name).setIcon(menuItem.icon);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }

        if (mMenu == null) {
            return super.onOptionsItemSelected(item);
        }
        cordovaWebView.sendJavascript(mMenu.action);
        return super.onOptionsItemSelected(item);
    }

    public void setMenu(Menu menu)
    {
        this.mMenu = menu;
    }

    protected void initView() {
        setBackMode(BACK, "标题", R.drawable.action_bar_close);

        Intent intent = getIntent();
        mUrl = intent.getStringExtra(APP_URL);
        if (TextUtils.isEmpty(mUrl)) {
            longToast("无效应用地址 ");
            return;
        }
        if (!mUrl.startsWith("http://")) {
            mUrl = app.schoolHost + mUrl;
        }
        cordovaWebView = (CordovaWebView) findViewById(R.id.htmlapp_webView);
        cordovaWebView.setVerticalScrollBarEnabled(true);
        cordovaWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        Config.init(this);

        mResouceCacheServer = app.getResouceCacheServer(this);
        mResouceCacheServer.addHandler("*", new WebResourceHandler("", this));
        mResouceCacheServer.start();

        cordovaWebView.loadUrl(mUrl, 60000);
        cordovaWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitle(title);
            }
        });

        cordovaWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    public Context getContext() {
        return this.getContext();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && cordovaWebView.canGoBack()) {
            cordovaWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
