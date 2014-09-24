package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.edusoho.kuozhi.R;

/**
 * Created by howzhi on 14-9-19.
 */
public class WebVideoLessonFragment extends BaseFragment {

    private WebView mWebView;
    @Override
    public String getTitle() {
        return "视频";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mWebView = (WebView) view.findViewById(R.id.webvideo_webview);
    }
}
