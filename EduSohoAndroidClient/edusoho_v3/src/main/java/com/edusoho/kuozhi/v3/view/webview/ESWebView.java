package com.edusoho.kuozhi.v3.view.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.cache.request.RequestCallback;
import com.edusoho.kuozhi.v3.cache.request.RequestManager;
import com.edusoho.kuozhi.v3.cache.request.model.Request;
import com.edusoho.kuozhi.v3.cache.request.model.Response;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.htmlapp.AppMeta;
import com.edusoho.kuozhi.v3.model.htmlapp.UpdateAppMeta;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.reflect.TypeToken;
import cn.trinea.android.common.util.FileUtils;

/**
 * Created by howzhi on 15/4/16.
 */
public class ESWebView extends RelativeLayout {

    public static final int LOAD_FROM_CACHE = 0001;
    public static final int LOAD_FROM_NET = 0002;
    public static final int LOAD_AUTO = 0003;

    private int mLoadType;
    protected ESCordovaWebView mWebView;
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

    private ESCordovaWebView createWebView() {
        return ESCordovaWebViewFactory.getFactory().getWebView(mActivity);
    }

    private void setupWebView() {

        String userAgent = mWebView.getSettings().getUserAgentString();

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setUserAgentString(userAgent.replace("Android", "Android-kuozhi"));

        CordovaContext cordovaContext = mWebView.getCordovaContext();
        mWebView.setScrollBarStyle(SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setWebViewClient(new ESWebViewClient(cordovaContext, mWebView));
        mWebView.setWebChromeClient(new ESPrivateWebChromeClient(cordovaContext));
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
        mWebView.setGoBackStatus(true);
        mWebView.goBack();
    }

    public String getAppCode() {
        return mAppCode;
    }

    public void updateApp(String appCode, boolean isLoadByDialog) {
        RequestUrl appVersionUrl = mActivity.app.bindUrl(
                String.format(Const.MOBILE_APP_VERSION, appCode), true);

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
        } else {
            mActivity.showActionBar();
        }

        if (checkResourceIsExists()) {
            mWebView.loadUrl(mUrl);
            return;
        }

        updateApp(mAppCode, true);
    }

    private boolean checkResourceIsExists() {
        File appZipStorage = AppUtil.getAppZipStorage();

        File[] files = appZipStorage.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.contains(mAppCode);
            }
        });

        final Pattern versionPat = Pattern.compile(".+-([\\.\\d]+)\\..*", Pattern.DOTALL);
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                Matcher matcher = versionPat.matcher(lhs.getName());
                String lv = matcher.find() ? matcher.group(1) : null;
                matcher = versionPat.matcher(rhs.getName());
                String rv = matcher.find() ? matcher.group(1) : null;
                return CommonUtil.compareVersion(lv, rv);
            }
        });

        if (mLocalAppMeta != null) {
            return true;
        }

        File schoolStorage = AppUtil.getSchoolStorage(mActivity.app.domain);
        File schoolAppFile = new File(schoolStorage, mAppCode);
        InputStream zinInputStream = null;

        try {
            if (files.length == 0) {
                zinInputStream = mContext.getAssets().open(String.format("edusoho-html5-%s.Android.zip", mAppCode));
                updateApp(mAppCode, false);
            } else {
                zinInputStream = new FileInputStream(files[0]);
            }
        } catch (Exception e) {
        }

        if (AppUtil.unZipFile(schoolAppFile, zinInputStream)) {
            mLocalAppMeta = getLocalApp(mAppCode);
        }

        return mLocalAppMeta != null;
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

    private class ESPrivateWebChromeClient extends ESWebChromeClient {

        public ESPrivateWebChromeClient(CordovaInterface cordova) {
            super(cordova);
        }

    };

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

    private class ESWebViewClient extends CordovaWebViewClient {

        public ESWebViewClient(CordovaInterface cordova) {
            super(cordova);
        }

        public ESWebViewClient(CordovaInterface cordova, CordovaWebView view) {
            super(cordova, view);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mWebView.setGoBackStatus(false);
            pbLoading.setVisibility(GONE);
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

    public ESCordovaWebView getWebView() {
        return mWebView;
    }
}
