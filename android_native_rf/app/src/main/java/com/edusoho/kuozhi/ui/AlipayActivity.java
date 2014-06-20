package com.edusoho.kuozhi.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.util.Const;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlipayActivity extends BaseActivity {

    private WebView webView;
    public static final int ALIPAY_REQUEST = 0001;
    public static final int ALIPAY_SUCCESS = 0002;
    public static final int ALIPAY_EXIT = 0003;

    private static Pattern urlPat = Pattern.compile("objc://([\\w\\W]+)\\?([\\w]+)", Pattern.DOTALL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alipay_layout);
        initView();
    }

    public static void startForResult(Activity context, String payurl)
    {
        Intent intent = new Intent();
        intent.setClass(context, AlipayActivity.class);
        intent.putExtra("payurl", payurl);
        context.startActivityForResult(intent, ALIPAY_REQUEST);
    }

    private String mPayurl;
    private String mHost;

    private void initView() {
        setBackMode("支付课程", true, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(ALIPAY_EXIT);
                finish();
            }
        });

        Intent dataIntent = getIntent();
        if (!dataIntent.hasExtra("payurl")) {
            longToast("错误的支付页面网址");
            return;
        }

        mPayurl = dataIntent.getStringExtra("payurl");
        try {
            URL hostURL = new URL(mPayurl);
            mHost = "http://" + hostURL.getHost();
        } catch (Exception e) {
            mHost = "";
        }

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.startsWith(mHost) && ! isEqualsURl(mPayurl, url)) {
                    setResult(ALIPAY_EXIT);
                    finish();
                    return;
                }

                Matcher matcher = urlPat.matcher(url);
                if (matcher.find()) {
                    String callBack = matcher.group(1);
                    String param = matcher.group(2);
                    callMethod(callBack, param);
                    return;
                }
            }
        });
        webView.loadUrl(mPayurl);
    }

    private boolean isEqualsURl(String firstUrl, String secUrl)
    {
        int tagIndex1 = firstUrl.indexOf("?");
        if (tagIndex1 != -1) {
            firstUrl = firstUrl.substring(0, tagIndex1);
            int tagIndex2 = secUrl.indexOf("?");
            if (tagIndex2 != -1) {
                secUrl = secUrl.substring(0, tagIndex2);
            }
        }

        if (firstUrl.equals(secUrl)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(ALIPAY_EXIT);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void callMethod(String name, String params)
    {
        Log.d(null, name + "  " + params);
        try {
            Method method = getClass().getMethod(name, new Class[]{String.class});
            method.invoke(this, params);
        } catch (Exception e) {
            Log.d(null, e.toString());
        }
    }

    public void alipayCallback(String status)
    {
        if (Const.RESULT_SUCCESS.equals(status)) {
            setResult(ALIPAY_SUCCESS);
            finish();
        }
    }
}
