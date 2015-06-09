package com.edusoho.kuozhi.v3.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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
    private String mCookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setBackMode(BACK, "注册");
        initViews();
    }

    private void initViews() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("手机注册",
                null).setContent(R.id.tab1));

        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("邮箱注册")
                .setContent(R.id.tab2));
        TabWidget tabWidget = tabHost.getTabWidget();

        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(AppUtil.px2sp(this, getResources().getDimension(R.dimen.large_font_size)));
            tv.setTypeface(null, Typeface.NORMAL);
            tv.setTextColor(this.getResources().getColor(R.color.green_alpha));
            //tabWidget.getChildAt(i).setBackgroundResource(R.drawable.register_tab_bg);
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
    }

    View.OnClickListener mSmsSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestUrl requestUrl = app.bindUrl(Const.SMS_SEND, false);
            String phoneNumber = etPhone.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                CommonUtil.longToast(mContext, String.format("请输入%s", "手机号"));
                return;
            }
            HashMap<String, String> params = requestUrl.getParams();
            params.put("phoneNumber", String.valueOf(phoneNumber));
            mActivity.ajaxPostHandleCookie(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        mCookie = jsonObject.getString("Cookie");
                    } catch (JSONException e) {

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
            params.put("password", strPass);

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

}
