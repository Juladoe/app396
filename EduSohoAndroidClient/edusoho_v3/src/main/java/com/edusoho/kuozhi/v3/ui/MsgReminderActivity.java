package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

/**
 * Created by JesseHuang on 15/5/18.
 */
public class MsgReminderActivity extends ActionBarBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_reminder);
        setBackMode(BACK, "新消息提醒");
    }
}
