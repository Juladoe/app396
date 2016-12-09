package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
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
import com.edusoho.kuozhi.v3.model.base.ApiResponse;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.ForgetPasswordActivity;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.InputUtils;
import com.edusoho.kuozhi.v3.util.ToastUtil;
import com.edusoho.kuozhi.v3.util.Validator;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JesseHuang on 2016/11/27.
 */

public class FindPasswordByMailFragment extends BaseFragment {

    private TextView tvSubmit;
    private EditText etResetPassword;
    private ImageView ivErase;
    private CheckBox cbShowOrHidePassword;
    private String mEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_find_password_by_mail);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        tvSubmit = (TextView) view.findViewById(R.id.tv_submit);
        etResetPassword = (EditText) view.findViewById(R.id.et_reset_password);
        ivErase = (ImageView) view.findViewById(R.id.iv_erase);
        cbShowOrHidePassword = (CheckBox) view.findViewById(R.id.cb_show_or_hide_password);
        tvSubmit.setOnClickListener(getSubmitClickListener());
        ivErase.setOnClickListener(getEraseInfoClickListener());
        cbShowOrHidePassword.setOnCheckedChangeListener(getShowOrHidePasswordChangeListener());
        etResetPassword.requestFocus();
        InputUtils.showKeyBoard(etResetPassword, mContext);
        InputUtils.addTextChangedListener(etResetPassword, new NormalCallback<Editable>() {
            @Override
            public void success(Editable editable) {
                if (editable.length() == 0) {
                    ivErase.setVisibility(View.INVISIBLE);
                    tvSubmit.setAlpha(0.6f);
                } else {
                    ivErase.setVisibility(View.VISIBLE);
                    tvSubmit.setAlpha(1.0f);
                }
            }
        });
    }

    private void initData() {
        if (getArguments() != null && getArguments().getString(ForgetPasswordActivity.RESET_INFO) != null) {
            mEmail = getArguments().getString(ForgetPasswordActivity.RESET_INFO);
        }
    }

    private View.OnClickListener getSubmitClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etResetPassword.length() == 0) {
                    return;
                }
                RequestUrl requestUrl = app.bindNewUrl(Const.EMAILS, false);
                Map<String, String> map = requestUrl.getParams();
                map.put("password", etResetPassword.getText().toString());
                map.put("email", mEmail);
                app.postUrl(requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ApiResponse<Error> error = ModelDecor.getInstance().decor(response, new TypeToken<ApiResponse<Error>>() {
                        });
                        if (error.error != null && error.error.code != null && error.error.code.equals("500")) {
                            ToastUtil.getInstance(mContext).makeText(error.error.message, Toast.LENGTH_LONG).show();
                            return;
                        }
                        new AlertDialog.Builder(getActivity()).setMessage("请前往该邮箱验证信息，验证成功即可登录").
                                setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        app.mEngine.runNormalPlugin("LoginActivity", mContext, new PluginRunCallback() {
                                            @Override
                                            public void setIntentDate(Intent startIntent) {
                                                startIntent.putExtra(LoginActivity.FIND_PASSWORD_ACCOUNT, mEmail);
                                            }
                                        }, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    }
                                }).setCancelable(false).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

            }
        };
    }

    private View.OnClickListener getEraseInfoClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etResetPassword.setText("");
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
}
