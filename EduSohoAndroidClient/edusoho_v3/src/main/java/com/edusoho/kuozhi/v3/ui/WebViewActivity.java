package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivityWithCordova;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.webview.ESWebView;
import com.edusoho.kuozhi.v3.view.webview.ESWebViewFactory;

/**
 * Created by JesseHuang on 15/6/17.
 */
public class WebViewActivity extends ActionBarBaseActivity {

    private final static String TAG = "WebViewActivity";
    public final static String URL = "data";
    public final static int CLOSE = 0x01;
    public final static int BACK = 0x02;

    private String url = "";
    private ESWebView mWebView;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setBackMode(super.BACK, "标题");
        setContentView(R.layout.webview_activity);
        initCordovaWebView();
    }


    public void initCordovaWebView() {
        Intent intent = getIntent();
        if (intent != null) {
            url = intent.getStringExtra(URL);
        }

        mWebView = (ESWebView) findViewById(R.id.webView);
        mWebView.initPlugin(mActivity);
        mWebView.loadUrl(url);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        MessageType messageType = message.type;
        if (Const.LOGIN_SUCCESS.equals(messageType.type)) {
            mHandler.postAtTime(new Runnable() {
                @Override
                public void run() {
                    mWebView.reload();
                }
            }, SystemClock.currentThreadTimeMillis() + 100);
            return;
        }
        if (messageType.code == BACK) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                        return;
                    }
                    finish();
                }
            });
        } else if (messageType.code == CLOSE) {
            finish();
        }
    }

    @Override
    public int getMode() {
        return REGIST_OBJECT;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        destoryWebView();
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(CLOSE, source),
                new MessageType(MessageType.NONE, Const.LOGIN_SUCCESS, MessageType.UI_THREAD)
        };
        return messageTypes;
    }

    @Override
    public void finish() {
        Log.d(TAG, "finish");
        super.finish();
    }

    private void destoryWebView() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
    }
}
