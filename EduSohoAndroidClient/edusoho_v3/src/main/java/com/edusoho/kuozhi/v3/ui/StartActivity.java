package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.model.bal.SystemInfo;
import com.edusoho.kuozhi.v3.model.sys.AppConfig;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.model.sys.SchoolResult;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.HashMap;


public class StartActivity extends ActionBarBaseActivity implements MessageEngine.MessageCallback {

    public static final String INIT_APP = "init_app";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
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
        app.sendMessage(INIT_APP, null);
    }

    protected void initApp() {
        if (!app.getNetIsConnect()) {
            CommonUtil.longToast(this, "没有网络服务！请检查网络设置。");
            startApp();
            return;
        }
        if (app.host == null || "".equals(app.host)) {
            startApp();
            return;
        }

        checkSchoolApiVersion();
    }

    @Override
    public void invoke(WidgetMessage message) {
        if (message.type.type == INIT_APP) {
            initApp();
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        MessageType[] messageTypes = {new MessageType(MessageType.NONE, INIT_APP)};
        return messageTypes;
    }

    @Override
    protected void onDestroy() {
        app.unRegistMsgSource(this);
        super.onDestroy();
    }

    /**
     * 检查网校版本
     */
    private void checkSchoolVersion(SystemInfo systemInfo) {
        ajaxGet(systemInfo.mobileApiUrl + Const.VERIFYSCHOOL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                SchoolResult schoolResult = parseJsonValue(response.toString(), new TypeToken<SchoolResult>() {
                });

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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private boolean checkMobileVersion(
            HashMap<String, String> versionRange) {
        String min = versionRange.get("min");
        String max = versionRange.get("max");

        Log.d(null, "api max version" + max + " min " + min);
        int result = CommonUtil.compareVersion(app.apiVersion, min);

        if (result == Const.LOW_VERSIO) {
            PopupDialog popupDialog = PopupDialog.createMuilt(
                    mContext,
                    "网校提示",
                    "您的客户端版本过低，无法登录该网校，请立即更新至最新版本。\n或选择其他网校",
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
                                // TODO SCAN
                                //QrSchoolActivity.start(mActivity);
                                finish();
                            }
                        }
                    });
            popupDialog.setCancelText("选择新网校");
            popupDialog.setOkText("立即下载");
            popupDialog.show();
            return false;
        }

        result = CommonUtil.compareVersion(app.apiVersion, max);
        if (result == Const.HEIGHT_VERSIO) {
            PopupDialog popupDialog = PopupDialog.createMuilt(
                    mContext,
                    "网校提示",
                    "网校服务器版本过低，无法继续登录！请重新尝试。\n或选择其他网校",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                // TODO SCAN
                                //QrSchoolActivity.start(mActivity);
                                finish();
                            }
                        }
                    });

            popupDialog.setOkText("选择新网校");
            popupDialog.show();
            return false;
        }

        return true;
    }

    /**
     * 检查网校Api版本
     */
    private void checkSchoolApiVersion() {
        ajaxGet(app.host + Const.VERIFYVERSION, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                SystemInfo info = parseJsonValue(response.toString(), new TypeToken<SystemInfo>() {
                });
                if (info == null
                        || info.mobileApiUrl == null || "".equals(info.mobileApiUrl)) {

                    showSchoolErrorDlg();
                    return;
                }
                checkSchoolVersion(info);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    protected void startApp() {

        app.mEngine.runNormalPlugin("DefaultPageActivity", this, null);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
        return;


//        if (app.config.startWithSchool && app.defaultSchool != null) {
//            app.mEngine.runNormalPlugin("DefaultPageActivity", this, null);
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            finish();
//            return;
//        }
//
//        app.mEngine.runNormalPlugin("QrSchoolActivity", this, null);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        finish();
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
                            // TODO SCAN
                            //QrSchoolActivity.start(mActivity);
                            finish();
                        }
                    }
                }
        );
        popupDialog.setOkText("选择新网校");
        popupDialog.show();
    }

    private void registDevice() {
        Log.d(null, "registDevice->");
        AppConfig config = app.config;
        if (config.isPublicRegistDevice && config.isRegistDevice) {
            return;
        }

        HashMap<String, String> params = app.getPlatformInfo();

        if (!config.isPublicRegistDevice) {
            RequestUrl requestUrl = new RequestUrl(Const.MOBILE_REGIST);
            requestUrl.setParams(params);
            app.postUrl(requestUrl, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Boolean result = app.gson.fromJson(
                                response.toString(), new TypeToken<Boolean>() {
                                }.getType()
                        );

                        if (true == result) {
                            app.config.isPublicRegistDevice = true;
                            app.saveConfig();
                        }
                    } catch (Exception e) {
                        Log.e(null, e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }

        if (!config.isRegistDevice) {
            RequestUrl requestUrl = new RequestUrl(app.schoolHost + Const.REGIST_DEVICE);
            requestUrl.setParams(params);
            app.postUrl(requestUrl, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(null, "regist device to school");
                    try {
                        Boolean result = app.gson.fromJson(
                                response.toString(), new TypeToken<Boolean>() {
                                }.getType()
                        );

                        if (true == result) {
                            app.config.isRegistDevice = true;
                            app.saveConfig();
                        }
                    } catch (Exception e) {
                        Log.e(null, e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }
}
