package com.edusoho.kuozhi;

import android.os.Bundle;
import android.view.View;

import com.edusoho.kuozhi.ui.DefaultPageActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.AboutFragment;

/**
 * Created by howzhi on 15/1/22.
 */
public class CustomDefaultPageActivity extends DefaultPageActivity {

    public void customBtnClick(View view) {
        String tag = view.getTag().toString();
        if (tag == null || tag.isEmpty()) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(AboutFragment.URL, tag);
        bundle.putString(FragmentPageActivity.FRAGMENT, "AboutFragment");
        mActivity.app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
    }
}