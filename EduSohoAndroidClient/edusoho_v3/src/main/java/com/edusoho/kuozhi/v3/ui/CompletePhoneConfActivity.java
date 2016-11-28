package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

/**
 * Created by DF on 2016/11/28.
 */
public class CompletePhoneConfActivity extends ActionBarBaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_phone_conf);
        hideActionBar();
        initView();
    }

    private void initView() {
        
    }
}
