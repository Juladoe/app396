package com.edusoho.kuozhi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.model.sys.Token;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.StartActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.google.gson.reflect.TypeToken;


public class KuozhiActivity extends StartActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtil.initConfig(new String[]{
                getResources().getString(R.string.app_code)
        });
    }

    @Override
    public void startSplash() {
        initApp();
    }

    @Override
    protected void startAnim() {
        startSplash();
    }

    protected void initApp() {
        if (!app.getNetIsConnect()) {
            CommonUtil.longToast(this, "没有网络服务！请检查网络设置。");
            startApp();
            return;
        }

        checkSchoolApiVersion();
    }

    private void setDefaultSchool() {
        School school = new School();
        school.host = app.host;
        school.url = app.host + "/mapi_v2";
        school.name = getResources().getString(R.string.app_name);
        school.logo = "";
        app.setCurrentSchool(school);
    }

    protected void showSchoolErrorDlg() {
        PopupDialog popupDialog = PopupDialog.createMuilt(
                mContext,
                "提示信息",
                "网校客户端已关闭或网校服务器出现异常。\n请联系管理员！或选择新网校",
                new PopupDialog.PopupClickListener() {
                    @Override
                    public void onClick(int button) {
                        if (button == PopupDialog.OK) {
                            finish();
                        }
                    }
                }
        );
        popupDialog.setOkText("退出");
        popupDialog.show();
    }

    @Override
    protected void checkSchoolApiVersion() {
        super.checkSchoolApiVersion();
        bindApiToken(app.host);
    }

    protected void startApp() {
        School school = getAppSettingProvider().getCurrentSchool();
        if (school == null) {
            setDefaultSchool();
        }
        if (app.config.showSplash && school.splashs != null && school.splashs.length > 0) {
            app.mEngine.runNormalPlugin("SplashActivity", this, null);
            app.config.showSplash = false;
            app.saveConfig();
            return;
        }
        app.mEngine.runNormalPlugin("DefaultPageActivity", this, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                if (mCurrentIntent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
                    startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
                startIntent.putExtras(mCurrentIntent);
            }
        });
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void bindApiToken(String host) {
        ajaxGet(host + Const.GET_API_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Token token = parseJsonValue(response, new TypeToken<Token>() {
                });
                if (token == null || TextUtils.isEmpty(token.token)) {
                    CommonUtil.longToast(mContext, "绑定网校信息失败");
                    return;
                }
                app.registDevice(null);
                app.saveApiToken(token.token);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
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
}