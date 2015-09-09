package com.edusoho.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

/**
 * Created by howzhi on 15/8/17.
 */
public class FragmentTestActivity extends ActionBarBaseActivity {

    public Fragment loadFragment(String fragmentName, Bundle bundle) {
        Fragment fragment = null;
        try {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragment = Fragment.instantiate(mActivity, fragmentName);
            fragment.setArguments(bundle);
            fragmentTransaction.replace(android.R.id.content, fragment);
            fragmentTransaction.commit();
        } catch (Exception ex) {
            Log.d("FragmentPageActivity", ex.toString());
        }

        return fragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getSupportFragmentManager().getFragments().size() > 0) {
            getSupportFragmentManager().getFragments().get(0).onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
