package com.edusoho.kuozhi.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.TokenResult;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.edusoho.plugin.photo.SchoolSplashActivity;
import com.edusoho.plugin.qr.CaptureActivity;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class QrSchoolActivity extends ActionBarBaseActivity {

    private Button mQrSearchBtn;
    private TextView mOtherBtn;

    public final static int REQUEST_QR = 0001;
    public final static int RESULT_QR = 0002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrsch_layout);
        initView();
        app.addTask("QrSchoolActivity", this);
    }

    public static void start(Activity context) {
        Activity qrSchoolActivity = EdusohoApp.runTask.get("QrSchoolActivity");
        if (qrSchoolActivity != null) {
            qrSchoolActivity.finish();
        }
        Intent intent = new Intent();
        intent.setClass(context, QrSchoolActivity.class);
        context.startActivity(intent);
    }

    private void initView() {
        setBackMode(null, "进入网校");
        mOtherBtn = (TextView) findViewById(R.id.qr_other_btn);
        mQrSearchBtn = (Button) findViewById(R.id.qr_search_btn);

        mOtherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.mEngine.runNormalPlugin("NetSchoolActivity", mActivity, null);
            }
        });

        mQrSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qrIntent = new Intent();
                qrIntent.setClass(mContext, CaptureActivity.class);
                startActivityForResult(qrIntent, REQUEST_QR);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_QR && resultCode == RESULT_QR) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                Log.d(null, "qr->" + result + "&version=2");
                showQrResultDlg(result + "&version=2");
            }
        }
    }

    private void showQrResultDlg(final String result)
    {
        final LoadDialog loading = LoadDialog.create(mContext);
        loading.show();

        RequestUrl requestUrl = new RequestUrl(result);
        ajaxGet(requestUrl, new ResultCallback(){

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                loading.dismiss();
                longToast("二维码信息错误!");
            }

            @Override
            public void callback(String url, String object, AjaxStatus status) {
                loading.dismiss();
                try {
                    final TokenResult schoolResult = app.gson.fromJson(
                            object, new TypeToken<TokenResult>() {
                    }.getType());

                    if (schoolResult == null) {
                        longToast("二维码信息错误!");
                        return;
                    }
                    Log.d(null, "token---->"+ schoolResult.token);
                    School site = schoolResult.site;
                    if (!checkMobileVersion(site, site.apiVersionRange)) {
                        return;
                    }

                    if (schoolResult.token == null || "".equals(schoolResult.token)) {
                        app.removeToken();
                    } else {
                        app.saveToken(schoolResult);
                    }
                    app.setCurrentSchool(site);

                    showSchSplash(site.name, site.splashs);

                } catch (Exception e) {
                    e.printStackTrace();
                    longToast("二维码信息错误!");
                }
            }
        });

    }

    private void showSchSplash(String schoolName, String[] splashs)
    {
        SchoolSplashActivity.start(mContext, schoolName, splashs);
        app.appFinish();
    }

    public boolean checkMobileVersion(final School site, HashMap<String, String> versionRange)
    {
        String min = versionRange.get("min");
        String max = versionRange.get("max");

        int result = AppUtil.compareVersion(app.apiVersion, min);
        if (result == Const.LOW_VERSIO) {
            PopupDialog dlg = PopupDialog.createMuilt(
                    mContext,
                    "网校提示",
                    "您的客户端版本过低，无法登录该网校，请立即更新至最新版本。",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                String code = getResources().getString(R.string.app_code);
                                String updateUrl = String.format(
                                        "%s/%s?code=%s",
                                        site.url,
                                        Const.DOWNLOAD_URL,
                                        code
                                );
                                app.startUpdateWebView(updateUrl);
                            }
                        }
                    });

            dlg.setOkText("立即下载");
            dlg.show();
            return false;
        }

        result = AppUtil.compareVersion(app.apiVersion, max);
        if (result == Const.HEIGHT_VERSIO) {
            PopupDialog.createNormal(
                    mContext,
                    "网校提示",
                    "网校服务器版本过低，无法继续登录！请重新尝试。"
            ).show();
            return false;
        }

        return true;
    }
}