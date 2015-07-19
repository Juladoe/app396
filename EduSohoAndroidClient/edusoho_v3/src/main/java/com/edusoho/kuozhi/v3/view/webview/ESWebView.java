package com.edusoho.kuozhi.v3.view.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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
import com.edusoho.kuozhi.v3.model.htmlapp.UpdateAppMeta;
import com.edusoho.kuozhi.v3.model.sys.AppUpdateInfo;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import org.apache.cordova.Config;
import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;

import java.io.File;
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

    public String getUserAgent() {
        return mWebView.getSettings().getUserAgentString();
    }

    private ESCordovaWebView createWebView() {
        return ESWebViewFactory.getFactory().getWebView(mActivity);
    }

    private void setupWebView() {
        String userAgent = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString(userAgent.replace("Android", "Android-kuozhi"));

        CordovaContext cordovaContext = mWebView.getCordovaContext();
        mWebView.setWebViewClient(new ESWebViewClient(cordovaContext, mWebView));
        mWebView.setWebChromeClient(new WebChromeClient(cordovaContext, mWebView));
    }

    private void initWebView() {
        mWebView = createWebView();
        setupWebView();

        pbLoading = (ProgressBar) LayoutInflater.from(mActivity).inflate(R.layout.progress_bar, null);
        RelativeLayout.LayoutParams paramProgressBar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppUtil.dp2px(mActivity, 2));
        paramProgressBar.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        pbLoading.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_status));
        addView(pbLoading, paramProgressBar);

        RelativeLayout.LayoutParams webViewProgressBar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webViewProgressBar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        webViewProgressBar.addRule(RelativeLayout.BELOW, R.id.pb_loading);
        addView(mWebView, webViewProgressBar);
    }

    public RequestManager getRequestManager() {
        return mRequestManager;
    }

    public void loadApp(String appCode) {
        updateCode(appCode);
        mLocalAppMeta = getLocalApp(appCode);
        updateApp(mAppCode);
    }

    private void updateCode(String code) {
        this.mAppCode = code;
        mRequestManager = ESWebViewRequestManager.getRequestManager(mContext, this.mAppCode);
    }

    private AppMeta getLocalApp(String appCode) {
        File schoolStorage = AppUtil.getSchoolStorage(mActivity.app.domain);
        File appDir = new File(schoolStorage, appCode);

        if (appDir.exists()) {
            StringBuilder appVersionString = FileUtils.readFile(
                    new File(appDir, "version.json").getAbsolutePath(), "utf-8");
            if (appVersionString == null) {
                return null;
            }
            return mActivity.parseJsonValue(
                    appVersionString.toString(), new TypeToken<AppMeta>() {
            });
        }

        return null;
    }

    public void reload() {
        mWebView.reload();
    }

    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    public BaseActivity getActivity() {
        return mActivity;
    }

    public void goBack() {
        mWebView.goBack();
    }

    public String getAppCode() {
        return mAppCode;
    }

    private void updateAppResource(final String resourceUrl) {
        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.show();
        mRequestManager.downloadResource(new Request(resourceUrl), new RequestCallback<Boolean>() {
            @Override
            public Boolean onResponse(Response<Boolean> response) {
                if (response.getData()) {
                    mWebView.loadUrl(String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, mAppCode));
                }
                loadDialog.dismiss();
                return null;
            }
        });
    }

    public void updateApp(final String appCode) {
        RequestUrl appVersionUrl = mActivity.app.bindUrl(
                String.format(Const.MOBILE_APP_VERSION, appCode), true);
        mRequestManager.updateApp(appVersionUrl, new RequestCallback<String>() {
            @Override
            public String onResponse(Response<String> response) {
                String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, appCode);
                UpdateAppMeta appMeta = mActivity.parseJsonValue(
                        response.getData(), new TypeToken<UpdateAppMeta>(){});
                if (appMeta == null) {
                    mWebView.loadUrl(url);
                    return null;
                }

                if (mLocalAppMeta == null) {
                    updateAppResource(appMeta.resource);
                    return null;
                }

                int result = CommonUtil.compareVersion(mLocalAppMeta.version, appMeta.version);
                if (result == Const.LOW_VERSIO) {
                    updateAppResource(appMeta.resource);
                    return null;
                }
                mWebView.loadUrl(url);

                return null;
            }
        });
    }

    public void loadUrl(String url) {

        Matcher matcher = APPCODE_PAT.matcher(url);
        if (matcher.find()) {
            updateCode(matcher.group(1));
            mLocalAppMeta = getLocalApp(mAppCode);
        } else {
            mActivity.showActionBar();
        }

        mWebView.loadUrl(url);
    }

    public void initPlugin(BaseActivity activity) {
        this.mActivity = activity;
        initWebView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void destroy() {
        Log.d(TAG, "destroy");

        mWebView.stopLoading();
        mWebView.clearHistory();
        mWebView.handleDestroy();
    }

    private class WebChromeClient extends CordovaChromeClient {

        public WebChromeClient(CordovaInterface cordova) {
            super(cordova);
        }

        public WebChromeClient(CordovaInterface ctx, CordovaWebView app) {
            super(ctx, app);
        }

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

    private class ESWebViewClient extends CordovaWebViewClient {

        public ESWebViewClient(CordovaInterface cordova) {
            super(cordova);
        }

        public ESWebViewClient(CordovaInterface cordova, CordovaWebView view) {
            super(cordova, view);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "s->" + System.currentTimeMillis());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "e->" + System.currentTimeMillis());
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(mActivity.app.host)) {
                view.loadUrl(url);
                return true;
            }

            mActivity.app.startUpdateWebView(url);
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
}
