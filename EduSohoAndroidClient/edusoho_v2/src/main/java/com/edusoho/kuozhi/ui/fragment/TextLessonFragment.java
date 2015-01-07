package com.edusoho.kuozhi.ui.fragment;

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
import com.edusoho.kuozhi.ui.course.LessonActivity;

/**
 * Created by howzhi on 14-9-15.
 */
public class TextLessonFragment extends BaseFragment {

    private WebView mLessonWebview;
    private String mContent;

    private Handler webViewHandler;
    private static final int SHOW_IMAGES = 0002;

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.text_fragment_layout);
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
        initWebViewSetting();
        showProgress(true);
        mLessonWebview.loadDataWithBaseURL("file:///android_asset/consult.html", mContent, "text/html", "utf-8", null);
    }

    private void initWebViewSetting()
    {
        WebSettings webSettings = mLessonWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        webSettings.setDefaultTextEncodingName("utf-8");

        mLessonWebview.addJavascriptInterface(new JavaScriptObj(), "jsobj");
        mLessonWebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mLessonWebview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    showProgress(false);
                }
            }
        });

        mLessonWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("imageindexnurls")) {
                    return true;
                }
                return true;
            }
        });
    }

    /**
     * js注入对象
     */
    public class JavaScriptObj {
        @JavascriptInterface
        public void showHtml(String src) {
            if (src != null && !"".equals(src)) {
                //webViewHandler.obtainMessage(PLAY_VIDEO, src).sendToTarget();
            }
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
