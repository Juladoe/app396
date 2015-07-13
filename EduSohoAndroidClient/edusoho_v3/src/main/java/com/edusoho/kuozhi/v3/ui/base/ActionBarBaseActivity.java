package com.edusoho.kuozhi.v3.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by JesseHuang on 15/4/23.
 * 用于包含ActionBar的theme
 */
public class ActionBarBaseActivity extends BaseActivity implements MessageEngine.MessageCallback {

    public static final int PAUSE = 0001;
    public static final int RESUME = 0010;

    public static final String TAG = "ActionBarBaseActivity";
    public static final String BACK = "返回";
    public ActionBar mActionBar;
    protected TextView mTitleTextView;
    private View mTitleLayoutView;
    protected int mRunStatus;
    private Queue<WidgetMessage> mUIMessageQueue;

    protected XGPushClickedResult mXGClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar = getSupportActionBar();
        app.registMsgSource(this);
        mUIMessageQueue = new ArrayDeque<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRunStatus = RESUME;
        invokeUIMessage();
        mXGClick = XGPushManager.onActivityStarted(this);
        Log.d("TPush", "onResumeXGPushClickedResult:" + mXGClick);
//        if (mXGClick != null) { // 判断是否来自信鸽的打开方式
//            CommonUtil.longToast(this, "通知被点击:" + mXGClick.toString());
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRunStatus = PAUSE;
        Log.d("MainActivity-->", "onPause");
        XGPushManager.onActivityStoped(this);
        mXGClick = null;
    }

    public void setBackMode(String backTitle, String title) {
        mTitleLayoutView = getLayoutInflater().inflate(R.layout.actionbar_custom_title, null);
        mTitleTextView = (TextView) mTitleLayoutView.findViewById(R.id.tv_action_bar_title);
        mTitleTextView.setText(title);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        //layoutParams.width = (int) (EdusohoApp.screenW * 0.6);
        layoutParams.gravity = Gravity.CENTER;
        mActionBar.setCustomView(mTitleLayoutView, layoutParams);

        if (backTitle != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.unRegistMsgSource(this);
    }

    protected void invokeUIMessage() {
        WidgetMessage message = null;
        while ((message = mUIMessageQueue.peek()) != null) {
            invoke(message);
        }
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (mRunStatus == PAUSE && messageType.runType == MessageType.UI_THREAD) {
            mUIMessageQueue.add(message);

            message.type.type = "";
            message.type.code = MessageType.NONE;
            return;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[0];
    }

    @Override
    public int getMode() {
        return REGIST_CLASS;
    }
}
