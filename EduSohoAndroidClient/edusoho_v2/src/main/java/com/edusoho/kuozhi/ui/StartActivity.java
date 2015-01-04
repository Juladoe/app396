package com.edusoho.kuozhi.ui;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.handler.ClientVersionHandler;
import com.edusoho.kuozhi.AppConfig;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.model.SchoolResult;
import com.edusoho.kuozhi.model.SystemInfo;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.common.QrSchoolActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import android.os.Bundle;
import android.util.Log;

import java.util.Map;

public class StartActivity extends ActionBarBaseActivity
        implements MessageEngine.MessageCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        app.registMsgSource(this);
        startSplash();
        registDevice();
    }

    public void startSplash() {
        if (app.config.showSplash) {
            app.mEngine.runNormalPlugin("SplashActivity", this, null);
            app.config.showSplash = false;
            app.saveConfig();
            return;
        }

        app.sendMessage(SplashActivity.INIT_APP, null);
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

    protected void initApp() {
        if (!app.getNetIsConnect()) {
            longToast("没有网络服务！请检查网络设置。");
            startApp();
            return;
        }
        if (app.host == null || "".equals(app.host)) {
            startApp();
            return;
        }

        checkSchoolApiVersion(app.host);
    }

    protected ClientVersionHandler getClientVersionHandler() {
        return null;
    }

    /**
     * 检查网校版本
     *
     * @param info
     */
    protected void checkSchoolVersion(SystemInfo info) {
        ajaxNormalGet(info.mobileApiUrl + Const.VERIFYSCHOOL, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                SchoolResult schoolResult = app.gson.fromJson(
                        object, new TypeToken<SchoolResult>() {
                        }.getType()
                );

                if (schoolResult == null) {
                    showSchoolErrorDlg();
                    return;
                }
                School site = schoolResult.site;
                if (!checkMobileVersion(site.apiVersionRange, getClientVersionHandler())) {
                    return;
                }

                app.setCurrentSchool(site);
                startApp();
            }
        });
    }

    /**
     * 检测网校api版本
     */
    protected void checkSchoolApiVersion(String host) {
        ajaxNormalGet(app.host + Const.VERIFYVERSION, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                SystemInfo info = app.gson.fromJson(
                        object, new TypeToken<SystemInfo>() {
                        }.getType()
                );
                if (info == null
                        || info.mobileApiUrl == null || "".equals(info.mobileApiUrl)) {

                    showSchoolErrorDlg();
                    return;
                }

                checkSchoolVersion(info);
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                super.error(url, ajaxStatus);
                showSchoolErrorDlg();
            }
        });
    }

    /**
     * 处理网校异常dlg
     */
    protected void showSchoolErrorDlg() {
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
                }
        );
        popupDialog.setOkText("选择新网校");
        popupDialog.show();
    }

    protected void startApp() {
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

    private void registDevice() {
        Log.d(null, "registDevice->");
        AppConfig config = app.config;
        if (config.isPublicRegistDevice && config.isRegistDevice) {
            return;
        }

        Map<String, String> params = app.getPlatformInfo();

        if (!config.isPublicRegistDevice) {
            app.logToServer(Const.MOBILE_REGIST, params, new AjaxCallback<String>() {
                @Override
                public void callback(String url, String object, AjaxStatus status) {
                    super.callback(url, object, status);
                    Log.d(null, "regist device to public");
                    try {
                        Boolean result = app.gson.fromJson(
                                object, new TypeToken<Boolean>() {
                                }.getType()
                        );

                        if (true == result) {
                            app.config.isPublicRegistDevice = true;
                            app.saveConfig();
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }

        if (!config.isRegistDevice) {
            app.logToServer(app.schoolHost + Const.REGIST_DEVICE, params, new AjaxCallback<String>() {
                @Override
                public void callback(String url, String object, AjaxStatus status) {
                    super.callback(url, object, status);
                    Log.d(null, "regist device to school");
                    try {
                        Boolean result = app.gson.fromJson(
                                object, new TypeToken<Boolean>() {
                                }.getType()
                        );

                        if (true == result) {
                            app.config.isRegistDevice = true;
                            app.saveConfig();
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }
    }
}
