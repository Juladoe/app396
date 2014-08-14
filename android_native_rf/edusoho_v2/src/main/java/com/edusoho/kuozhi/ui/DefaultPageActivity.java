package com.edusoho.kuozhi.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;

import com.edusoho.kuozhi.view.EduSohoTextBtn;
import com.edusoho.kuozhi.view.dialog.EdusohoMaterialDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by howzhi on 14-7-22.
 */
public class DefaultPageActivity extends ActionBarBaseActivity {

    private Timer mExitTimer;
    private boolean mIsExit;
    private ViewGroup mNavBtnLayout;
    private NavBtnClickListener mNavBtnClickListener;
    private static final String FRAGMENT_TAG = "fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defalt_layout);
        setBackMode(null, "推荐");
        initView();
        mExitTimer = new Timer();
    }

    private void initView() {
        mNavBtnClickListener = new NavBtnClickListener();
        bindNavOnClick();
        selectNavBtn(R.id.nav_recommend_btn);
        addListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExitTimer.cancel();
        mExitTimer = null;
        AQUtility.cleanCacheAsync(this);
        BitmapAjaxCallback.clearCache();
    }

    private void addListener()
    {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            synchronized (mContext) {
                if (mIsExit) {
                    mIsExit = false;
                    app.exit();
                    finish();
                }
                longToast("再按一次退出应用");
                mIsExit = true;
                mExitTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void selectNavBtn(int id) {
        Fragment fragment = null;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (id == R.id.nav_recommend_btn) {
            fragment = app.mEngine.runPluginWithFragment("RecommendFragment", mActivity, null);
        } else {
            return;
        }

        if (mFragmentManager.findFragmentByTag(FRAGMENT_TAG) != null) {
            mFragmentManager.popBackStack();
        }
        fragmentTransaction.add(R.id.fragment_container, fragment, FRAGMENT_TAG).commit();
        changeNavBtn(id);
    }

    private void changeNavBtn(int id)
    {
        mNavBtnLayout = (ViewGroup) findViewById(R.id.nav_bottom_layout);
        int count = mNavBtnLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mNavBtnLayout.getChildAt(i);
            if (child instanceof EduSohoTextBtn) {
                if (child.getId() == id) {
                    enableBtn((ViewGroup)child, false);
                } else {
                    enableBtn((ViewGroup) child, true);
                }
            }
        }
    }
    private class NavBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            selectNavBtn(id);
        }
    }

    private void bindNavOnClick() {
        mNavBtnLayout = (ViewGroup) findViewById(R.id.nav_bottom_layout);
        int count = mNavBtnLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mNavBtnLayout.getChildAt(i);
            child.setOnClickListener(mNavBtnClickListener);
        }
    }

}
