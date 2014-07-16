package com.howzhi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.*;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.model.SystemInfo;
import com.edusoho.kuozhi.ui.SplashActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.google.gson.reflect.TypeToken;

public class HowzhiActivity extends Activity {

    private Handler mWorkHandler;
    private EdusohoApp app;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howzhi_start);
        mActivity = this;
        app = (EdusohoApp) getApplication();
        initApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initApp() {
        if ("".equals(app.schoolHost)) {
            final LoadDialog loading = LoadDialog.create(mActivity);
            loading.show();
            String app_host = getResources().getString(R.string.app_host);

            app.query.ajax(app_host + Const.VERIFYVERSION, String.class, new AjaxCallback<String>() {
                @Override
                public void callback(String url, String object, AjaxStatus status) {
                    super.callback(url, object, status);
                    loading.dismiss();
                    SystemInfo info = app.gson.fromJson(
                            object, new TypeToken<SystemInfo>() {
                    }.getType());
                    if (info == null
                            ||info.mobileApiUrl == null || "".equals(info.mobileApiUrl)) {
                        PopupDialog.createNormal(mActivity, "提示信息", "网校客户端已关闭或网校服务器出现异常，请联系管理员！").show();
                        return;
                    }

                    School school = app.getDefaultSchool(
                            info.mobileApiUrl, getResources().getString(R.string.app_name));
                    app.setCurrentSchool(school);
                    startApp();
                }
            });
            return;
        }

        mWorkHandler = new Handler();
        mWorkHandler.postAtTime(new Runnable() {
            @Override
            public void run() {
                startApp();
            }
        }, SystemClock.uptimeMillis() + 1200);
    }

    private void startApp() {
        EdusohoApp app = (EdusohoApp) getApplication();
        if (app.config.startWithSchool && app.defaultSchool != null) {
            app.mEngine.runNormalPlugin("SchoolCourseActivity", this, null);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            return;
        }

        app.mEngine.runNormalPlugin("QrSchoolActivity", this, null);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}
