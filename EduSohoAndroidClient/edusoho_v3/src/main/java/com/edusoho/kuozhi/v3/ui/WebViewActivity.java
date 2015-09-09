package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.webview.CordovaContext;
import com.edusoho.kuozhi.v3.view.webview.ESWebView;

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
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (Const.THIRD_PARTY_LOGIN_SUCCESS.equals(messageType.type) || Const.LOGIN_SUCCESS.equals(messageType.type)) {
            if (getRunStatus() == MSG_PAUSE) {
                saveMessage(message);
                return;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.reload();
                }
            });
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
    protected void onResume() {
        super.onResume();
        invokeUIMessage();
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
                new MessageType(Const.LOGIN_SUCCESS),
                new MessageType(Const.THIRD_PARTY_LOGIN_SUCCESS),
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CordovaContext cordovaContext = mWebView.getWebView().getCordovaContext();
        cordovaContext.onActivityResult(requestCode, resultCode, data);
    }
}
