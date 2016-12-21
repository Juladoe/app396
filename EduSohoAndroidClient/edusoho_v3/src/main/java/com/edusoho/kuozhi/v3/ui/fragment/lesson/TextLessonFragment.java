package com.edusoho.kuozhi.v3.ui.fragment.lesson;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.koushikdutta.async.util.StreamUtility;

import java.io.IOException;
import java.io.InputStream;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by howzhi on 14-9-15.
 */
public class TextLessonFragment extends BaseFragment {

    protected WebView mLessonWebview;
    protected String mContent;

    protected Handler webViewHandler;
    private static final int SHOW_IMAGES = 0002;

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.text_fragment_layout);
        initWorkHandler();
    }

    protected void initWorkHandler() {
        webViewHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SHOW_IMAGES:
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", msg.arg1);
                        bundle.putStringArray("images", (String[]) msg.obj);
                        app.mEngine.runNormalPluginWithBundle("ViewPagerActivity", mActivity, bundle);
                        break;
                }
            }
        };
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        mContent = bundle.getString(LessonActivity.CONTENT);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mLessonWebview = (WebView) view.findViewById(R.id.lesson_webview);
        initWebViewSetting(mLessonWebview);
        mLessonWebview.loadDataWithBaseURL(app.host, getWrapContent(mContent), "text/html", "utf-8", null);
    }

    private String getWrapContent(String content) {
        try {
            InputStream inputStream = getContext().getAssets().open("template.html");
            String wrapContent = FileUtils.readFile(inputStream);
            return wrapContent.replace("%content%", content);
        } catch (IOException e) {
        }
        return content;
    }

    protected void initWebViewSetting(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        webSettings.setDefaultTextEncodingName("utf-8");

        webView.addJavascriptInterface(getJsObj(), "jsobj");
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("imageindexnurls")) {
                    return true;
                }
                return true;
            }
        });
    }

    protected Object getJsObj() {
        return new JavaScriptObj();
    }

    /**
     * js注入对象
     */
    public class JavaScriptObj {
        @JavascriptInterface
        public void showHtml(String src) {
        }

        @JavascriptInterface
        public void showImages(String index, String[] imageArray) {
            Message msg = webViewHandler.obtainMessage(SHOW_IMAGES);
            msg.obj = imageArray;
            msg.arg1 = Integer.parseInt(index);
            msg.sendToTarget();
        }
    }
}
