package com.edusoho.kuozhi.v3.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

/**
 * Created by JesseHuang on 15/4/23.
 * 用于包含ActionBar的theme
 */
public class ActionBarBaseActivity extends BaseActivity {

    public static final String TAG = "ActionBarBaseActivity";
    public static final String BACK = "返回";
    public ActionBar mActionBar;
    protected TextView mTitleTextView;
    private View mTitleLayoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app.addTask(getLocalClassName(), this);
        mActionBar = getSupportActionBar();
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
    protected void onResume() {
        super.onResume();
        XGPushClickedResult click = XGPushManager.onActivityStarted(this);
        Log.d("TPush", "onResumeXGPushClickedResult:" + click);
        if (click != null) { // 判断是否来自信鸽的打开方式
            CommonUtil.longToast(this, "通知被点击:" + click.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity-->", "onPause");
        XGPushManager.onActivityStoped(this);
    }

}
