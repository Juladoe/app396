package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.ui.common.AboutActivity;
import com.edusoho.kuozhi.util.Const;

/**
 * Created by howzhi on 14-8-25.
 */
public class MoreSettingFragment extends BaseFragment {

    private View mLogoutBtn;
    private AQuery mAQuery;

    @Override
    public String getTitle() {
        return "更多";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContainerView(R.layout.more_setting);
        super.onCreate(savedInstanceState);
    }

    protected void showSchoolAbout()
    {
        final String url = app.schoolHost + Const.ABOUT;
        app.mEngine.runNormalPlugin("AboutActivity", mActivity, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(AboutActivity.URL, url);
                startIntent.putExtra(AboutActivity.TITLE, "关于网校");
            }
        });
    }

    @Override
    protected void initView(View view) {
        mAQuery = new AQuery(view);

        mAQuery.id(R.id.more_setting_about).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSchoolAbout();
            }
        });

        mLogoutBtn = view.findViewById(R.id.more_setting_logout_btn);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (app.loginUser != null) {
            mLogoutBtn.setVisibility(View.VISIBLE);
        }
    }
}
