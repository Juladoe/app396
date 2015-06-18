package com.edusoho.kuozhi.v3.view.webview;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.edusoho.kuozhi.v3.cache.request.RequestCallback;
import com.edusoho.kuozhi.v3.cache.request.RequestManager;
import com.edusoho.kuozhi.v3.cache.request.model.Request;
import com.edusoho.kuozhi.v3.cache.request.model.Response;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by howzhi on 15/4/16.
 */
public class ESWebView extends FrameLayout {

    protected CordovaWebView mWebView;
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

        addView(mWebView);

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
