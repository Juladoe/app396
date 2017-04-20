package com.edusoho.kuozhi.clean.module.order.alipay;

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
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

/**
 * Created by DF on 2017/4/12.
 */

public class AlipayActivity extends AppCompatActivity {

    private static final String TARGET_ID = "targetId";
    private static final String URL_DATA = "urlData";
    private static final String ALIPAY_CALLBACK = "alipayCallback?";
    private static final String PAYMENT_CALLBACK = "/pay/center/success/show";

    private LoadDialog mProcessDialog;
    private WebView mAlipay;
    private String mData;
    private int mTargetId;

    public static void launch(Context context, String data, int targetId) {
        Intent intent = new Intent(context, AlipayActivity.class);
        intent.putExtra(URL_DATA, data);
        intent.putExtra(TARGET_ID, targetId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay);
        mData = getIntent().getStringExtra(URL_DATA);
        mTargetId = getIntent().getIntExtra(TARGET_ID, 0);

        initView();
        initData();
    }

    private void initView() {
        mAlipay = (WebView) findViewById(R.id.wv);
    }

    private void initData() {
        WebSettings ws = mAlipay.getSettings();
        ws.setJavaScriptEnabled(true);
        mAlipay.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(ALIPAY_CALLBACK)) {
                    CommonUtil.shortToast(AlipayActivity.this, getString(R.string.join_success));
                    sendBroad();
                    return true;
                }
                if (url.contains(PAYMENT_CALLBACK)) {
                    finish();
                    return true;
                }
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
    protected void onDestroy() {
        super.onDestroy();
        if (mAlipay != null) {
            mAlipay.destroy();
        }
    }

    private void sendBroad() {
        Intent intent = new Intent();
        intent.setAction("Finish");
        sendBroadcast(intent);
        CourseProjectActivity.launch(this, mTargetId);
        finish();
    }
}
