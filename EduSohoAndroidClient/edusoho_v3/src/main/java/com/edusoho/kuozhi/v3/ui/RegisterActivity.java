package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.register.MsgCode;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoLoadingButton;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JesseHuang on 15/6/8.
 */
public class RegisterActivity extends ActionBarBaseActivity {
    private EditText etPhone;
    private EditText etCode;
    private EditText etPhonePass;
    private EduSohoLoadingButton btnPhoneReg;
    private Button btnSendCode;
    private EditText etMail;
    private EditText etMailPass;
    private EduSohoLoadingButton btnMailReg;
    private String mCookie = "";

    private int mClockTime;
    private Timer mTimer;
    private SmsCodeHandler mSmsCodeHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setBackMode(BACK, "注册");
        initView();
    }

    private void initView() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("手机注册").setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("邮箱注册").setContent(R.id.tab2));
        TabWidget tabWidget = tabHost.getTabWidget();

        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(AppUtil.px2sp(this, getResources().getDimension(R.dimen.medium_font_size)));
            tv.setTypeface(null, Typeface.NORMAL);
            tv.setTextColor(this.getResources().getColor(R.color.green_alpha));
            tabWidget.getChildAt(i).setBackgroundResource(R.drawable.register_tab_bg);
        }

        etPhone = (EditText) findViewById(R.id.et_phone);
        etCode = (EditText) findViewById(R.id.et_code);
        etPhonePass = (EditText) findViewById(R.id.et_phone_pass);
        btnSendCode = (Button) findViewById(R.id.btn_send_code);
        btnSendCode.setOnClickListener(mSmsSendClickListener);
        btnPhoneReg = (EduSohoLoadingButton) findViewById(R.id.btn_phone_reg);
        btnPhoneReg.setOnClickListener(mPhoneRegClickListener);
        etMail = (EditText) findViewById(R.id.et_mail);
        etMailPass = (EditText) findViewById(R.id.et_mail_pass);
        btnMailReg = (EduSohoLoadingButton) findViewById(R.id.btn_mail_reg);
        btnMailReg.setOnClickListener(mMailRegClickListener);
        mSmsCodeHandler = new SmsCodeHandler(this);
    }

    public static class SmsCodeHandler extends Handler {
        WeakReference<RegisterActivity> mWeakReference;
        RegisterActivity mActivity;

        public SmsCodeHandler(RegisterActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            mActivity = mWeakReference.get();
            mActivity.btnSendCode.setText(mActivity.mClockTime + "秒后重发");
            mActivity.mClockTime--;
            if (mActivity.mClockTime < 0) {
                mActivity.mTimer.cancel();
                mActivity.mTimer = null;
                mActivity.btnSendCode.setText(mActivity.getResources().getString(R.string.reg_send_code));
                mActivity.btnSendCode.setEnabled(true);
                mActivity.btnSendCode.setBackgroundColor(mActivity.getResources().getColor(R.color.green_alpha));
            }
        }
    }

    View.OnClickListener mSmsSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String phoneNumber = etPhone.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                CommonUtil.longToast(mContext, String.format("请输入%s", "手机号"));
                return;
            }

            RequestUrl requestUrl = app.bindUrl(Const.SMS_SEND, false);

            HashMap<String, String> params = requestUrl.getParams();
            params.put("phoneNumber", String.valueOf(phoneNumber));
            mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        MsgCode result = parseJsonValue(response, new TypeToken<MsgCode>() {
                        });
                        if (result != null && result.code == 200) {
                            btnSendCode.setEnabled(false);
                            btnSendCode.setBackgroundColor(getResources().getColor(R.color.grey_main));
                            mClockTime = 120;
                            mTimer = new Timer();
                            mTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Message message = mSmsCodeHandler.obtainMessage();
                                    message.what = 0;
                                    mSmsCodeHandler.sendMessage(message);

                                }
                            }, 0, 1000);
                            CommonUtil.longToast(mContext, result.msg);

                        } else {
                            CommonUtil.longToast(mContext, response);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "phone reg error");
                    }
                }
            }, null);
        }
    };

    View.OnClickListener mPhoneRegClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestUrl url = app.bindUrl(Const.REGIST, false);
            HashMap<String, String> params = url.getParams();
            params.put("registeredWay","android");
            String strPhone = etPhone.getText().toString().trim();
            if (TextUtils.isEmpty(strPhone)) {
                CommonUtil.longToast(mContext, String.format("请输入手机号"));
                return;
            }
            params.put("phone", strPhone);

            String strPass = etPhonePass.getText().toString();
            if (TextUtils.isEmpty(strPass)) {
                CommonUtil.longToast(mContext, "请输入密码");
                return;
            } else if (strPass.length() > 20) {
                CommonUtil.longToast(mContext, "密码的长度必须小于或等于20");
                return;
            }
            params.put("password", strPass);

            String strCode = etCode.getText().toString().trim();
            if (TextUtils.isEmpty(strCode)) {
                CommonUtil.longToast(mContext, "请输入验证码");
                return;
            } else {
                params.put("smsCode", strCode);
            }

            HashMap<String, String> headers = url.getHeads();
            if (!TextUtils.isEmpty(mCookie)) {
                headers.put("Cookie", mCookie);
            }

            btnPhoneReg.setLoadingState();

            mActivity.ajaxPost(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        UserResult userResult = mActivity.parseJsonValue(
                                response, new TypeToken<UserResult>() {
                                });
                        if (userResult != null && userResult.user != null) {
                            app.saveToken(userResult);
                            btnPhoneReg.setSuccessState();
                            btnPhoneReg.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    app.mEngine.runNormalPlugin("DefaultPageActivity", mContext, null, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    app.sendMessage(Const.LOGIN_SUCCESS, null);
                                }
                            }, 500);
                        } else {
                            btnPhoneReg.setInitState();
                            if (!TextUtils.isEmpty(response)) {
                                CommonUtil.longToast(mContext, response);
                            } else {
                                CommonUtil.longToast(mContext, getResources().getString(R.string.user_not_exist));
                            }
                        }
                    } catch (Exception e) {
                        btnPhoneReg.setInitState();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + new String(error.networkResponse.data));
                    btnPhoneReg.setInitState();
                    CommonUtil.longToast(mContext, getResources().getString(R.string.request_fail_text));
                }
            });
        }
    };

    View.OnClickListener mMailRegClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestUrl url = app.bindUrl(Const.REGIST, false);
            HashMap<String, String> params = url.getParams();
            params.put("registeredWay","android");
            String strMail = etMail.getText().toString().trim();
            if (TextUtils.isEmpty(strMail)) {
                CommonUtil.longToast(mContext, "请输入邮箱地址");
                return;
            }
            params.put("email", strMail);

            String strPass = etMailPass.getText().toString();
            if (TextUtils.isEmpty(strPass)) {
                CommonUtil.longToast(mContext, "请输入密码");
                return;
            } else if (strPass.length() > 20) {
                CommonUtil.longToast(mContext, "密码的长度必须小于或等于20");
                return;
            }
            if (strPass.length() < 5) {
                CommonUtil.longToast(mContext, "密码不能小于5位");
                return;
            }
            params.put("password", strPass);

            btnMailReg.setLoadingState();

            mActivity.ajaxPost(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        UserResult userResult = mActivity.parseJsonValue(
                                response, new TypeToken<UserResult>() {
                                });
                        if (userResult != null && userResult.user != null) {
                            btnMailReg.setInitState();
                            app.saveToken(userResult);
                            btnMailReg.setSuccessState();
                            btnMailReg.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    app.mEngine.runNormalPlugin("DefaultPageActivity", mContext, null, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    app.sendMessage(Const.LOGIN_SUCCESS, null);
                                }
                            }, 500);
                        } else {
                            btnMailReg.setInitState();
                            if (!TextUtils.isEmpty(response)) {
                                CommonUtil.longToast(mContext, response);
                            } else {
                                CommonUtil.longToast(mContext, getResources().getString(R.string.user_not_exist));
                            }
                        }
                    } catch (Exception e) {
                        btnMailReg.setInitState();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    btnMailReg.setInitState();
                    CommonUtil.longToast(mContext, getResources().getString(R.string.request_fail_text));
                }
            });
        }
    };

}