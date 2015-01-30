package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.Service.M3U8DownService;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.AppUpdateInfo;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.annotations.ViewUtil;
import com.edusoho.kuozhi.view.EduUpdateView;
import com.edusoho.kuozhi.view.dialog.ExitCoursePopupDialog;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.edusoho.listener.StatusCallback;

import java.io.File;
import java.util.Set;

/**
 * Created by howzhi on 14-9-21.
 */
public class SettingFragment extends BaseFragment {

    @ViewUtil("setting_app_btn")
    private View mAppView;

    @ViewUtil("setting_clear_btn")
    private View mClearCacheView;

    @ViewUtil("setting_offline_set_btn")
    private View mOfflineSetBtn;

    @ViewUtil("setting_cache_view")
    private TextView mCacheView;

    @ViewUtil("setting_offline_set_value")
    private TextView mOfflineSetView;

    @ViewUtil("setting_load_progress")
    private ProgressBar mLoadProgressBar;

    @ViewUtil("setting_check_version")
    private EduUpdateView mCheckView;

    @ViewUtil("setting_logout_btn")
    private Button mLogoutBtn;

    @ViewUtil("more_setting_about")
    private View mSettingAbout;

    @Override
    public String getTitle() {
        return "设置";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.setting_fragment);
    }

    private void registNotify() {
        mCheckView.addNotifyType("app_update");
    }

    private void checkNotify() {
        Set<String> notifys = app.getNotifys();
        for (String type : notifys) {
            if (mCheckView == null) {
                continue;
            }
            if (mCheckView.hasNotify(type)) {
                mCheckView.setUpdateIcon(R.drawable.setting_new);
                continue;
            }

            boolean updateMode = mCheckView.getUpdateMode();
            if (updateMode) {
                mCheckView.clearUpdateIcon();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNotify();
        if (app.loginUser != null) {
            mLogoutBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        viewInject(view);
        registNotify();
        //设置缓存模式
        String[] array = getResources().getStringArray(R.array.offline_array);
        mOfflineSetView.setText(array[app.config.offlineType]);

        mCacheView.setText(getCacheSize());
        mCheckView.setText(AppUtil.getColorTextAfter(
                "版本更新 ",
                mContext.getResources().getString(R.string.apk_version),
                R.color.system_normal_text
        ));
        mClearCacheView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupDialog.createMuilt(
                        mActivity,
                        "清理缓存",
                        "是否清理文件缓存",
                        new PopupDialog.PopupClickListener() {
                            @Override
                            public void onClick(int button) {
                                if (button == PopupDialog.OK) {
                                    mLoadProgressBar.setVisibility(View.VISIBLE);
                                    clearCache();
                                    mLoadProgressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        }).show();
            }
        });


        mCheckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LoadDialog loadDialog = LoadDialog.create(mActivity);
                loadDialog.setMessage("检查版本中...");
                loadDialog.show();
                AppUtil.checkUpateApp(mActivity, new StatusCallback<AppUpdateInfo>() {
                    @Override
                    public void success(final AppUpdateInfo result) {
                        loadDialog.dismiss();
                        PopupDialog popupDialog = PopupDialog.createMuilt(
                                mActivity,
                                "版本更新",
                                "更新内容\n" + result.updateInfo, new PopupDialog.PopupClickListener() {
                                    @Override
                                    public void onClick(int button) {
                                        if (button == PopupDialog.OK) {
                                            app.startUpdateWebView(result.updateUrl);
                                        } else {
                                            mCheckView.clearUpdateIcon();
                                            app.removeNotify("app_update");
                                        }
                                    }
                                });

                        popupDialog.setOkText("更新");
                        popupDialog.show();
                    }

                    @Override
                    public void error(AppUpdateInfo obj) {
                        loadDialog.dismiss();
                        mActivity.longToast("已经是最新版本!");
                    }
                });
            }
        });

        mOfflineSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitCoursePopupDialog.createNormal(
                        mActivity, "视频课时下载播放", new ExitCoursePopupDialog.PopupClickListener() {
                            @Override
                            public void onClick(int button, int position, String selStr) {
                                if (button == ExitCoursePopupDialog.CANCEL) {
                                    return;
                                }

                                app.config.offlineType = position;
                                app.saveConfig();
                                mOfflineSetView.setText(selStr);
                            }
                        }).show();
            }
        });

        mSettingAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String url = app.schoolHost + Const.ABOUT;
                app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(AboutFragment.URL, url);
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "AboutFragment");
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, getResources().getString(R.string.school_about));
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

        mAppView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "EduAppPluginFragment");
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, "应用中心");
                    }
                });
            }
        });
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
                app.sendMsgToTarget(MineFragment.LOGOUT, null, MineFragment.class);
                app.sendMsgToTarget(SchoolRoomFragment.LOGOUT, null, SchoolRoomFragment.class);
                //app.sendMsgToTarget(MyInfoFragment.LOGOUT, null, MyInfoFragment.class);

                M3U8DownService service = M3U8DownService.getService();
                if (service != null) {
                    service.cancelAllDownloadTask();
                }
            }
        });
    }


    private void clearCache() {
        File dir = AQUtility.getCacheDir(mActivity);
        AQUtility.cleanCache(dir, 0, 0);
        mCacheView.setText(getCacheSize());
        mContext.deleteDatabase("webview.db");
        mContext.deleteDatabase("webviewCache.db");
    }

    private String getCacheSize() {
        File dir = AQUtility.getCacheDir(mContext);
        long totalSize = 0;
        for (File file : dir.listFiles()) {
            totalSize += file.length();
        }

        float kb = totalSize / 1024.0f / 1024.0f;
        return String.format("%.1f%s", kb, "M");
    }
}
