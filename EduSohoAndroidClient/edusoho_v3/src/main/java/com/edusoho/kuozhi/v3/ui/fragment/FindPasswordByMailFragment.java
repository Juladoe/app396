package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

/**
 * Created by JesseHuang on 2016/11/27.
 */

public class FindPasswordByMailFragment extends BaseFragment {

    private Button btnSubmit;
    private EditText etResetPassword;

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
        btnSubmit.setOnClickListener(getSubmitClickListener());
    }

    private View.OnClickListener getSubmitClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }
}
