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

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.ForgetPasswordActivity;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.InputUtils;

/**
 * Created by JesseHuang on 2016/11/27.
 */

public class FindPasswordByMailFragment extends BaseFragment {

    private Button btnSubmit;
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
        btnSubmit = (Button) view.findViewById(R.id.btn_submit);
        etResetPassword = (EditText) view.findViewById(R.id.et_reset_password);
        ivErase = (ImageView) view.findViewById(R.id.iv_erase);
        cbShowOrHidePassword = (CheckBox) view.findViewById(R.id.cb_show_or_hide_password);
        btnSubmit.setOnClickListener(getSubmitClickListener());
        ivErase.setOnClickListener(getEraseInfoClickListener());
        cbShowOrHidePassword.setOnCheckedChangeListener(getShowOrHidePasswordChangeListener());
        etResetPassword.requestFocus();
        InputUtils.showKeyBoard(etResetPassword, mContext);
        InputUtils.addTextChangedListener(etResetPassword, new NormalCallback<Editable>() {
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

    private void initData() {
        if (getArguments() != null && getArguments().getString(ForgetPasswordActivity.RESET_INFO) != null) {
            mEmail = getArguments().getString(ForgetPasswordActivity.RESET_INFO);
        }
    }

    private View.OnClickListener getSubmitClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
