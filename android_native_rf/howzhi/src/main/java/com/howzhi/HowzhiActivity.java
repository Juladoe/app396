package com.howzhi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.*;
import com.edusoho.kuozhi.model.AppUpdateInfo;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.model.SchoolResult;
import com.edusoho.kuozhi.model.SystemInfo;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.ui.SplashActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

public class HowzhiActivity extends BaseActivity {

    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howzhi_start);
        mActivity = this;
        initApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initApp() {
        String app_host = getResources().getString(R.string.app_host);
        ajaxGetString(app_host + Const.VERIFYVERSION, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                SystemInfo info = app.gson.fromJson(
                        object, new TypeToken<SystemInfo>() {
                }.getType());

                if (info == null
                        || info.mobileApiUrl == null || "".equals(info.mobileApiUrl)) {
                    PopupDialog.createNormal(
                            mActivity, "提示信息", "网校客户端已关闭或网校服务器出现异常，请联系管理员！").show();
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
                            PopupDialog.createNormal(
                                    mActivity, "提示信息", "网校客户端已关闭或网校服务器出现异常，请联系管理员！").show();
                            return;
                        }
                        School site = schoolResult.site;
                        if (!checkMobileVersion(site.apiVersionRange)) {
                            return;
                        };

                        app.setCurrentSchool(site);
                        startApp();
                    }
                });
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                super.error(url, ajaxStatus);
                PopupDialog.createNormal(
                        mActivity, "提示信息", "网校服务器出现异常，请联系管理员!").show();
            }
        });
    }

    private void startApp() {
        EdusohoApp app = (EdusohoApp) getApplication();
        if (app.config.startWithSchool && app.defaultSchool != null) {
            app.mEngine.runNormalPlugin("DefaultPageActivity", this, null);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            return;
        }

        app.mEngine.runNormalPlugin("QrSchoolActivity", this, null);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
