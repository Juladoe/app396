package com.edusoho.kuozhi.v3.ui.base;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;

/**
 * Created by DEL on 2016/11/24.
 */

public class BaseNoTitleActivity extends BaseActivity implements MessageEngine.MessageCallback  {

    protected int mRunStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        app.registMsgSource(this);
    }

    protected void initView() {
        hideActionBar();
        View back = findViewById(R.id.back);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    public void invoke(WidgetMessage message) {

    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[0];
    }

    @Override
    public int getMode() {
        return REGIST_CLASS;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRunStatus = MSG_RESUME;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRunStatus = MSG_PAUSE;
    }

    @Override
    public void finish() {
        super.finish();
        app.unRegistMsgSource(this);
    }
}
