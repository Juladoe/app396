package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

/**
 * Created by JesseHuang on 15/5/6.
 */
public class SettingActivity extends ActionBarBaseActivity {
    private View viewScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setBackMode(BACK, "设置");
        initView();
    }

    private void initView() {
        viewScan = findViewById(R.id.linear_scan);
        viewScan.setOnClickListener(scanClickListener);
    }

    private View.OnClickListener scanClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.app.mEngine.runNormalPlugin("QrSchoolActivity", mActivity, null);
        }
    };
}
