package com.edusoho.kuozhi.v3.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.fragment.LoginFragment;
import com.edusoho.kuozhi.v3.ui.fragment.RegisterFragment;

import java.util.List;

/**
 * Created by JesseHuang on 15/5/22.
 */
public class LoginActivity extends ActionBarBaseActivity {

    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_REGISTER = 2;
    public static final int OK = 1003;
    public static final String FRAGMENT_TYPE = "fragment_type";
    private int mFragmentType;
    private static boolean isRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        Intent data = getIntent();
        if (data != null) {
            mFragmentType = data.getIntExtra(FRAGMENT_TYPE, TYPE_LOGIN);
        }
        showFragment(mFragmentType == TYPE_LOGIN ? LoginFragment.TAG : RegisterFragment.TAG);
    }

    public static void startLogin(Activity activity) {
        synchronized (activity) {
            if (isRun) {
                return;
            }
            Intent intent = new Intent();
            intent.setClass(activity, LoginActivity.class);
            activity.startActivityForResult(intent, TYPE_LOGIN);
        }
    }

    public void showFragment(String tag) {
        setBackMode(BACK, mFragmentType == TYPE_LOGIN ? "登录" : "注册");
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        BaseFragment fragment = (BaseFragment) app.mEngine.runPluginWithFragment(tag, mActivity, null);
        fragmentTransaction.replace(R.id.login_container, fragment);
        List<Fragment> fragmentList = mFragmentManager.getFragments();
        if (fragmentList != null && !fragmentList.isEmpty()) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;
    }
}
