package com.edusoho.kuozhi.v3.view.webview;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.cache.request.RequestCallback;
import com.edusoho.kuozhi.v3.cache.request.RequestManager;
import com.edusoho.kuozhi.v3.cache.request.model.Request;
import com.edusoho.kuozhi.v3.cache.request.model.Response;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by howzhi on 15/4/16.
 */
public class ESWebView extends RelativeLayout {

    protected CordovaWebView mWebView;
    protected ProgressBar pbLoading;
    protected Context mContext;
    protected Activity mActivity;

    private AttributeSet mAttrs;
    private static final String TAG = "ESWebView";

    private RequestManager mRequestManager;

    public ESWebView(Context context) {
        super(context);
        mContext = context;
    }

    public ESWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.mAttrs = attrs;
    }

    private void initWebView(AttributeSet attrs) {
        mWebView = new CordovaWebView(new CordovaContext(mActivity), attrs);

        String userAgent = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString(userAgent.replace("Android", "Android-kuozhi"));
        Log.d(TAG, mWebView.getSettings().getUserAgentString());
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setOnKeyListener(mOnKeyListener);

        pbLoading = (ProgressBar) LayoutInflater.from(new CordovaContext(mActivity)).inflate(R.layout.progress_bar, null);
        RelativeLayout.LayoutParams paramProgressBar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppUtil.dp2px(mActivity, 2));
        paramProgressBar.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        pbLoading.setProgress(0);
        pbLoading.setMax(100);
        pbLoading.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_status));
        addView(pbLoading, paramProgressBar);

        RelativeLayout.LayoutParams webViewProgressBar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webViewProgressBar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        webViewProgressBar.addRule(RelativeLayout.BELOW, R.id.pb_loading);
        addView(mWebView, webViewProgressBar);

        mRequestManager = new ESWebViewRequestManager(mContext, mWebView.getSettings().getUserAgentString());
    }

    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    public void initPlugin(Activity activity) {
        this.mActivity = activity;
        Config.init(activity);
        initWebView(mAttrs);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class CordovaContext extends ContextWrapper implements CordovaInterface {

        Activity activity;
        protected final ExecutorService threadPool = Executors.newCachedThreadPool();

        public CordovaContext(Activity activity) {
            super(activity.getBaseContext());
            this.activity = activity;
        }

        public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {
        }

        public void setActivityResultCallback(CordovaPlugin plugin) {
        }

        public Activity getActivity() {
            return activity;
        }

        public Object onMessage(String id, Object data) {
            return null;
        }

        public ExecutorService getThreadPool() {
            return threadPool;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void destory() {
        if (mWebView.pluginManager != null) {
            mWebView.pluginManager.onDestroy();
        }
        mWebView.removeAllViews();
        mWebView.handleDestroy();
        mWebView.destroy();
        mRequestManager.destory();
    }

    protected WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            mActivity.setTitle(title);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            //mActivity.getActionBar().setIcon(new BitmapDrawable(icon));
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            PopupDialog.createNormal(mActivity, "提示:", message).show();
            result.cancel();
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                //ESWebView.this.pbLoading.setProgress(newProgress);
                ESWebView.this.pbLoading.setVisibility(View.GONE);
            } else {
                if (ESWebView.this.pbLoading.getVisibility() == View.GONE) {
                    ESWebView.this.pbLoading.setVisibility(View.VISIBLE);
                }
                ESWebView.this.pbLoading.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    };


    protected OnKeyListener mOnKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            Log.d(TAG, "onKey " + event.getAction());
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && keyCode == KeyEvent.KEYCODE_BACK
                    && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
            return false;
        }
    };

    protected WebViewClient mWebViewClient = new ESWebViewClient();

    private class ESWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            WebResourceResponse resourceResponse = mRequestManager.blockGet(
                    new Request(url), new RequestCallback<WebResourceResponse>() {
                        @Override
                        public WebResourceResponse onResponse(Response<WebResourceResponse> response) {
                            Log.d(TAG, "onResponse: " + response.isEmpty());
                            if (response.isEmpty()) {
                                return null;
                            }
                            WebResourceResponse webResourceResponse = new WebResourceResponse(
                                    response.getMimeType(), response.getEncoding(), response.getContent()
                            );
                            return webResourceResponse;
                        }
                    });

            if (resourceResponse == null) {
                resourceResponse = super.shouldInterceptRequest(view, url);
            }
            Log.d(TAG, String.format("WebResourceResponse %s %s", url, resourceResponse));
            return resourceResponse;
        }
    }

    public CordovaWebView getWebView() {
        return mWebView;
    }

}
