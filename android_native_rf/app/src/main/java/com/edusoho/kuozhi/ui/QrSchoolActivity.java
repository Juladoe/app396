package com.edusoho.kuozhi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.model.AppUpdateInfo;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.LoadDialog;
import com.edusoho.kuozhi.view.PopupDialog;
import com.edusoho.listener.NormalCallback;
import com.edusoho.plugin.photo.SchoolSplashActivity;
import com.edusoho.plugin.qr.CaptureActivity;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class QrSchoolActivity extends BaseActivity {

    private Button qr_search_btn;
    private View normal_login_btn;

    public final static int REQUEST_QR = 0001;
    public final static int RESULT_QR = 0002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrsch_layout);
        initView();
        app.addTask("QrSchoolActivity", this);
        updateApp();
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
        normal_login_btn = findViewById(R.id.normal_login_btn);
        qr_search_btn = (Button) findViewById(R.id.qr_search_btn);
        qr_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qrIntent = new Intent();
                qrIntent.setClass(mContext, CaptureActivity.class);
                startActivityForResult(qrIntent, REQUEST_QR);
            }
        });

        normal_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetSchoolActivity.start(mActivity);
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
                showQrResultDlg(result);
            }
        }
    }

    private void showQrResultDlg(final String result)
    {
        final LoadDialog loading = LoadDialog.create(mContext);
        loading.show();
        app.query.ajax(result, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                loading.dismiss();
                int code = status.getCode();
                if (code != Const.OK) {
                    longToast("二维码信息错误!");
                    return;
                }
                try {
                    final TokenResult schoolResult = app.gson.fromJson(
                            object, new TypeToken<TokenResult>() {
                    }.getType());

                    if (schoolResult == null) {
                        longToast("二维码信息错误!");
                        return;
                    }

                    School site = schoolResult.site;
                    if (!checkMobileVersion(site.apiVersionRange)) {
                        return;
                    };

                    showSchSplash(site.name, site.splashs);

                    if (schoolResult.token != null && ! "".equals(schoolResult.token)) {
                        app.saveToken(schoolResult);
                    }
                    app.setCurrentSchool(site);

                }catch (Exception e) {
                    longToast("二维码信息错误!");
                }
            }
        });

    }

    private void showSchSplash(String schoolName, String[] splashs)
    {
        SchoolSplashActivity.start(mContext, schoolName, splashs);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }

    private boolean checkMobileVersion(HashMap<String, String> versionRange)
    {
        String min = versionRange.get("min");
        String max = versionRange.get("max");

        int result = AppUtil.compareVersion(app.apiVersion, min);
        if (result == Const.LOW_VERSIO) {
            PopupDialog dlg = PopupDialog.createMuilt(
                    mContext,
                    "网校提示",
                    "您的客户端版本过低，无法登录，请立即更新至最新版本。",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                app.updateApp(true, new NormalCallback() {
                                    @Override
                                    public void success(Object obj) {
                                        AppUpdateInfo appUpdateInfo = (AppUpdateInfo) obj;
                                        app.startUpdateWebView(appUpdateInfo.updateUrl);
                                    }
                                });
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
                    "服务器维护中，请稍后再试。"
            ).show();
            return false;
        }

        return true;
    }
}