package com.edusoho.kuozhi.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.model.AppUpdateInfo;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.model.SchoolResult;
import com.edusoho.kuozhi.model.SystemInfo;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.NormalCallback;
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

    private void searchSchool(String searchStr)
    {
        if (TextUtils.isEmpty(searchStr)) {
            longToast("请输入搜索网校url");
            return;
        }

        String url = "http://" + searchStr + Const.VERIFYVERSION;

        final LoadDialog loading = LoadDialog.create(mContext);
        loading.show();
        app.query.ajax(url, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                loading.dismiss();
                int code = status.getCode();
                if (code != Const.OK) {
                    PopupDialog.createNormal(mContext, "提示信息", "网络异常！请检查网络链接").show();
                    return;
                }

                try {
                    SystemInfo info = app.gson.fromJson(
                            object, new TypeToken<SystemInfo>() {
                    }.getType());

                    if (info.mobileApiUrl == null || "".equals(info.mobileApiUrl)) {
                        PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
                        return;
                    }
                    ajaxNormalGet(info.mobileApiUrl + Const.VERIFYSCHOOL, new ResultCallback(){
                        @Override
                        public void callback(String url, String object, AjaxStatus ajaxStatus) {
                            super.callback(url, object, ajaxStatus);
                            SchoolResult schoolResult = app.gson.fromJson(
                                    object, new TypeToken<SchoolResult>() {
                            }.getType());

                            if (schoolResult == null) {
                                PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
                                return;
                            }
                            School site = schoolResult.site;
                            if (!checkMobileVersion(site.apiVersionRange)) {
                                return;
                            };

                            showSchSplash(site.name, site.splashs);
                            app.setCurrentSchool(site);
                        }
                    });

                } catch (Exception e) {
                    PopupDialog.createNormal(mContext, "错误信息", "没有搜索到网校").show();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            app.exit();
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
        finish();
    }

    public boolean checkMobileVersion(HashMap<String, String> versionRange)
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
                                app.updateApp(Const.DEFAULT_UPDATE_URL, true, new NormalCallback() {
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