package com.edusoho.kuozhi.clean.module.courseset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;

/**
 * Created by DF on 2017/4/12.
 */

public class BaseFinishActivity<T extends BasePresenter> extends BaseActivity<T> {

    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Finish");
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastReceiver);
    }
}
