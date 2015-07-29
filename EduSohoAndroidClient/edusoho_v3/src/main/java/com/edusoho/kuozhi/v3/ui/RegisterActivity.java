package com.edusoho.kuozhi.v3.ui;

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
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.ErrorResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

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
    private Button btnPhoneReg;
    private Button btnSendCode;
    private EditText etMail;
    private EditText etMailPass;
    private Button btnMailReg;
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
            tv.setTextSize(AppUtil.px2sp(this, getResources().getDimension(R.dimen.large_font_size)));
            tv.setTypeface(null, Typeface.NORMAL);
            tv.setTextColor(this.getResources().getColor(R.color.green_alpha));
            tabWidget.getChildAt(i).setBackgroundResource(R.drawable.register_tab_bg);
        }

        etPhone = (EditText) findViewById(R.id.et_phone);
        etCode = (EditText) findViewById(R.id.et_code);
        etPhonePass = (EditText) findViewById(R.id.et_phone_pass);
        btnSendCode = (Button) findViewById(R.id.btn_send_code);
        btnSendCode.setOnClickListener(mSmsSendClickListener);
        btnPhoneReg = (Button) findViewById(R.id.btn_phone_reg);
        btnPhoneReg.setOnClickListener(mPhoneRegClickListener);
        etMail = (EditText) findViewById(R.id.et_mail);
        etMailPass = (EditText) findViewById(R.id.et_mail_pass);
        btnMailReg = (Button) findViewById(R.id.btn_mail_reg);
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
            mActivity.btnSendCode.setText(mActivity.mClockTime + "秒后重新发送");
            mActivity.mClockTime--;
            if (mActivity.mClockTime < 0) {
                mActivity.mTimer.cancel();
                mActivity.mTimer = null;
                mActivity.btnSendCode.setText(mActivity.getResources().getString(R.string.reg_send_code));
                mActivity.btnSendCode.setEnabled(true);
                mActivity.btnSendCode.setBackgroundResource(R.drawable.reg_code_press);
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
                        ErrorResult result = parseJsonValue(response, new TypeToken<ErrorResult>() {
                        });
                        if (result != null && result.error != null) {
                            CommonUtil.longToast(mActivity, result.error.message);
                            return;
                        }

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("code").equals("200")) {
                            btnSendCode.setEnabled(false);
                            btnSendCode.setBackgroundResource(R.drawable.reg_code_disable);
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
                            CommonUtil.longToast(mContext, jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
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

            mActivity.ajaxPostWithLoading(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        UserResult userResult = mActivity.parseJsonValue(
                                response, new TypeToken<UserResult>() {
                                });
                        app.saveToken(userResult);
                        mActivity.finish();
                        app.sendMessage(Const.LOGIN_SUCCESS, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, null, "注册中...");
        }
    };

    View.OnClickListener mMailRegClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestUrl url = app.bindUrl(Const.REGIST, false);
            HashMap<String, String> params = url.getParams();

            String strMail = etMail.getText().toString().trim();
            if (TextUtils.isEmpty(strMail)) {
                CommonUtil.longToast(mContext, String.format("请输入邮箱地址"));
                return;
            }
            params.put("email", strMail);

            String strPass = etMailPass.getText().toString();
            if (TextUtils.isEmpty(strPass)) {
                CommonUtil.longToast(mContext, "请输入密码");
                return;
            }
            if (strPass.length() < 5) {
                CommonUtil.longToast(mContext, "密码不能小于5位");
                return;
            }
            params.put("password", strPass);

            mActivity.ajaxPostWithLoading(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        ErrorResult result = parseJsonValue(response, new TypeToken<ErrorResult>() {
                        });
                        if (result != null && result.error != null) {
                            CommonUtil.longToast(mActivity, result.error.message);
                            return;
                        }

                        UserResult userResult = mActivity.parseJsonValue(
                                response, new TypeToken<UserResult>() {
                                });
                        app.saveToken(userResult);
                        mActivity.finish();
                        app.sendMessage(Const.LOGIN_SUCCESS, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, null, "注册中...");
        }
    };

}
