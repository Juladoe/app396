package com.edusoho.handler;

import android.os.Handler;
import android.os.Message;

import com.edusoho.kowzhi.ui.BaseActivity;

public class ProgressBarHandler extends Handler {

    public final static int REFRESH_START = 0001;
    public final static int REFRESH_STOP = 0002;

    private BaseActivity mActivity;

    public ProgressBarHandler(BaseActivity activity) {
        mActivity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case REFRESH_START:
                //mActivity.setSupportProgressBarIndeterminateVisibility(true);
                break;
            case REFRESH_STOP:
                //mActivity.setSupportProgressBarIndeterminateVisibility(false);
                break;
        }
    }
}
