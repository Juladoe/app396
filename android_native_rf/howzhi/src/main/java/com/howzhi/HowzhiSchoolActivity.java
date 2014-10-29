package com.howzhi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.model.AppUpdateInfo;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.ui.DefaultPageActivity;
import com.edusoho.kuozhi.ui.common.QrSchoolActivity;
import com.edusoho.kuozhi.ui.course.SchoolCourseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.NormalCallback;
import com.edusoho.plugin.photo.SchoolSplashActivity;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 14-7-7.
 */
public class HowzhiSchoolActivity extends SchoolCourseActivity {

    @Override
    public void setActionBar() {
        setMenu(R.layout.howzhi_course_layout_menu, new MenuListener() {
            @Override
            public void bind(View menuView) {
                View sch_qr_search_btn = menuView.findViewById(R.id.sch_qr_search_btn);
                sch_qr_search_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        app.sendMessage(DefaultPageActivity.COLUMN_MENU, null);
                    }
                });

                View sch_search_btn = menuView.findViewById(R.id.sch_search_btn);
                sch_search_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        app.mEngine.runNormalPlugin("SearchActivity", mActivity, null);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QrSchoolActivity.REQUEST_QR && resultCode == QrSchoolActivity.RESULT_QR) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                showQrResultDlg(result);
            }
        }
    }

    private void showQrResultDlg(final String result)
    {
        if (!result.startsWith(app.host)) {
            longToast("请登录" + getString(R.string.app_name) + "－网校！");
            return;
        }

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

                    if (schoolResult.token == null || "".equals(schoolResult.token)) {
                        app.removeToken();
                    } else {
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

    @Override
    protected void updateApp()
    {
        app.updateApp(app.schoolHost + "app_version", false, new NormalCallback() {
            @Override
            public void success(Object obj) {
                final AppUpdateInfo appUpdateInfo = (AppUpdateInfo) obj;
                String newVersion = appUpdateInfo.androidVersion;
                int result = AppUtil.compareVersion(app.getApkVersion(), newVersion);
                if (result == Const.LOW_VERSIO) {
                    PopupDialog.createMuilt(
                            mActivity,
                            "版本更新",
                            "当前有新版本，是否更新?", new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                app.startUpdateWebView(appUpdateInfo.updateUrl);
                            }
                        }
                    }).show();
                }
            }
        });
    }
}
