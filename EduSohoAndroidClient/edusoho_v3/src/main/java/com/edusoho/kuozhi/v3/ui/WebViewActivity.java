package com.edusoho.kuozhi.v3.ui;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.kuozhi.v3.view.webview.ESWebView;
import com.edusoho.kuozhi.v3.view.webview.bridgeadapter.bridge.BridgePluginContext;

/**
 * Created by JesseHuang on 15/6/17.
 */
public class WebViewActivity extends ActionBarBaseActivity {

    private final static String TAG = "WebViewActivity";
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
            url = intent.getStringExtra(Const.WEB_URL);
        }

        if (TextUtils.isEmpty(url)) {
            CommonUtil.longToast(mActivity, "访问的地址不存在");
            return;
        }

        if (!url.startsWith(app.host)) {
            showActionBar();
        }
        mWebView = (ESWebView) findViewById(R.id.webView);
        mWebView.initPlugin(mActivity);
        mWebView.loadUrl(url);
    }

    @Override
    public void invoke(WidgetMessage message) {
        processMessage(message);
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
        destoryVideoResource();
        destoryWebView();
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(CLOSE, source),
                new MessageType(Const.TOKEN_LOSE),
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
        BridgePluginContext pluginContext = mWebView.getWebView().getBridgePluginContext();
        pluginContext.onActivityResult(requestCode, resultCode, data);
    }

    private void destoryVideoResource() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(
                new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int i) {
                        //nothing
                    }
                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        );

        Log.i(null, "WebVideoActivity webview stop");
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onPause", (Class[]) null)
                    .invoke(mWebView.getWebView(), (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
