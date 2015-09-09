package com.edusoho.kuozhi.v3.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.model.sys.Token;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.kuozhi.v3.view.photo.SchoolSplashActivity;
import com.edusoho.kuozhi.v3.view.qr.CaptureActivity;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

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

    public static class SchoolChangeHandler
    {
        private EdusohoApp mApp;
        private BaseActivity mActivity;

        public SchoolChangeHandler(BaseActivity activity)
        {
            this.mActivity = activity;
            this.mApp = mActivity.app;
        }

        private void showSchSplash(String schoolName, String[] splashs) {
            SchoolSplashActivity.start(mActivity, schoolName, splashs);
        }

        public void change(String url) {
            Log.d(TAG, url);
            final LoadDialog loading = LoadDialog.create(mActivity);
            loading.show();

            RequestUrl requestUrl = new RequestUrl(url);
            mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loading.dismiss();
                    try {
                        final UserResult userResult = mApp.gson.fromJson(
                                response, new TypeToken<UserResult>() {
                                }.getType());

                        if (userResult == null) {
                            CommonUtil.longToast(mActivity.getBaseContext(), "二维码信息错误!");
                            return;
                        }

                        final School site = userResult.site;
                        if (!checkMobileVersion(site, site.apiVersionRange)) {
                            return;
                        }

                        if (userResult.token == null || "".equals(userResult.token)) {
                            //未登录二维码
                            mApp.removeToken();
                            mApp.sendMessage(Const.LOGOUT_SUCCESS, null);
                        } else {
                            //扫描登录用户二维码
                            mApp.saveToken(userResult);
                            mApp.sendMessage(Const.LOGIN_SUCCESS, null);
                        }

                        mApp.setCurrentSchool(site);
                        SqliteChatUtil.getSqliteChatUtil(mActivity.getBaseContext(), mApp.domain).close();
                        showSchSplash(site.name, site.splashs);
                        mApp.registDevice(new NormalCallback() {
                            @Override
                            public void success(Object obj) {
                                showSchSplash(site.name, site.splashs);
                            }
                        });

                        RequestUrl requestUrl = mApp.bindUrl(Const.GET_API_TOKEN, false);
                        mApp.getUrl(requestUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Token token = mActivity.parseJsonValue(response, new TypeToken<Token>() {
                                });
                                if (token != null) {
                                    mApp.saveApiToken(token.token);
                                    Bundle bundle = new Bundle();
                                    bundle.putString(Const.BIND_USER_ID, userResult.user.id + "");
                                    mApp.pushRegister(bundle);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "无法获取网校Token");
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        CommonUtil.longToast(mActivity.getBaseContext(), "二维码信息错误!");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    CommonUtil.longToast(mActivity.getBaseContext(), "二维码信息错误!");
                }
            });
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
