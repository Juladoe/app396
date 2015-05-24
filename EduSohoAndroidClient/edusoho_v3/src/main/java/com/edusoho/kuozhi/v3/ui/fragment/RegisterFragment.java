package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

/**
 * Created by JesseHuang on 15/5/23.
 */
public class RegisterFragment extends BaseFragment {
    public static final String TAG = "RegisterFragment";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_register);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
    }
}
