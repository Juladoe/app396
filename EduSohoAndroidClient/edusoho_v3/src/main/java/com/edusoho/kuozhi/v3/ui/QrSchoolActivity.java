package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

/**
 * Created by JesseHuang on 15/5/6.
 * 扫描网校界面
 */
public class QrSchoolActivity extends ActionBarBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_school);
        setBackMode(null, "进入网校");
    }
}
