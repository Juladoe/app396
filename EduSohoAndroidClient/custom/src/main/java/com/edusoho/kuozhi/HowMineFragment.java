package com.edusoho.kuozhi;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.AboutFragment;
import com.edusoho.kuozhi.ui.fragment.MineFragment;
import com.edusoho.kuozhi.util.Const;

/**
 * Created by howzhi on 15/7/12.
 */
public class HowMineFragment extends MineFragment {

    private RelativeLayout mHelpBtn;

    @Override
    protected void initView(View view) {
        super.initView(view);

        mHelpBtn = (RelativeLayout) view.findViewById(R.id.my_help);
        mHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "AboutFragment");
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, "好知帮助");
                        startIntent.putExtra(AboutFragment.URL, app.host + "/page/mobilehelp");
                    }
                });
            }
        });
    }
}
