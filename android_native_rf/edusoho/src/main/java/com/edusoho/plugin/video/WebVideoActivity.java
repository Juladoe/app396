package com.edusoho.plugin.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.MessageModel;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.NormalCallback;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by howzhi on 14-7-11.
 */
public class WebVideoActivity extends Activity implements VideoPlayerCallback{

    private boolean isFullScreen;
    private Context mContext;
    private boolean isAutoScreen;
    private NormalCallback mNormalCallback;
    private WebVideoWebChromClient mWebVideoWebChromClient;
    private WebView mWebView;
    public static final String MESSAGE_ID = "WebVideoActivity";
    public static final String AUTO_SCREEN = "auto_screen";
    public static final int MESSAGE_OPEN_FULL = 0001;
    public static final int MESSAGE_CLOSE_FULL = 0002;
    public static final int CHECK_HTML_PLAYER = 0011;

    private LoadDialog mLoadDialog;
    private Handler workHandler;
    private boolean isAddFullScreenEvent;
    private LessonItem.MediaSourceType mMediaSourceType;
    private String mUri;

    private static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36";
    private static final String IOS_UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A403 Safari/8536.25";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.web_video_layout);
        initView();
    }

    public boolean isFullScreen()
    {
        return isFullScreen;
    }

    @Override
    public void exitFullScreen() {
        System.out.println("-->exitFullScreen");
    }

    @Override
    public void clear(NormalCallback normalCallback) {
        if (mWebView == null) {
            return;
        }
        webViewStop();
        mWebView.stopLoading();
        mWebView.loadData("javascript:;", "text/html", "utf-8");
        mNormalCallback = normalCallback;
        Log.i(null, "WebVideoActivity clear");
    }

    private boolean checkInstallFlash() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> infoList = pm
                .getInstalledPackages(PackageManager.GET_SERVICES);
        for (PackageInfo info : infoList) {
            if ("com.adobe.flashplayer".equals(info.packageName)) {
                return true;
            }
        }
        return false;
    }

    private LoadDialog getLoadView()
    {
        mLoadDialog = LoadDialog.create(mContext);
        mLoadDialog.show();
        return mLoadDialog;
    }

    private void initView()
    {
        Intent dataIntent = getIntent();
        mUri = dataIntent.getStringExtra("url");
        isAutoScreen = dataIntent.getBooleanExtra(WebVideoActivity.AUTO_SCREEN, false);
        mMediaSourceType = (LessonItem.MediaSourceType) dataIntent.getSerializableExtra("MediaSourceType");
        if (mUri == null || "".equals(mUri)) {
            Toast.makeText(this, "该课程无法播放!(无效播放网址)", Toast.LENGTH_SHORT).show();
            return;
        }

        workHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CHECK_HTML_PLAYER:
                        mWebView.loadUrl("javascript:var divs = document.getElementsByTagName('b');" +
                                "for(var i=0; i < divs.length; i++){" +
                                "if (divs[i].className == 'x-zoomin'){" +
                                "window.obj.addFullScreenEvent();" +
                                "divs[i].addEventListener('click', function(event){window.obj.toggleFullScreen(), false});}}");
                        break;
                }
            }
        };

        mLoadDialog = getLoadView();
        Log.i(null, "WebVideoActivity url->" + mUri);
        mWebView = (WebView) findViewById(R.id.webView);
        if (Build.VERSION.SDK_INT >= 11) {
            mWebView.setLayerType(View.LAYER_TYPE_NONE, null);
        }
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setAllowFileAccess(true);
        mWebView.addJavascriptInterface(new JavaScriptObj(), "obj");

        if (isAutoScreen) {
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
        }
        // 4.1以下
        if (Build.VERSION.SDK_INT < 16) {
            webSettings.setUserAgentString(USER_AGENT);
            if (!checkInstallFlash()) {
                PopupDialog.createMuilt(
                        mContext,
                        "播放提示",
                        "系统尚未安装播放器组件，是否安装？",
                        new PopupDialog.PopupClickListener() {
                            @Override
                            public void onClick(int button) {
                                if (button == PopupDialog.OK) {
                                    EdusohoApp.app.installApk(EdusohoApp.app.getPluginFile("flash_player.apk"));
                                }
                                finish();
                            }
                        }).show();
                return;
            }
        }

        mWebVideoWebChromClient = new WebVideoWebChromClient();
        mWebView.setWebChromeClient(mWebVideoWebChromClient);
        mWebView.setWebViewClient(new WebVideoWebViewClient());

        if (isAutoScreen) {
            mWebView.loadUrl(mUri);
        } else {
            switch (mMediaSourceType) {
                case YOUKU:
                    mWebView.loadUrl(mUri);
                    if (Build.VERSION.SDK_INT >= 19) {
                        checkHtmlPlayer();
                    }
                    break;
                default:
                    mUri = "<iframe id='esIframe' height='99%' width='100%' src='" + mUri + "' frameborder=0 allowfullscreen></iframe>";
                    mWebView.loadDataWithBaseURL(null, mUri, "text/html", "utf-8", null);
            }
        }
    }

    private Timer workTimer;
    private void checkHtmlPlayer()
    {
        workTimer = new Timer();
        workTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (mContext) {
                    if (isAddFullScreenEvent) {
                        workTimer.cancel();
                        return;
                    }
                    workHandler.obtainMessage(CHECK_HTML_PLAYER).sendToTarget();
                }
            }
        }, 0, 1000);
    }

    /**
     * js注入对象
    */
    public class JavaScriptObj
    {
        @JavascriptInterface
        public void show(String html)
        {
            Log.i(null, "html->" + html);
        }

        @JavascriptInterface
        public void addFullScreenEvent()
        {
            isAddFullScreenEvent = true;
        }

        @JavascriptInterface
        public void toggleFullScreen()
        {
            if (isFullScreen) {
                hide();
                return;
            }
            fullScreen();
        }
    }

    private View mView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    private class WebVideoWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(null, "WebVideoActivity onPageFinished->" + url);
            if (url.endsWith("javascript:;")) {
                if (mNormalCallback != null) {
                    mNormalCallback.success(null);
                }
                return;
            }

            if (url.matches(".+\\.mp4\\?.+")) {
                EduSohoVideoActivity.start(mContext, url);
                webViewStop();
                return;
            }
            if (Build.VERSION.SDK_INT >= 16 && url.matches(".+\\.flv\\??.*")) {
                EduSohoVideoActivity.start(mContext, url);
                mWebView.loadUrl(mUri);
                return;
            }
            super.onPageFinished(view, url);
        }
    }

    private class WebVideoWebChromClient extends WebChromeClient
    {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                mLoadDialog.dismiss();
            }
        }

        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            fullScreen();
            if (mCustomViewCallback != null) {
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;
                return;
            }

            fullScreen();
            ViewGroup viewGroup = (ViewGroup) mWebView.getParent();
            viewGroup.removeView(mWebView);
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            viewGroup.addView(view);

            mView = view;
            mCustomViewCallback = callback;
        }

        @Override
        public void onHideCustomView() {
            hide();
            System.out.println("onHide->");
        }
    }

    private void fullScreen()
    {
        isFullScreen = true;
        EdusohoApp.app.sendMessage(MESSAGE_ID, new MessageModel(MESSAGE_OPEN_FULL));
    }

    private void hide()
    {
        isFullScreen = false;

        EdusohoApp.app.sendMessage(MESSAGE_ID, new MessageModel(MESSAGE_CLOSE_FULL));
        if (mView == null) {
            return;
        }

        if (mCustomViewCallback != null) {
            mCustomViewCallback.onCustomViewHidden();
            mCustomViewCallback = null;
        }

        ViewGroup viewGroup = (ViewGroup) mView.getParent();
        viewGroup.removeView(mView);
        viewGroup.addView(mWebView);

        mView = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isFullScreen) {
            hide();
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (workTimer != null) {
            workTimer.cancel();
        }
        Log.i(null, "WebVideoActivity destory");
    }

    private void webViewStop()
    {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(
                new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int i) {
                        //nothing
                    }
                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        Log.i(null, "WebVideoActivity webview stop");
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onPause", (Class[]) null)
                    .invoke(mWebView, (Object[]) null);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
