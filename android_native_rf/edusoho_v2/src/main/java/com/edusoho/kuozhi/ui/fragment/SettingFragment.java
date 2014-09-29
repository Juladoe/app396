package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.annotations.ViewUtil;
import com.edusoho.kuozhi.view.dialog.PopupDialog;

import java.io.File;

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

    @Override
    public String getTitle() {
        return "设置";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.setting_fragment);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        viewInject(view);

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
        return String.valueOf(kb + "M");
    }
}
