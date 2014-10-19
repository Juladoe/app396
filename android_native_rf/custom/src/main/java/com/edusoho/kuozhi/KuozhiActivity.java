package com.edusoho.kuozhi;

import android.os.Bundle;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.model.SchoolResult;
import com.edusoho.kuozhi.model.SystemInfo;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;

import com.edusoho.kuozhi.ui.SplashActivity;
import com.edusoho.kuozhi.ui.common.QrSchoolActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

public class KuozhiActivity extends ActionBarBaseActivity implements MessageEngine.MessageCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kuozhi_start);
        app.registMsgSource(this);
        startSplash();
    }

    @Override
    protected void onDestroy() {
        app.unRegistMsgSource(this);
        super.onDestroy();
    }

    @Override
    public MessageType[] getMsgTypes() {
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(MessageType.NONE, SplashActivity.INIT_APP)
        };

        return messageTypes;
    }

    @Override
    public void invoke(WidgetMessage message) {
        initApp();
    }

    private void initApp() {
        if (!app.getNetStatus()) {
            longToast("没有网络服务！请检查网络设置。");
            return;
        }

        String host = getResources().getString(R.string.app_host);

        ajaxNormalGet(host + Const.VERIFYVERSION, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                SystemInfo info = app.gson.fromJson(
                        object, new TypeToken<SystemInfo>() {
                }.getType());
                if (info == null
                        || info.mobileApiUrl == null || "".equals(info.mobileApiUrl)) {

                    showSchoolErrorDlg();
                    return;
                }

                ajaxNormalGet(info.mobileApiUrl + Const.VERIFYSCHOOL, new ResultCallback() {
                    @Override
                    public void callback(String url, String object, AjaxStatus ajaxStatus) {
                        super.callback(url, object, ajaxStatus);
                        SchoolResult schoolResult = app.gson.fromJson(
                                object, new TypeToken<SchoolResult>() {
                        }.getType());

                        if (schoolResult == null) {
                            showSchoolErrorDlg();
                            return;
                        }
                        School site = schoolResult.site;
                        if (!checkMobileVersion(site.apiVersionRange)) {
                            return;
                        }

                        app.setCurrentSchool(site);
                        startApp();
                    }
                });
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                super.error(url, ajaxStatus);
                showSchoolErrorDlg();
            }
        });
    }

    private void showSchoolErrorDlg()
    {
        PopupDialog popupDialog = PopupDialog.createMuilt(
                mContext,
                "提示信息",
                "网校客户端已关闭或网校服务器出现异常。\n请联系管理员！或选择新网校",
                new PopupDialog.PopupClickListener() {
                    @Override
                    public void onClick(int button) {
                        if (button == PopupDialog.OK) {
                            QrSchoolActivity.start(mActivity);
                            finish();
                        }
                    }
                });
        popupDialog.setOkText("选择新网校");
        popupDialog.show();
    }

    private void startApp() {
        if (app.config.startWithSchool && app.defaultSchool != null) {
            app.mEngine.runNormalPlugin("DefaultPageActivity", this, null);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            return;
        }
    }
}
