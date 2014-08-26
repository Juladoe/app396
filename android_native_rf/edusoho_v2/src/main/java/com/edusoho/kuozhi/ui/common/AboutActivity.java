package com.edusoho.kuozhi.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.AboutResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.net.URL;

public class AboutActivity extends ActionBarBaseActivity {

    public static final String URL = "url";
    public static final String TITLE = "title";
    private WebView about_content;
    private String mUrl;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        initView();
    }

    public static void start(Activity context, String url)
    {
        Intent intent = new Intent();
        intent.setClass(context, AboutActivity.class);
        context.startActivity(intent);
    }

    private void initView() {
        Intent data = getIntent();
        if (data != null && data.hasExtra(URL)) {
            mUrl = data.getStringExtra(URL);
            mTitle = data.getStringExtra(TITLE);
        }

        if (mTitle == null) {
            mTitle = "";
        }
        setBackMode(BACK, mTitle);

        about_content = (WebView) findViewById(R.id.about_content);
        about_content.getSettings().setJavaScriptEnabled(false);
        about_content.getSettings().setDefaultTextEncodingName("UTF-8");
        about_content.getSettings().setUseWideViewPort(true);

        about_content.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    setProgressBarIndeterminateVisibility(false);
                }
            }
        });
        loadContent();
    }

    private void loadContent()
    {
        setProgressBarIndeterminateVisibility(true);
        about_content.loadUrl(mUrl);
    }
}
