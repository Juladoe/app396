package com.edusoho.kuozhi.v3.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

/**
 * Created by JesseHuang on 2016/11/25.
 */

public class ForgetPasswordActivity extends ActionBarBaseActivity {

    private ImageView ivBack;
    private String mCurrentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initView();
        showFragment("FindPasswordFragment");
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(getBackClickListener());
    }

    public void showFragment(String fragmentTag) {
        Fragment fragment;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragment = mFragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            fragmentTransaction.show(fragment);
        } else {
            fragment = app.mEngine.runPluginWithFragment(fragmentTag, mActivity, new PluginFragmentCallback() {
                @Override
                public void setArguments(Bundle bundle) {

                }
            });
            fragmentTransaction.add(R.id.fl_container, fragment, fragmentTag);
        }
        fragmentTransaction.commit();
        mCurrentTag = fragmentTag;
    }

    public void hideFragment(String fragmentTag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            fragmentTransaction.hide(fragment);
        }
        fragmentTransaction.commit();
    }

    public void switchFragment(String fragmentTag) {
        if (mCurrentTag != null) {
            hideFragment(mCurrentTag);
            showFragment(fragmentTag);
        }
    }

    private View.OnClickListener getBackClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }
}
