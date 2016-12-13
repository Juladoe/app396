package com.edusoho.kuozhi.v3.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.register.MsgCode;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.InputUtils;
import com.edusoho.kuozhi.v3.util.Validator;
import com.google.gson.reflect.TypeToken;

import java.util.Map;


/**
 * Created by DF on 2016/11/28.
 */

public class RegisterActivity extends ActionBarBaseActivity {
    private EditText etAccount;
    private TextView tvNext;
    private ImageView ivBack;
    private TextView tvInfo;
    private ImageView ivClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        hideActionBar();
        initView();

    }

    private void initView() {
        tvInfo = (TextView) findViewById(R.id.tv_info);
        etAccount = (EditText) findViewById(R.id.et_phone_num);
        etAccount.addTextChangedListener(mTextChangeListener);
        tvNext = (TextView) findViewById(R.id.tv_next);
        tvNext.setOnClickListener(nextClickListener);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(mBackClickListener);
        ivClear = (ImageView) findViewById(R.id.iv_clear_phone);
        ivClear.setOnClickListener(mClearClickListener);

        InputUtils.showKeyBoard(etAccount, mContext);
    }

    TextWatcher mTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                tvNext.setAlpha(1f);
                ivClear.setVisibility(View.VISIBLE);
            } else {
                tvNext.setAlpha(0.6f);
                ivClear.setVisibility(View.INVISIBLE);
            }
        }
    };

    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RegisterActivity.this.finish();
        }
    };

    View.OnClickListener mClearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            etAccount.setText("");
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

    private View.OnClickListener nextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (etAccount.length() == 0) {
                return;
            }
            if ("".equals(etAccount.getText().toString().trim())) {
                CommonUtil.longToast(mContext, getString(R.string.complete_phone_empty));
                return;
            }
            final String phoneNum = etAccount.getText().toString().trim();
            if (Validator.isPhone(phoneNum)) {
                RequestUrl requestUrl = app.bindUrl(Const.SMS_SEND, false);
                Map<String, String> params = requestUrl.getParams();
                params.put("phoneNumber", String.valueOf(phoneNum));
                mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MsgCode result = parseJsonValue(response, new TypeToken<MsgCode>() {
                            });
                            if (result != null && result.code == 200) {
                                Intent registerIntent = new Intent(mContext, RegisterConfirmActivity.class);
                                registerIntent.putExtra("num", phoneNum);
                                startActivity(registerIntent);
                            } else {
                                if (response.equals(getString(R.string.register_hint))) {
                                    showDialog();
                                } else {
                                    CommonUtil.longToast(mContext, response);
                                }
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "phone reg error");
                        }
                    }
                }, null);
            } else {
                CommonUtil.longToast(mContext, getString(R.string.register_error));
            }
        }
    };

    /**
     * 弹窗提示已注册
     */
    private void showDialog() {
        new AlertDialog.Builder(RegisterActivity.this)
                .setTitle(R.string.notification)
                .setMessage(R.string.register_hint)
                .setPositiveButton(R.string.register_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RegisterActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.register_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommonUtil.longToast(mContext, getString(R.string.register_modify_phone));
                    }
                }).show();

    }

}

