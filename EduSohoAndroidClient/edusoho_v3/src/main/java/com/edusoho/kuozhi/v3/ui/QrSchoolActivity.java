package com.edusoho.kuozhi.v3.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.provider.IMServiceProvider;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.model.sys.Token;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.kuozhi.v3.view.photo.SchoolSplashActivity;
import com.edusoho.kuozhi.v3.view.qr.CaptureActivity;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JesseHuang on 15/5/6.
 * 扫描网校界面
 */
public class QrSchoolActivity extends ActionBarBaseActivity {
    private Button mQrSearchBtn;
    private TextView tvOther;
    public final static int REQUEST_QR = 0001;
    public final static int RESULT_QR = 0002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_school);
        setBackMode(BACK, "进入网校");
        initView();
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
        mQrSearchBtn = (Button) findViewById(R.id.qr_search_btn);
        mQrSearchBtn.setOnClickListener(mSearchClickListener);
        tvOther = (TextView) findViewById(R.id.qr_other_btn);
        tvOther.setOnClickListener(mOtherClickListener);
    }

    private View.OnClickListener mSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent qrIntent = new Intent();
            qrIntent.setClass(QrSchoolActivity.this, CaptureActivity.class);
            startActivityForResult(qrIntent, REQUEST_QR);
        }
    };

    private View.OnClickListener mOtherClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MobclickAgent.onEvent(mContext,"i_mySetting_otherWaysEnter");
            app.mEngine.runNormalPlugin("NetSchoolActivity", mContext, null);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_QR && resultCode == RESULT_QR) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                new SchoolChangeHandler(mActivity).change(result + "&version=2");
            }
        }
    }

    public static class SchoolChangeHandler {
        private EdusohoApp mApp;
        private BaseActivity mActivity;
        private LoadDialog mLoading;

        private Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mLoading.dismiss();
                CommonUtil.longToast(mActivity.getBaseContext(), "二维码信息错误!");
            }
        };

        public SchoolChangeHandler(BaseActivity activity) {
            this.mActivity = activity;
            this.mApp = mActivity.app;
        }

        private void showSchSplash(String schoolName, String[] splashs) {
            if (splashs == null || splashs.length == 0) {
                mApp.mEngine.runNormalPlugin("DefaultPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }
                });
            }
            SchoolSplashActivity.start(mActivity.getBaseContext(), schoolName, splashs);
            mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            mActivity.finish();
        }

        protected void startSchoolActivity(School site) {
            mLoading.dismiss();
            showSchSplash(site.name, site.splashs);
        }

        protected AppSettingProvider getAppSettingProvider() {
            return FactoryManager.getInstance().create(AppSettingProvider.class);
        }

        protected void bindApiToken(final UserResult userResult) {
            School school = userResult.site;

            RequestUrl requestUrl = new RequestUrl(school.host + Const.GET_API_TOKEN);
            Map<String,String> tokenMap =  ApiTokenUtil.getToken(mActivity.getBaseContext());
            requestUrl.heads.put("Auth-Token", tokenMap.get("token"));
            mApp.getUrl(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Token token = mActivity.parseJsonValue(response, new TypeToken<Token>() {
                    });
                    if (token != null) {
                        mApp.saveApiToken(token.token);
                        selectSchool(userResult);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CommonUtil.longToast(mActivity.getBaseContext(), "获取网校信息失败");
                }
            });
        }

        public void selectSchool(UserResult userResult) {
            IMClient.getClient().destory();
            School site = userResult.site;
            mApp.setCurrentSchool(site);
            mApp.registDevice(null);

            if (userResult.token == null || "".equals(userResult.token)) {
                //未登录二维码
                mApp.removeToken();
                getAppSettingProvider().setUser(null);
                mApp.sendMessage(Const.LOGOUT_SUCCESS, null);
            } else {
                //扫描登录用户二维码
                mApp.saveToken(userResult);
                new IMServiceProvider(mActivity.getApplicationContext()).bindServer(userResult.user.id, userResult.user.nickname);
                mApp.sendMessage(Const.LOGIN_SUCCESS, null);
            }
            startSchoolActivity(site);
        }

        public void change(String url) {
            mLoading = LoadDialog.create(mActivity);
            mLoading.show();

            RequestUrl requestUrl = new RequestUrl(url);
            mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        final UserResult userResult = mApp.gson.fromJson(
                                response, new TypeToken<UserResult>() {
                                }.getType());

                        if (userResult == null) {
                            CommonUtil.longToast(mActivity.getBaseContext(), "二维码信息错误!");
                            return;
                        }

                        School site = userResult.site;
                        if (site == null) {
                            CommonUtil.longToast(mActivity.getBaseContext(), "没有识别到网校信息!");
                            return;
                        }
                        if (!checkMobileVersion(site, site.apiVersionRange)) {
                            return;
                        }

                        bindApiToken(userResult);

                    } catch (Exception e) {
                        mLoading.dismiss();
                        CommonUtil.longToast(mActivity.getBaseContext(), "二维码信息错误!");
                    }
                }
            }, errorListener);
        }

        private boolean checkMobileVersion(final School site, HashMap<String, String> versionRange) {
            String min = versionRange.get("min");
            String max = versionRange.get("max");

            int result = AppUtil.compareVersion(mApp.apiVersion, min);
            if (result == Const.LOW_VERSIO) {
                PopupDialog dlg = PopupDialog.createMuilt(
                        mActivity,
                        "网校提示",
                        "您的客户端版本过低，无法登录该网校，请立即更新至最新版本。",
                        new PopupDialog.PopupClickListener() {
                            @Override
                            public void onClick(int button) {
                                if (button == PopupDialog.OK) {
                                    String code = mActivity.getResources().getString(R.string.app_code);
                                    String updateUrl = String.format(
                                            "%s/%s?code=%s",
                                            site.url,
                                            Const.DOWNLOAD_URL,
                                            code
                                    );
                                    mApp.startUpdateWebView(updateUrl);
                                }
                            }
                        });

                dlg.setOkText("立即下载");
                dlg.show();
                return false;
            }

            result = AppUtil.compareVersion(mApp.apiVersion, max);
            if (result == Const.HEIGHT_VERSIO) {
                PopupDialog.createNormal(
                        mActivity,
                        "网校提示",
                        "网校服务器版本过低，无法继续登录！请重新尝试。"
                ).show();
                return false;
            }

            return true;
        }
    }
}
