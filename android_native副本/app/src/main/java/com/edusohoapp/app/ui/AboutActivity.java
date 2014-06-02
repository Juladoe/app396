package com.edusohoapp.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.androidquery.callback.AjaxStatus;
import com.edusohoapp.app.EdusohoApp;
import com.edusohoapp.app.R;
import com.edusohoapp.app.util.Const;
import com.edusohoapp.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

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
                String result = app.gson.fromJson(
                        object, new TypeToken<String>(){}.getType());

                if (result != null) {
                    about_content.loadDataWithBaseURL(null, result, "text/html", "utf-8", null);
                }
            }
        });
    }
}
