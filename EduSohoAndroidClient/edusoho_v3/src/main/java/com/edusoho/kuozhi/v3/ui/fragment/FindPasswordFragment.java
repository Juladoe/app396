package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.register.FindPasswordSmsCode;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.http.ModelDecor;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.ForgetPasswordActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.InputUtils;
import com.edusoho.kuozhi.v3.util.ToastUtil;
import com.edusoho.kuozhi.v3.util.Validator;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by JesseHuang on 2016/11/25.
 */

public class FindPasswordFragment extends BaseFragment {

    public static final String SMS_TOKEN = "sms_token";
    private Button btnNext;
    private EditText etPhoneOrMail;
    private ImageView ivErase;

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
        ivErase = (ImageView) view.findViewById(R.id.iv_erase);
        btnNext.setOnClickListener(getNextClickListener());
        ivErase.setOnClickListener(getEraseInfoClickListener());
        etPhoneOrMail.requestFocus();
        InputUtils.showKeyBoard(etPhoneOrMail, mContext);
        InputUtils.addTextChangedListener(etPhoneOrMail, new NormalCallback<Editable>() {
            @Override
            public void success(Editable editable) {
                if (editable.length() == 0) {
                    ivErase.setVisibility(View.INVISIBLE);
                } else {
                    ivErase.setVisibility(View.VISIBLE);
                }
            }
        });
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
                    final ForgetPasswordActivity forgetPasswordActivity = (ForgetPasswordActivity) getActivity();
                    final Bundle bundle = new Bundle();
                    if (Validator.isMail(etPhoneOrMail.getText().toString().trim())) {
                        bundle.putString(ForgetPasswordActivity.RESET_INFO, getResetInfo());
                        forgetPasswordActivity.switchFragment("FindPasswordByMailFragment", bundle);
                    } else if (Validator.isPhone(etPhoneOrMail.getText().toString().trim())) {
                        sendSmsToPhone(new NormalCallback<FindPasswordSmsCode>() {
                            @Override
                            public void success(FindPasswordSmsCode result) {
                                if (result != null) {
                                    ToastUtils.show(mContext, getString(R.string.sms_code_success), Toast.LENGTH_LONG);
                                    bundle.putString(ForgetPasswordActivity.RESET_INFO, getResetInfo());
                                    bundle.putString(SMS_TOKEN, result.smsToken);
                                    forgetPasswordActivity.switchFragment("FindPasswordByPhoneFragment", bundle);
                                }
                            }
                        });
                    } else {
                        ToastUtil.getInstance(mContext).makeText(getString(R.string.phone_or_mail_format_error), Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
    }

    private View.OnClickListener getEraseInfoClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPhoneOrMail.setText("");
            }
        };
    }

    private void sendSmsToPhone(final NormalCallback<FindPasswordSmsCode> callback) {
        RequestUrl requestUrl = app.bindNewUrl(Const.SMS_CODES, false);
        Map<String, String> params = requestUrl.getParams();
        params.put("type", "sms_change_password");
        params.put("mobile", etPhoneOrMail.getText().toString().trim());
        app.postUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                FindPasswordSmsCode smsCode = ModelDecor.getInstance().decor(response, new TypeToken<FindPasswordSmsCode>() {
                });
                callback.success(smsCode);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.success(null);
            }
        });
    }

    public String getResetInfo() {
        return etPhoneOrMail.getText().toString().trim();
    }
}
