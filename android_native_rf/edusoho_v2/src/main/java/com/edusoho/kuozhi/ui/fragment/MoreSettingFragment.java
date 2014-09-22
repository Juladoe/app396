package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.annotations.ViewUtil;
import com.edusoho.kuozhi.view.dialog.EdusohoMaterialDialog;
import com.edusoho.listener.ResultCallback;

/**
 * Created by howzhi on 14-8-25.
 */
public class MoreSettingFragment extends BaseFragment {

    @ViewUtil("more_setting_logout_btn")
    private View mLogoutBtn;

    @ViewUtil("more_setting_set")
    private View mSettingBtn;

    @ViewUtil("more_setting_about")
    private View mSettingAbout;

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
        app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(AboutFragment.URL, url);
                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "AboutFragment");
                startIntent.putExtra(Const.ACTIONBAT_TITLE, "关于网校");
            }
        });
    }

    @Override
    protected void initView(View view) {
        viewInject(view);
        mSettingAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSchoolAbout();
            }
        });

        mSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "SettingFragment");
                        startIntent.putExtra(Const.ACTIONBAT_TITLE, "设置");
                    }
                });
            }
        });

        mLogoutBtn = view.findViewById(R.id.more_setting_logout_btn);
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EdusohoMaterialDialog.createMuilt(
                        mActivity,
                        "退出提示",
                        "是否退出登录?",
                        new EdusohoMaterialDialog.PopupClickListener() {
                            @Override
                            public void onClick(int button) {
                                if (button == EdusohoMaterialDialog.OK) {
                                    logout();
                                }
                            }
                }).show();
            }
        });
    }

    private void logout()
    {
        showProgress(true);
        RequestUrl url = app.bindUrl(Const.LOGOUT, true);
        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                showProgress(false);
                app.removeToken();
                mLogoutBtn.setVisibility(View.GONE);
                app.sendMsgToTarget(MyInfoFragment.LOGOUT, null, MyInfoFragment.class);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (app.loginUser != null) {
            mLogoutBtn.setVisibility(View.VISIBLE);
        }
    }
}
