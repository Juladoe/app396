package com.edusoho.kuozhi;

import android.os.Bundle;

import com.tencent.bugly.crashreport.CrashReport;
import com.edusoho.kuozhi.ui.StartActivity;

public class KuozhiActivity extends StartActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CrashReport.initCrashReport(getApplicationContext(), getString(R.string.bugly_appid), false);
        super.onCreate(savedInstanceState);
    }
}
