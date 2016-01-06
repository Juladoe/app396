package com.edusoho.kuozhi.v3.view.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
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
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.edusoho.kuozhi.v3.view.webview.bridgeadapter.AbstractJsBridgeAdapterWebView;
import com.google.gson.reflect.TypeToken;
import cn.trinea.android.common.util.FileUtils;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by howzhi on 15/4/16.
 */
public class ESWebView extends RelativeLayout {

    public static final int LOAD_FROM_CACHE = 0001;
    public static final int LOAD_FROM_NET = 0002;
    public static final int LOAD_AUTO = 0003;

    private int mLoadType;
    protected AbstractJsBridgeAdapterWebView mWebView;
    protected ProgressBar pbLoading;
    protected Context mContext;
    protected BaseActivity mActivity;
    protected String mAppCode;
    private AttributeSet mAttrs;
    private String mUrl;
    private static final String TAG = "ESWebView";
    private static Pattern APPCODE_PAT = Pattern.compile(".+/mapi_v2/mobile/(\\w+)[#|/]*", Pattern.DOTALL);

    private RequestManager mRequestManager;
    private AppMeta mLocalAppMeta;

    public ESWebView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ESWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.mAttrs = attrs;
        init();
    }

    public void setLoadType(int type) {
        this.mLoadType = type;
    }

    protected void init() {
        mLoadType = LOAD_AUTO;
    }

    public int getLoadType() {
        return mLoadType;
    }

    public String getUserAgent() {
        return mWebView.getSettings().getUserAgentString();
    }

    private AbstractJsBridgeAdapterWebView createWebView() {
        return ESCordovaWebViewFactory.getFactory().getWebView(mActivity);
    }

    private void setupWebView() {
        mWebView.setWebViewClient(new ESWebViewClient());
        mWebView.setWebChromeClient(new ESWebChromeClient(mWebView));
    }

    private void initWebView() {
        mWebView = createWebView();
        setupWebView();

        RelativeLayout.LayoutParams webViewProgressBar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webViewProgressBar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        addView(mWebView, webViewProgressBar);

        pbLoading = (ProgressBar) LayoutInflater.from(mContext).inflate(R.layout.progress_bar, null);
        RelativeLayout.LayoutParams paramProgressBar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramProgressBar.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        paramProgressBar.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        addView(pbLoading, paramProgressBar);
    }

    public RequestManager getRequestManager() {
        return mRequestManager;
    }

    public void loadApp(String appCode) {
        updateCode(appCode);
        mLocalAppMeta = getLocalApp(appCode);
        setLoadType(LOAD_FROM_CACHE);
        mUrl = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, appCode);
        loadUrl(mUrl);
    }

    private void updateCode(String code) {
        this.mAppCode = code;
        mRequestManager = ESWebViewRequestManager.getRequestManager(mContext, this.mAppCode);
        mRequestManager.setWebView(this);
    }

    private AppMeta getLocalApp(String appCode) {
        File schoolStorage = AppUtil.getHtmlPluginStorage(mContext, mActivity.app.domain);
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
        mWebView.setGoBackStatus(true);
        mWebView.goBack();
    }

    public String getAppCode() {
        return mAppCode;
    }

    public void updateApp(String appCode, boolean isLoadByDialog) {
        String projectCode = mContext.getString(R.string.app_code);
        RequestUrl appVersionUrl = mActivity.app.bindUrl(
                String.format(Const.MOBILE_APP_VERSION, appCode, projectCode), true);

        RequestCallback<Boolean> callback = null;
        if (isLoadByDialog) {
            final LoadDialog loadDialog = LoadDialog.create(mActivity);
            loadDialog.show();
            callback = new RequestCallback<Boolean>() {
                @Override
                public Boolean onResponse(Response response) {
                    loadDialog.dismiss();
                    mWebView.loadUrl(mUrl);
                    return false;
                }
            };
        }

        mRequestManager.updateApp(appVersionUrl, callback);
    }

    public void loadUrl(String url) {
        mUrl = url;
        Matcher matcher = APPCODE_PAT.matcher(url);
        if (matcher.find()) {
            updateCode(matcher.group(1));
            mLocalAppMeta = getLocalApp(mAppCode);
        }

        if (TextUtils.isEmpty(mAppCode) || checkResourceIsExists()) {
            mWebView.loadUrl(mUrl);
            return;
        }

        updateApp(mAppCode, true);
    }

    private boolean checkResourceIsExists() {
        File schoolStorage = AppUtil.getHtmlPluginStorage(mContext, mActivity.app.domain);
        File schoolAppFile = new File(schoolStorage, mAppCode);
        InputStream zinInputStream = null;

        String projectCode = mContext.getString(R.string.app_code);
        AppMeta innerAppmeta = getInnerHtmlPluginVersion(projectCode);

        try {
            zinInputStream = mContext.getAssets().open(String.format("%s-html5-%s.Android.zip", projectCode, mAppCode));
        } catch (Exception e) {
        }

        if (mLocalAppMeta == null) {
            if (AppUtil.unZipFile(schoolAppFile, zinInputStream)) {
                mLocalAppMeta = getLocalApp(mAppCode);
            }

            return mLocalAppMeta != null;
        }

        if (CommonUtil.compareVersion(mLocalAppMeta.version, innerAppmeta.version) == Const.LOW_VERSIO) {
            if (AppUtil.unZipFile(schoolAppFile, zinInputStream)) {
                mLocalAppMeta = getLocalApp(mAppCode);
            }
        }
        updateApp(mAppCode, false);
        return mLocalAppMeta != null;
    }

    private AppMeta getInnerHtmlPluginVersion(String projectCode){
        ZipEntry zipEntry = null;
        AppMeta appMeta = null;
        ZipInputStream zipInputStream = null;
        try {
            InputStream inputStream = mContext.getAssets().open(String.format("%s-html5-%s.Android.zip", projectCode, mAppCode));
            zipInputStream = new ZipInputStream(inputStream);
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if ("version.json".equals(zipEntry.getName())) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(zipInputStream));
                    String line = null;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    appMeta = mActivity.parseJsonValue(stringBuilder.toString(), new TypeToken<AppMeta>(){});
                    break;
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
        } finally {
            try {
                zipInputStream.close();
            } catch (Exception e) {
            }
        }

        return appMeta;
    }

    public void initPlugin(BaseActivity activity) {
        this.mActivity = activity;
        initWebView();
    }

    public void destroy() {
        Log.d(TAG, "destroy");

        mWebView.stopLoading();
        mWebView.clearHistory();
        mWebView.handleDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_BACK
                && mWebView.canGoBack()) {
            goBack();
            return true;
        }
        return false;
    }

    private class ESWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            mWebView.setGoBackStatus(false);
            pbLoading.setVisibility(GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(mActivity.app.host)) {
                loadUrl(url);
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

    public AbstractJsBridgeAdapterWebView getWebView() {
        return mWebView;
    }
}
