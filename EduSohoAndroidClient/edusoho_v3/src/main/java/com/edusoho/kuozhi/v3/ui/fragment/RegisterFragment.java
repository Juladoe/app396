package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by JesseHuang on 15/5/23.
 */
public class RegisterFragment extends BaseFragment {
    public static final String TAG = "RegisterFragment";
    private EditText etUsername;
    private EditText etPassword;
    private EditText etCode;
    private Button btnRegister;
    private Button btnEmailReg;
    private Button btnPhoneReg;
    private Button btnSmsSend;
    private View vCode;
    private boolean mIsPhoneReg;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_register);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        etUsername = (EditText) mContainerView.findViewById(R.id.et_username);
        etPassword = (EditText) mContainerView.findViewById(R.id.et_password);
        etCode = (EditText) mContainerView.findViewById(R.id.et_code);
        btnEmailReg = (Button) mContainerView.findViewById(R.id.btn_mail_reg);
        btnEmailReg.setOnClickListener(mPhoneRegTypeClickListener);
        btnPhoneReg = (Button) mContainerView.findViewById(R.id.btn_phone_reg);
        btnPhoneReg.setOnClickListener(mPhoneRegTypeClickListener);
        btnRegister = (Button) mContainerView.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(mRegisterClickListener);
        vCode = mContainerView.findViewById(R.id.v_code);
        btnSmsSend = (Button) mContainerView.findViewById(R.id.btn_send_code);
        btnSmsSend.setOnClickListener(mSmsSendClickListener);

        initRegStatus(true, btnPhoneReg, btnEmailReg, getString(R.string.reg_phone_hint));
    }

    void initRegStatus(boolean regStatus, Button selectButton, Button denyButton, String usernameHint) {
        selectButton.setTextColor(getResources().getColor(android.R.color.white));
        selectButton.setBackgroundResource(R.drawable.reg_btn_press);
        denyButton.setTextColor(getResources().getColor(R.color.base_black_26));
        denyButton.setBackgroundColor(getResources().getColor(android.R.color.white));
        etUsername.setHint(usernameHint);
        mIsPhoneReg = regStatus;
    }

    View.OnClickListener mPhoneRegTypeClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_phone_reg) {
                initRegStatus(true, btnPhoneReg, btnEmailReg, getString(R.string.reg_phone_hint));
                vCode.setVisibility(View.VISIBLE);
            } else {
                initRegStatus(false, btnEmailReg, btnPhoneReg, getString(R.string.reg_mail_hint));
                vCode.setVisibility(View.INVISIBLE);
            }
        }
    };

    View.OnClickListener mRegisterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestUrl url = app.bindUrl(Const.REGIST, false);
            HashMap<String, String> params = url.getParams();

            String username = etUsername.getText().toString().trim();
            if (TextUtils.isEmpty(username)) {
                CommonUtil.longToast(mContext, String.format("请输入%s", mIsPhoneReg ? "手机号" : "邮箱地址"));
                return;
            }
            params.put(mIsPhoneReg ? "phone" : "email", username);

            String password = etPassword.getText().toString();
            if (TextUtils.isEmpty(password)) {
                CommonUtil.longToast(mContext, "请输入密码");
                return;
            }
            params.put("password", password);

            String code = etCode.getText().toString().trim();
            if (mIsPhoneReg) {
                if (TextUtils.isEmpty(code)) {
                    CommonUtil.longToast(mContext, "请输入验证码");
                    return;
                } else {
                    params.put("smsCode", code);
                }
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

    View.OnClickListener mSmsSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestUrl requestUrl = app.bindUrl(Const.SMS_SEND, false);
            String phoneNumber = etUsername.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                CommonUtil.longToast(mContext, String.format("请输入%s", "手机号"));
                return;
            }
            HashMap<String, String> params = requestUrl.getParams();
            params.put("phoneNumber", String.valueOf(phoneNumber));
            mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, null);
        }
    };
}
