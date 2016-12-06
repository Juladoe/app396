package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.error.Error;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.http.ModelDecor;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.ForgetPasswordActivity;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.InputUtils;
import com.edusoho.kuozhi.v3.util.ToastUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by JesseHuang on 2016/11/27.
 */

public class FindPasswordByPhoneFragment extends BaseFragment {

    private static final int PHONE_RETRIEVE_TIME = 120;
    public static final String FIND_PASSWORD_USERNAME = "find_password_username";
    private TextView tvPhoneSmsCodeHint;
    private EditText etSmsCode;
    private EditText etResetPassword;
    private TextView tvSubmit;
    private CheckBox cbShowOrHidePassword;
    private TextView tvPhoneCodeTimer;
    private TextView tvRetrievePhoneCode;
    private ImageView ivErasePhoneCode;
    private ImageView ivErasePassword;
    private Timer mTimer;
    private TimerHandler mTimerHandler;
    private String mSmsToken;
    private String mUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_find_password_by_phone);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        mTimer = new Timer();
        mTimerHandler = new TimerHandler(this);
        mTimer.schedule(getTimerTimerTask(), 0, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        tvPhoneSmsCodeHint = (TextView) view.findViewById(R.id.tv_sms_code);
        etSmsCode = (EditText) view.findViewById(R.id.et_phone_code);
        etResetPassword = (EditText) view.findViewById(R.id.et_reset_password);
        cbShowOrHidePassword = (CheckBox) view.findViewById(R.id.cb_show_or_hide_password);
        tvSubmit = (TextView) view.findViewById(R.id.tv_submit);
        tvPhoneCodeTimer = (TextView) view.findViewById(R.id.tv_code_timer);
        tvRetrievePhoneCode = (TextView) view.findViewById(R.id.tv_retrieve_phone_code);
        ivErasePhoneCode = (ImageView) view.findViewById(R.id.iv_erase_phone_code);
        ivErasePassword = (ImageView) view.findViewById(R.id.iv_erase_password);
        ivErasePhoneCode.setOnClickListener(getEraseInfoClickListener());
        ivErasePassword.setOnClickListener(getEraseInfoClickListener());
        tvSubmit.setOnClickListener(getSubmitClickListener());
        cbShowOrHidePassword.setOnCheckedChangeListener(getShowOrHidePasswordChangeListener());
        tvRetrievePhoneCode.setOnClickListener(getRetrievePhoneCodeClickListener());
        etSmsCode.requestFocus();
        InputUtils.showKeyBoard(etSmsCode, mContext);

        InputUtils.addTextChangedListener(etSmsCode, new NormalCallback<Editable>() {
            @Override
            public void success(Editable editable) {
                if (editable.length() == 0) {
                    ivErasePhoneCode.setVisibility(View.INVISIBLE);
                    tvSubmit.setAlpha(0.6f);
                } else {
                    ivErasePhoneCode.setVisibility(View.VISIBLE);
                    tvSubmit.setAlpha(1.0f);
                }
            }
        });

        InputUtils.addTextChangedListener(etResetPassword, new NormalCallback<Editable>() {
            @Override
            public void success(Editable editable) {
                if (editable.length() == 0) {
                    ivErasePassword.setVisibility(View.INVISIBLE);
                    tvSubmit.setAlpha(0.6f);
                } else {
                    ivErasePassword.setVisibility(View.VISIBLE);
                    tvSubmit.setAlpha(1.0f);
                }
            }
        });
    }

    private void initData() {
        if (getArguments() != null && getArguments().getString(ForgetPasswordActivity.RESET_INFO) != null) {
            mUsername = getArguments().getString(ForgetPasswordActivity.RESET_INFO);
            tvPhoneSmsCodeHint.setText(getString(R.string.phone_code_input_hint) + mUsername);
            mSmsToken = getArguments().getString(FindPasswordFragment.SMS_TOKEN);
            Log.d("mSmsToken", "mSmsToken: " + mSmsToken);
        }
    }

    private TimerTask getTimerTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Message msg = mTimerHandler.obtainMessage();
                msg.sendToTarget();
            }
        };
    }

    private View.OnClickListener getSubmitClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etSmsCode.length() == 0 || etResetPassword.length() == 0) {
                    return;
                }
                String smsCode = etSmsCode.getText().toString().trim();
                String resetPassword = etResetPassword.getText().toString().trim();
                if (TextUtils.isEmpty(smsCode)) {
                    ToastUtil.getInstance(mContext).makeText(getString(R.string.sms_code_not_null), Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(resetPassword)) {
                    ToastUtil.getInstance(mContext).makeText(getString(R.string.reset_password_not_null), Toast.LENGTH_LONG).show();
                    return;
                }
                if (resetPassword.length() < 6) {
                    ToastUtil.getInstance(mContext).makeText(getString(R.string.password_more_than_six_digit_number), Toast.LENGTH_LONG).show();
                    return;
                }
                RequestUrl requestUrl = app.bindNewUrl(Const.FIND_PASSWORD, false);
                Map<String, String> params = requestUrl.getParams();
                params.put("password", etResetPassword.getText().toString());
                params.put("sms_code", etSmsCode.getText().toString());
                params.put("sms_token", mSmsToken);
                params.put("type", "sms");
                app.postUrl(requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ToastUtils.show(mContext, R.string.reset_password_success, Toast.LENGTH_LONG);
                        app.mEngine.runNormalPlugin("LoginActivity", mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(LoginActivity.FIND_PASSWORD_ACCOUNT, mUsername);
                            }
                        }, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null && error.networkResponse != null && error.networkResponse.data != null) {
                            String errorResult = new String(error.networkResponse.data);
                            Error errorModel = ModelDecor.getInstance().decor(errorResult, new TypeToken<Error>() {
                            });
                            ToastUtils.show(mContext, errorModel.message, Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        };
    }

    private View.OnClickListener getRetrievePhoneCodeClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvPhoneCodeTimer.getVisibility() != View.VISIBLE) {
                    if (mTimer == null) {
                        mTimer = new Timer();
                    }
                    mTimer.schedule(getTimerTimerTask(), 0, 1000);
                    tvRetrievePhoneCode.setText(getString(R.string.after_retrieve_phone_code));
                    tvRetrievePhoneCode.setTextColor(getResources().getColor(R.color.secondary2_font_color));
                    tvPhoneCodeTimer.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private View.OnClickListener getEraseInfoClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.iv_erase_password) {
                    etResetPassword.setText("");
                } else if (v.getId() == R.id.iv_erase_phone_code) {
                    etSmsCode.setText("");
                }
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener getShowOrHidePasswordChangeListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etResetPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    etResetPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                etResetPassword.setSelection(etResetPassword.getText().toString().length());
            }
        };
    }


    private static class TimerHandler extends Handler {
        private final WeakReference<FindPasswordByPhoneFragment> mFragment;
        int limitTime = PHONE_RETRIEVE_TIME;

        public TimerHandler(FindPasswordByPhoneFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            FindPasswordByPhoneFragment fragment = mFragment.get();
            if (fragment != null) {
                if (limitTime > 0) {
                    limitTime = limitTime - 1;
                    fragment.tvPhoneCodeTimer.setText(limitTime + "s");
                } else {
                    fragment.tvRetrievePhoneCode.setText(fragment.getString(R.string.retrieve_phone_code));
                    fragment.tvRetrievePhoneCode.setTextColor(fragment.getResources().getColor(R.color.secondary2_color));
                    fragment.tvPhoneCodeTimer.setVisibility(View.INVISIBLE);
                    limitTime = PHONE_RETRIEVE_TIME;
                    fragment.mTimer.cancel();
                    fragment.mTimer = null;
                }
            }
        }
    }
}
