package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_find_password_by_mail);
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

    private View.OnClickListener getSubmitClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
