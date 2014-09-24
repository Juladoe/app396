package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.util.annotations.ViewUtil;

import java.io.File;

import cn.trinea.android.common.util.StringUtils;

/**
 * Created by howzhi on 14-9-21.
 */
public class SettingFragment extends BaseFragment {

    @ViewUtil("setting_clear_btn")
    private TextView mClearCacheView;

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

        mClearCacheView.setText("清理缓存    " + getCacheSize());
        mClearCacheView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AQUtility.cleanCacheAsync(mContext);
            }
        });
    }

    private String getCacheSize()
    {
        File dir = AQUtility.getCacheDir(mContext);
        long totalSize = 0;
        for (File file : dir.listFiles()) {
            totalSize += file.length();
        }

        float kb = totalSize / 1024.0f;
        return String.valueOf(kb + "kb");
    }
}
