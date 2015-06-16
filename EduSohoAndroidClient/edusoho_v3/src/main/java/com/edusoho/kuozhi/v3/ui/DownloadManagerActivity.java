package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

/**
 * Created by JesseHuang on 15/6/16.
 */
public class DownloadManagerActivity extends ActionBarBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        setBackMode(BACK, "下载管理");
    }
}
