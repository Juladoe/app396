package com.edusoho.kuozhi.clean.module.courseset.alipay;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

/**
 * Created by DF on 2017/4/12.
 */

public class AlipayActivity extends AppCompatActivity implements AlipayContract.View{

    private LoadDialog mProcessDialog;
    private WebView mAlipay;
    private String mData;


    public static void newInstance(Context context, String data) {
        Intent intent = new Intent(context, AlipayActivity.class);
        intent.putExtra("Data", data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay);
        mData = getIntent().getStringExtra("Data");

        initView();
        initData();
    }

    private void initView() {
        mAlipay = (WebView) findViewById(R.id.wv);
    }


    private void initData() {
        WebSettings ws = mAlipay.getSettings();
        ws.setJavaScriptEnabled(true);
        mAlipay.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mAlipay.loadDataWithBaseURL(null, mData, "text/html", "utf-8", null);
    }

    protected void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(this);
        }
        mProcessDialog.show();
    }

    protected void hideProcesDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }

    @Override
    public void showLoadDialog(boolean isShow) {
        if (isShow) {
            showProcessDialog();
        } else {
            hideProcesDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlipay.destroy();
        mAlipay = null;
    }

    private void sendBroad(){
        Intent intent = new Intent();
        intent.setAction("Finish");
        sendBroadcast(intent);
    }
}
