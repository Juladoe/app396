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
import com.edusoho.kuozhi.v3.model.htmlapp.AppMeta;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import org.apache.cordova.Config;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.volley.Response.Listener;
import com.google.gson.reflect.TypeToken;
import cn.trinea.android.common.util.FileUtils;

/**
 * Created by howzhi on 15/4/16.
 */
public class ESWebView extends RelativeLayout {

    protected ESCordovaWebView mWebView;
    protected ProgressBar pbLoading;
    protected Context mContext;
    protected BaseActivity mActivity;
    protected String mAppCode;

    private AttributeSet mAttrs;
    private static final String TAG = "ESWebView";
    private static Pattern APPCODE_PAT = Pattern.compile(".+/mapi_v2/mobile/(\\w+)[#|/]*", Pattern.DOTALL);

    private RequestManager mRequestManager;
    private AppMeta mLocalAppMeta;

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
        mWebView = new ESCordovaWebView(new CordovaContext(mActivity), attrs);

        String userAgent = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString(userAgent.replace("Android", "Android-kuozhi"));

        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
        //mWebView.setOnKeyListener(mOnKeyListener);

        pbLoading = (ProgressBar) LayoutInflater.from(new CordovaContext(mActivity)).inflate(R.layout.progress_bar, null);
        RelativeLayout.LayoutParams paramProgressBar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppUtil.dp2px(mActivity, 2));
        paramProgressBar.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        pbLoading.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_status));
        addView(pbLoading, paramProgressBar);

        RelativeLayout.LayoutParams webViewProgressBar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webViewProgressBar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        webViewProgressBar.addRule(RelativeLayout.BELOW, R.id.pb_loading);
        addView(mWebView, webViewProgressBar);

        mRequestManager = new ESWebViewRequestManager(this, mWebView.getSettings().getUserAgentString());
    }

    public void loadApp(String appCode) {
        this.mAppCode = appCode;
        mLocalAppMeta = getLocalApp(appCode);
        updateApp(mAppCode);
        mWebView.loadUrl(String.format("%s%s/%s", mActivity.app.schoolHost, "mobile", appCode));
    }

    private AppMeta getLocalApp(String appCode) {
        File schoolStorage = AppUtil.getSchoolStorage(mActivity.app.domain);
        File appDir = new File(schoolStorage, appCode);

        if (appDir.exists()) {
            StringBuilder appVersionString = FileUtils.readFile(
                    new File(appDir, "version.json").getAbsolutePath(), "utf-8");
            return mActivity.parseJsonValue(
                    appVersionString.toString(), new TypeToken<AppMeta>() {
            });
        }

        return null;
    }

    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    public void goBack() {
        mWebView.goBack();
    }

    public String getAppCode() {
        return mAppCode;
    }

    private void updateAppResource(final String resourceUrl) {
        mRequestManager.downloadResource(new Request(resourceUrl));
    }

    public void updateApp(final String appCode) {
        RequestUrl appVersionUrl = mActivity.app.bindUrl(
                String.format(Const.MOBILE_APP_VERSION, appCode), true);
        mActivity.ajaxPost(appVersionUrl, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppMeta appMeta = mActivity.parseJsonValue(response, new TypeToken<AppMeta>() {
                });
                if (appMeta == null) {
                    return;
                }

                if (mLocalAppMeta == null) {
                    updateAppResource(appMeta.resource);
                    return;
                }

                int result = CommonUtil.compareVersion(mLocalAppMeta.version, appMeta.version);
                if (result == Const.LOW_VERSIO) {
                    updateAppResource(appMeta.resource);
                }
            }
        }, null);
    }

    public void loadUrl(String url) {

        Matcher matcher = APPCODE_PAT.matcher(url);
        if (matcher.find()) {
            mAppCode = matcher.group(1);
            mLocalAppMeta = getLocalApp(mAppCode);
        }

        mWebView.loadUrl(url);
    }

    public void initPlugin(BaseActivity activity) {
        this.mActivity = activity;
        Config.init(activity);
        initWebView(mAttrs);
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

    public void destroy() {
        Log.d(TAG, "destroy");

        mWebView.stopLoading();
        mWebView.handleDestroy();
        mRequestManager.destroy();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_BACK
                && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    protected WebViewClient mWebViewClient = new ESWebViewClient();

    private class ESWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (mAppCode == null) {
                return super.shouldInterceptRequest(view, url);
            }
            WebResourceResponse resourceResponse = mRequestManager.blockGet(
                    new Request(url), new RequestCallback<WebResourceResponse>() {
                        @Override
                        public WebResourceResponse onResponse(Response<WebResourceResponse> response) {

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

            return resourceResponse;
        }
    }

    public CordovaWebView getWebView() {
        return mWebView;
    }

    private class ESCordovaWebView extends CordovaWebView
    {
        public ESCordovaWebView(Context context) {
            super(context);
        }

        public ESCordovaWebView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return false;
            }
            return super.onKeyUp(keyCode, event);
        }
    }
}
