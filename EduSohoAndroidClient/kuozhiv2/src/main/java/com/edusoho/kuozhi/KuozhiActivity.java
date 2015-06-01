package com.edusoho.kuozhi;

import android.os.Bundle;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.edusoho.kuozhi.ui.StartActivity;
import com.tencent.bugly.crashreport.CrashReport;


public class KuozhiActivity extends StartActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CrashReport.initCrashReport(getApplicationContext(), getString(R.string.bugly_appid), false);
        super.onCreate(savedInstanceState);
        StatService.setAppKey("8f1996ac26");
        StatService.setAppChannel(this, "Edusoho", true);
        StatService.setSessionTimeOut(30);
        StatService.setOn(this, StatService.EXCEPTION_LOG);
        StatService.setLogSenderDelayed(0);
        StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 0);
        StatService.setDebugOn(false);
    }
}
