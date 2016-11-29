package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.ForgetPasswordActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.ToastUtil;

/**
 * Created by JesseHuang on 2016/11/27.
 */

public class FindPasswordByPhoneFragment extends BaseFragment {


    private TextView tvPhoneSmsCodeHint;
    private EditText etSmsCode;
    private EditText etResetPassword;
    private Button btnSubmit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_find_password_by_phone);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        tvPhoneSmsCodeHint = (TextView) view.findViewById(R.id.tv_sms_code);
        etSmsCode = (EditText) view.findViewById(R.id.et_sms_code);
        etResetPassword = (EditText) view.findViewById(R.id.et_reset_password);
        btnSubmit = (Button) view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(getSubmitClickListener());
    }

    private void initData() {
        if (getArguments() != null && getArguments().getString(ForgetPasswordActivity.RESET_INFO) != null) {
            tvPhoneSmsCodeHint.setText(getString(R.string.phone_code_input_hint) + getArguments().getString(ForgetPasswordActivity.RESET_INFO));
        }
    }

    private View.OnClickListener getSubmitClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        };
    }
}
