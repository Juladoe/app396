package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.ForgetPasswordActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.ToastUtil;
import com.edusoho.kuozhi.v3.util.Validator;

/**
 * Created by JesseHuang on 2016/11/25.
 */

public class FindPasswordFragment extends BaseFragment {

    private Button btnNext;
    private EditText etPhoneOrMail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_find_password);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        btnNext = (Button) view.findViewById(R.id.btn_next);
        etPhoneOrMail = (EditText) view.findViewById(R.id.et_phone_or_mail);
        btnNext.setOnClickListener(getNextClickListener());
    }

    private View.OnClickListener getNextClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(etPhoneOrMail.getText().toString().trim())) {
                    ToastUtil.getInstance(mContext).makeText(getString(R.string.find_password_text_not_null), Toast.LENGTH_LONG).show();
                    return;
                }
                if (getActivity() != null && getActivity() instanceof ForgetPasswordActivity) {
                    ForgetPasswordActivity forgetPasswordActivity = (ForgetPasswordActivity) getActivity();
                    Bundle bundle = new Bundle();

                    if (Validator.isMail(etPhoneOrMail.getText().toString().trim())) {
                        bundle.putString(ForgetPasswordActivity.RESET_INFO, getResetInfo());
                        forgetPasswordActivity.switchFragment("FindPasswordByMailFragment", bundle);
                    } else if (Validator.isPhone(etPhoneOrMail.getText().toString().trim())) {
                        bundle.putString(ForgetPasswordActivity.RESET_INFO, getResetInfo());
                        forgetPasswordActivity.switchFragment("FindPasswordByPhoneFragment", bundle);
                    } else {
                        ToastUtil.getInstance(mContext).makeText(getString(R.string.phone_or_mail_format_error), Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
    }

    public String getResetInfo() {
        return etPhoneOrMail.getText().toString().trim();
    }
}
