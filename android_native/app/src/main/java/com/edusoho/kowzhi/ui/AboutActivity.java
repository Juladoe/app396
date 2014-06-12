package com.edusoho.kowzhi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kowzhi.R;
import com.edusoho.kowzhi.model.AboutResult;
import com.edusoho.kowzhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

public class AboutActivity extends BaseActivity {

    private WebView about_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        initView();
    }

    public static void start(Activity context)
    {
        Intent intent = new Intent();
        intent.setClass(context, AboutActivity.class);
        context.startActivity(intent);
    }

    private void initView() {
        setBackMode("关于网校", true, null);

        about_content = (WebView) findViewById(R.id.about_content);
        loadContent();
    }

    private void loadContent()
    {
        String url = app.bindToken2Url(Const.ABOUT, false);
        ajaxGetString(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                AboutResult result = app.gson.fromJson(
                        object, new TypeToken<AboutResult>(){}.getType());

                if (result != null) {
                    about_content.loadDataWithBaseURL(null, result.about, "text/html", "utf-8", null);
                }
            }
        });
    }
}
