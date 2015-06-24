package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.BaseActivityWithCordova;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaWebView;

/**
 * Created by JesseHuang on 15/6/17.
 */
public class WebViewActivity extends BaseActivityWithCordova implements MessageEngine.MessageCallback {

    private final static String TAG = "WebViewActivity";
    public final static String URL = "data";
    public final static int CLOSE = 0x01;
    private String url = "";
    private CordovaWebView webView;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        initCordovaWebView();
        app.registMsgSource(this);
    }

    public void initCordovaWebView() {
        Intent intent = getIntent();
        if (intent != null) {
            url = intent.getStringExtra(URL);
        }
        Config.init(mActivity);
        webView = (CordovaWebView) findViewById(R.id.webView);
        String userAgent = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(userAgent.replace("Android", "Android-kuozhi"));
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (messageType.code == CLOSE) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (webView.canGoBack()) {
                        webView.goBack();
                        return;
                    }
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.unRegistMsgSource(this);
        if (webView != null) {
            webView.handleDestroy();
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{new MessageType(CLOSE, source)};
        return messageTypes;
    }
}
