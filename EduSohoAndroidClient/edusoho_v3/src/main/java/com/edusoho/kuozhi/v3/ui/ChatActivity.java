package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;

/**
 * Created by JesseHuang on 15/6/3.
 */
public class ChatActivity extends ActionBarBaseActivity {

    public static final int COURSE_CHAT = 0x01;
    public static final String COURSE_ID = "course_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setBackMode(BACK, "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null) {
            String courseId = intent.getStringExtra(COURSE_ID);
            CommonUtil.longToast(mActivity, courseId);
        }
    }
}
