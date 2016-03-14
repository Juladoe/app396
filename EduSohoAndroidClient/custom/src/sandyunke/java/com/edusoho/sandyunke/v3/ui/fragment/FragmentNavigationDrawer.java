package com.edusoho.sandyunke.v3.ui.fragment;

import android.content.Intent;
import android.view.View;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.WebViewActivity;
import com.edusoho.kuozhi.v3.ui.fragment.AboutFragment;

import java.net.URLEncoder;

/**
 * Created by JesseHuang on 15/4/27.
 */
public class FragmentNavigationDrawer extends com.edusoho.kuozhi.v3.ui.fragment.FragmentNavigationDrawer {

    @Override
    protected void handleDrawViewClick(View view, int position) {
        if (position == 5) {
            final String url = String.format("%s/mobile/es/discuz/sync?redirect_url=%s&token=%s", app.host, URLEncoder.encode("http://bbs.3dsjw.com"), app.token);

            mActivity.app.mEngine.runNormalPlugin("FragmentPageActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(FragmentPageActivity.FRAGMENT, "AboutFragment");
                    startIntent.putExtra(AboutFragment.URL, url);
                }
            });
            return;
        }
        super.handleDrawViewClick(view, position);
    }

    @Override
    protected int[] getRadioIds() {
        int radio5 = mContext.getResources().getIdentifier(
                "radio5", "id", mContext.getPackageName());
        return new int[]{
                com.edusoho.kuozhi.R.id.radio0,
                com.edusoho.kuozhi.R.id.radio1,
                com.edusoho.kuozhi.R.id.radio2,
                com.edusoho.kuozhi.R.id.radio3,
                com.edusoho.kuozhi.R.id.radio4,
                radio5,

        };
    }
}
