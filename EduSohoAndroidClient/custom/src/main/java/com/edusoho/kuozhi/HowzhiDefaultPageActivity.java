package com.edusoho.kuozhi;

import android.content.Intent;
import android.view.View;

import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.ui.DefaultPageActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.AboutFragment;
import com.edusoho.kuozhi.util.Const;

/**
 * Created by howzhi on 15/1/22.
 */
public class HowzhiDefaultPageActivity extends DefaultPageActivity {

    @Override
    protected void initNavSelected() {
        if (app.token == null || "".equals(app.token)) {
            mSelectBtn = R.id.nav_schoolroom_btn;
        } else {
            mSelectBtn = R.id.nav_me_btn;
        }

        selectNavBtn(mSelectBtn);
    }

    public void helpClick(View v) {
        app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "AboutFragment");
                startIntent.putExtra(Const.ACTIONBAR_TITLE, "好知帮助");
                startIntent.putExtra(AboutFragment.URL, app.host + "/page/mobilehelp");
            }
        });
    }
}