package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.ForgetPasswordActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

/**
 * Created by JesseHuang on 2016/11/25.
 */

public class FindPasswordFragment extends BaseFragment {

    private Button btnNext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_find_password);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        btnNext = (Button) view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(getNextClickListener());
    }

    private View.OnClickListener getNextClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && getActivity() instanceof ForgetPasswordActivity) {
                    ForgetPasswordActivity forgetPasswordActivity = (ForgetPasswordActivity) getActivity();
                    forgetPasswordActivity.switchFragment("FindPasswordByPhoneFragment");
                }
            }
        };
    }
}
