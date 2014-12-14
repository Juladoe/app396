package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.ui.Message.MessageTabActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.annotations.ViewUtil;
import com.edusoho.kuozhi.view.EduUpdateView;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;

import java.util.Set;

/**
 * Created by howzhi on 14-8-25.
 */
public class MoreSettingFragment extends BaseFragment {

    @ViewUtil("more_setting_logout_btn")
    private View mLogoutBtn;

    @ViewUtil("more_setting_set")
    private EduUpdateView mSettingBtn;

    @ViewUtil("more_setting_about")
    private View mSettingAbout;

    @ViewUtil("more_setting_qrsearch")
    private View mSearchBtn;

    @ViewUtil("more_setting_message")
    private View mMessageBtn;

    @ViewUtil("more_setting_offline")
    private View mOffLineBtn;

    @Override
    public String getTitle() {
        return "更多";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContainerView(R.layout.more_setting);
        super.onCreate(savedInstanceState);
    }

    protected void showSchoolAbout() {
        final String url = app.schoolHost + Const.ABOUT;
        app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(AboutFragment.URL, url);
                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "AboutFragment");
                startIntent.putExtra(Const.ACTIONBAT_TITLE, getResources().getString(R.string.school_about));
            }
        });
    }

    private void registNotify() {
        mSettingBtn.addNotifyType("app_update");
    }

    private void bindListener()
    {
        mOffLineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.ACTIONBAT_TITLE, "视频下载");
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "LessonDownloadedFragment");
                    }
                });
            }
        });

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

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupDialog.createMuilt(
                        mActivity,
                        "退出提示",
                        "是否退出登录?",
                        new PopupDialog.PopupClickListener() {
                            @Override
                            public void onClick(int button) {
                                if (button == PopupDialog.OK) {
                                    logout();
                                }
                            }
                        }).show();
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.mEngine.runNormalPlugin("QrSchoolActivity", mActivity, null);
            }
        });

//        mMessageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (app.loginUser == null) {
//                    LoginActivity.start(mActivity);
//                    return;
//                }
//                app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
//                    @Override
//                    public void setIntentDate(Intent startIntent) {
//                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "MessageFragment");
//                        startIntent.putExtra(Const.ACTIONBAT_TITLE, "通知");
//                    }
//                });
//            }
//        });

        mMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (app.loginUser == null) {
                    LoginActivity.start(mActivity);
                    return;
                }
                PluginRunCallback callback = new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(MessageTabActivity.FRAGMENT_DATA, new Bundle());
                        startIntent.putExtra(MessageTabActivity.FRAGMENT_NAME, "MessageFragment");
                        startIntent.putExtra(MessageTabActivity.FRAGMENT_LIST, Const.MESSAGE_FRAGMENT_LIST);
                        startIntent.putExtra(MessageTabActivity.TAB_TITLES, Const.MESSAGE_TAB_TITLE);
                        startIntent.putExtra(Const.ACTIONBAT_TITLE, "通知");
                    }
                };

                app.mEngine.runNormalPlugin("MessageTabActivity", mActivity, callback);
            }
        });
    }

    @Override
    protected void initView(View view) {
        viewInject(view);
        registNotify();

        bindListener();
    }

    private void checkNotify() {
        Set<String> notifys = app.getNotifys();
        if (notifys.isEmpty()) {
            mSettingBtn.setUpdate(false);
            return;
        }
        for (String type : notifys) {
            if (mSettingBtn == null) {
                continue;
            }
            if (mSettingBtn.hasNotify(type)) {
                mSettingBtn.setUpdate(true);
                continue;
            }
        }
    }

    private void logout() {
        showProgress(true);
        RequestUrl url = app.bindUrl(Const.LOGOUT, true);
        mActivity.ajaxPost(url, new ResultCallback() {
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
        Log.d(null, "MoreSettingFragment->onResume");
        checkNotify();
        if (app.loginUser != null) {
            mLogoutBtn.setVisibility(View.VISIBLE);
        }
    }
}
