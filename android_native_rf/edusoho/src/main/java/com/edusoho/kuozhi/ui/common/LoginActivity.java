package com.edusoho.kuozhi.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.model.AppUpdateInfo;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.ResultCallback;
import com.edusoho.plugin.qr.CaptureActivity;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class LoginActivity extends BaseActivity {
    public static final int EXIT = 1002;
    public static final int LOGIN = 1001;
    public static final int OK = 1003;
    private AQuery aq;

    private Handler workHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        initView();
    }

    protected void setLayout()
    {
        setContentView(R.layout.login);
    }

    public static void start(Activity context)
    {
        EdusohoApp.app.mEngine.runNormalPlugin("LoginActivity", context, null);
    }

    public static void startForResult(Activity context)
    {
        EdusohoApp.app.mEngine.runNormalPluginForResult("LoginActivity", context, LOGIN, null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(EXIT);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case RegistActivity.RESULT:
                finish();
                break;
            case QrSchoolActivity.REQUEST_QR:
                if (resultCode == QrSchoolActivity.RESULT_QR && data != null) {
                    Bundle bundle = data.getExtras();
                    String result = bundle.getString("result");
                    showQrResultDlg(result);
                }
                break;
        }
    }

    private void showQrResultDlg(final String result)
    {
        Log.d(null, "qr result->" + result + " app.host->" + app.host);
        if (!result.startsWith(app.host)) {
            longToast("请登录" + app.defaultSchool.name + "－网校！");
            return;
        }

        final LoadDialog loading = LoadDialog.create(mContext);
        loading.show();
        app.query.ajax(result, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                int code = status.getCode();
                if (code != Const.OK) {
                    loading.dismiss();
                    longToast("二维码信息错误!");
                    return;
                }
                try {
                    final TokenResult schoolResult = app.gson.fromJson(
                            object, new TypeToken<TokenResult>() {
                    }.getType());
                    if (schoolResult == null) {
                        loading.dismiss();
                        longToast("二维码信息错误!");
                        return;
                    }

                    final School site = schoolResult.site;

                    if (schoolResult.token == null || "".equals(schoolResult.token)) {
                        loading.dismiss();
                        app.removeToken();
                        longToast("二维码登录信息已过期或失效!");
                    } else {
                        workHandler.postAtTime(new Runnable() {
                            @Override
                            public void run() {
                                loading.dismiss();
                                app.saveToken(schoolResult);
                                app.setCurrentSchool(site);
                                setResult(EXIT);
                                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                finish();
                            }
                        }, SystemClock.uptimeMillis() + 500);

                    }
                }catch (Exception e) {
                    loading.dismiss();
                    longToast("二维码信息错误!");
                }
            }
        });
    }

    private void initView() {
        setBackMode("登录", true, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(EXIT);
                finish();
            }
        });

        workHandler = new Handler();
        aq = new AQuery(this);

        aq.id(R.id.regist_btn).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registIntent = new Intent();
                registIntent.setClass(mContext, RegistActivity.class);
                startActivityForResult(registIntent, RegistActivity.REQUEST);
            }
        });

        aq.id(R.id.qr_login_btn).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qrIntent = new Intent();
                qrIntent.setClass(mContext, CaptureActivity.class);
                startActivityForResult(qrIntent, QrSchoolActivity.REQUEST_QR);
            }
        });

        aq.id(R.id.login_btn).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = aq.id(R.id.login_email_edt).getText().toString();
                if (TextUtils.isEmpty(email)) {
                    longToast("请输入用户名或者邮箱");
                    return;
                }

                String pass = aq.id(R.id.login_pass_edt).getText().toString();
                if (TextUtils.isEmpty(pass)) {
                    longToast("请输入密码");
                    return;
                }

                loginUser(email, pass);
            }
        });
    }

    protected void loginUser(String email, String pass)
    {
        StringBuffer params = new StringBuffer(Const.LOGIN);
        params.append("?_username=").append(email);
        params.append("&_password=").append(pass);

        String url = app.bindToken2Url(params.toString(), false);
        ajaxGetString(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                TokenResult result = app.gson.fromJson(
                        object, new TypeToken<TokenResult>(){}.getType());
                if (result != null) {
                    app.saveToken(result);
                    setResult(OK);
                    finish();
                } else {
                    longToast("用户名或密码错误！");
                }
            }
        });
    }
}
