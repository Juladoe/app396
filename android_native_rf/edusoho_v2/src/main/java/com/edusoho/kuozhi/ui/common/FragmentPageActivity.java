package com.edusoho.kuozhi.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.Const;

public class FragmentPageActivity extends ActionBarBaseActivity {

    public static final String FRAGMENT = "fragment";
    private String mFragment;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_page_layout);
        initView();
    }

    private void initView()
    {
        Intent data = getIntent();
        if (data != null) {
            mFragment = data.getStringExtra(FRAGMENT);
            mTitle = data.getStringExtra(Const.ACTIONBAT_TITLE);
        }

        if (mTitle == null) {
            mTitle = BACK;
        }

        setBackMode(BACK, mTitle);
        loadFragment(mFragment, data != null ? data.getExtras() : null);
    }

    private void loadFragment(String fragmentName, Bundle bundle)
    {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = app.mEngine.runPluginWithFragmentByBundle(
                fragmentName, mActivity, bundle);
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.commit();
    }
}
