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
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.NormalCallback;

import java.util.List;

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

    private LoadDialog mLoadDialog;

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
        String url = dataIntent.getStringExtra("url");
        isAutoScreen = dataIntent.getBooleanExtra(WebVideoActivity.AUTO_SCREEN, false);
        if (url == null || "".equals(url)) {
            Toast.makeText(this, "无效播放网址!", Toast.LENGTH_SHORT).show();
            return;
        }

        mLoadDialog = getLoadView();
        Log.i(null, "WebVideoActivity url->" + url);
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
            mWebView.loadUrl(url);
        } else {
            mWebView.loadDataWithBaseURL(
                    null, url, "text/html", "utf-8", null);
        }
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
            if (Build.VERSION.SDK_INT >= 16 && url.matches(".+\\.flv\\??.*")) {
                Intent intent = new Intent(mContext, EduSohoVideoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("url", url);
                startActivity(intent);
                webViewStop();
                return;
            }
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i(null, "WebVideoActivity onPageFinished->" + url);
            if (isAutoScreen) {
                view.loadUrl("javascript:var videos = document.getElementsByTagName('video');" +
                        "for (var i=0; i < videos.length; i++){" +
                        "videos[i].height = '1500';" +
                        "}");
            }
            if (url.endsWith("javascript:;")) {
                if (mNormalCallback != null) {
                    mNormalCallback.success(null);
                }
                return;
            }

            if (url.matches(".+\\.mp4\\?.+")) {
                Intent intent = new Intent(mContext, EduSohoVideoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("url", url);
                startActivity(intent);
                return;
            }
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
            if (mCustomViewCallback != null) {
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;
                return;
            }

            isFullScreen = true;
            EdusohoApp.app.sendMessage(MESSAGE_ID, new MessageModel(MESSAGE_OPEN_FULL));

            ViewGroup viewGroup = (ViewGroup) mWebView.getParent();
            viewGroup.removeView(mWebView);
            viewGroup.addView(view);

            mView = view;
            mCustomViewCallback = callback;

        }

        @Override
        public void onHideCustomView() {
        }
    }

    private void hide()
    {
        isFullScreen = false;

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
            EdusohoApp.app.sendMessage(MESSAGE_ID, new MessageModel(MESSAGE_CLOSE_FULL));
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
