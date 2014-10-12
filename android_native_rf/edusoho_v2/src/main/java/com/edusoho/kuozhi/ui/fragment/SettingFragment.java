package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.AppUpdateInfo;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.annotations.ViewUtil;
import com.edusoho.kuozhi.view.EduUpdateView;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.ResultCallback;
import com.edusoho.listener.StatusCallback;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Set;

import cn.trinea.android.common.util.StringUtils;

/**
 * Created by howzhi on 14-9-21.
 */
public class SettingFragment extends BaseFragment {

    @ViewUtil("setting_clear_btn")
    private View mClearCacheView;

    @ViewUtil("setting_cache_view")
    private TextView mCacheView;

    @ViewUtil("setting_load_progress")
    private ProgressBar mLoadProgressBar;

    @ViewUtil("setting_fix_btn")
    private TextView mFixBtn;

    @ViewUtil("setting_check_version")
    private EduUpdateView mCheckView;

    @Override
    public String getTitle() {
        return "设置";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.setting_fragment);
    }

    private void registNotify()
    {
        mCheckView.addNotifyType("app_update");
    }

    private void checkNotify()
    {
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
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        viewInject(view);
        registNotify();

        mCacheView.setText(getCacheSize());
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

        mFixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FragmentPageActivity.FRAGMENT, "SuggestionFragment");
                bundle.putString(Const.ACTIONBAT_TITLE, "意见反馈");
                startAcitivityWithBundle("FragmentPageActivity", bundle);
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
    }

    private void clearCache()
    {
        File dir = AQUtility.getCacheDir(mActivity);
        AQUtility.cleanCache(dir, 0, 0);
        mCacheView.setText(getCacheSize());
    }

    private String getCacheSize()
    {
        File dir = AQUtility.getCacheDir(mContext);
        long totalSize = 0;
        for (File file : dir.listFiles()) {
            totalSize += file.length();
        }

        float kb = totalSize / 1024.0f / 1024.0f;
        return String.format("%.1f%s", kb, "M");
    }
}
