package com.edusoho.kuozhi;


import android.os.Bundle;
import android.util.Log;

import com.androidquery.callback.AjaxStatus;
import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.edusoho.handler.ClientVersionHandler;
import com.edusoho.kuozhi.model.ErrorResult;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.model.SchoolResult;
import com.edusoho.kuozhi.model.SystemInfo;
import com.edusoho.kuozhi.ui.StartActivity;
import com.edusoho.kuozhi.ui.common.QrSchoolActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class KuozhiActivity extends StartActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initChannel();
    }

    @Override
    protected void initApp() {
        if (!app.getNetIsConnect()) {
            longToast("没有网络服务！请检查网络设置。");
            return;
        }
        String host = getResources().getString(R.string.app_host);
        checkSchoolApiVersion(host);
    }

    @Override
    protected boolean handlerError(String errorStr) {
        try {
            ErrorResult result = app.gson.fromJson(
                    errorStr, new TypeToken<ErrorResult>() {
                    }.getType());
            if (result != null) {
                com.edusoho.kuozhi.model.Error error = result.error;
                if (Const.CLIENT_CLOSE.equals(error.name)) {
                    PopupDialog popupDialog = PopupDialog.createMuilt(
                            mContext,
                            "系统提示",
                            error.message,
                            new PopupDialog.PopupClickListener() {
                                @Override
                                public void onClick(int button) {
                                    if (button == PopupDialog.OK) {
                                        finish();
                                    }
                                }
                            });
                    popupDialog.setOkText("退出");
                    popupDialog.show();
                    return true;
                }
                longToast(result.error.message);
                return true;
            }
        } catch (Exception e) {
            //result error
        }
        return false;
    }

    @Override
    protected void checkSchoolVersion(SystemInfo info) {
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
                CustomClientVersionHandler handler = new CustomClientVersionHandler(mActivity);
                if (!checkMobileVersion(site.apiVersionRange, handler)) {
                    return;
                }

                app.setCurrentSchool(site);
                startApp();
            }
        });
    }

    @Override
    public boolean checkMobileVersion(
            HashMap<String, String> versionRange, ClientVersionHandler handler) {
        String min = versionRange.get("min");
        String max = versionRange.get("max");

        Log.d(null, "api max version" + max + " min " + min);
        int result = AppUtil.compareVersion(app.apiVersion, min);
        if (handler != null) {
            return handler.execute(min, max, app.apiVersion);
        }

        if (result == Const.LOW_VERSIO) {
            PopupDialog popupDialog = PopupDialog.createMuilt(
                    mContext,
                    "网校提示",
                    "您的客户端版本过低，无法登录该网校，请立即更新至最新版本。",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                String code = getResources().getString(R.string.app_code);
                                String updateUrl = String.format(
                                        "%s%s?code=%s",
                                        app.schoolHost,
                                        Const.DOWNLOAD_URL,
                                        code
                                );
                                app.startUpdateWebView(updateUrl);
                            } else {
                                finish();
                            }
                        }
                    });
            popupDialog.setCancelText("下次更新");
            popupDialog.setOkText("立即下载");
            popupDialog.show();
            return false;
        }

        result = AppUtil.compareVersion(app.apiVersion, max);
        if (result == Const.HEIGHT_VERSIO) {
            PopupDialog popupDialog = PopupDialog.createMuilt(
                    mContext,
                    "网校提示",
                    "网校服务器版本过低，无法继续登录！请重新尝试。",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            finish();
                        }
                    });

            popupDialog.setOkText("退出");
            popupDialog.show();
            return false;
        }

        return true;
    }

    @Override
    protected void showSchoolErrorDlg() {
        PopupDialog.createNormal(
                mContext,
                "提示信息",
                "网校客户端已关闭或网校服务器出现异常"
        ).show();
    }

    @Override
    protected void startApp() {
        if (app.config.startWithSchool && app.defaultSchool != null) {
            app.mEngine.runNormalPlugin("DefaultPageActivity", this, null);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            return;
        }
    }

    private void initChannel() {
        //AppKey不用改
        StatService.setAppKey("8f1996ac26");
        //定制项目，app_name就是渠道号
        StatService.setAppChannel(this, getString(R.string.channel_name), true);
        StatService.setSessionTimeOut(30);
        StatService.setOn(this, StatService.EXCEPTION_LOG);
        StatService.setLogSenderDelayed(0);
        StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 0);
        StatService.setDebugOn(false);
    }
}
