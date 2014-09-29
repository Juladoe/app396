package com.edusoho.kuozhi.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;

import com.edusoho.kuozhi.Service.EdusohoMainService;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.view.EduSohoTextBtn;
import java.util.List;
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
    private String mCurrentTag;
    private int mSelectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defalt_layout);
        setBackMode(null, "推荐");
        initView();
        mExitTimer = new Timer();
        mService.sendMessage(EdusohoMainService.LOGIN_WITH_TOKEN, null);
        app.addTask("DefaultPageActivity", this);
    }

    private void initView() {
        mNavBtnClickListener = new NavBtnClickListener();
        bindNavOnClick();
        if ("".equals(app.token)) {
            mSelectBtn = R.id.nav_recommend_btn;
        } else {
            mSelectBtn = R.id.nav_me_btn;
        }

        selectNavBtn(mSelectBtn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExitTimer.cancel();
        mExitTimer = null;
        AQUtility.cleanCacheAsync(this);
        BitmapAjaxCallback.clearCache();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            returnHome();
            /*
            synchronized (mContext) {
                if (mIsExit) {
                    mIsExit = false;
                    finish();
                    app.exit();
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
            */
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void returnHome()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }

    private void selectNavBtn(int id) {
        String tag = null;
        BaseFragment fragment = null;

        if (id == R.id.nav_recommend_btn) {
            tag = "RecommendFragment";
        } else if(id == R.id.nav_found_btn) {
            tag = "FoundFragment";
        }else if(id == R.id.nav_me_btn) {
            tag = "MyInfoFragment";
        }else if(id == R.id.nav_more_btn) {
            tag = "MoreSettingFragment";
        }else {
            return;
        }

        hideFragment(mCurrentTag);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragment = (BaseFragment) mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentTransaction.show(fragment);
        } else {
            fragment = (BaseFragment) app.mEngine.runPluginWithFragment(tag, mActivity, null);
            fragmentTransaction.add(R.id.fragment_container, fragment, tag);
        }

        fragmentTransaction.commit();
        mCurrentTag = tag;
        setTitle(fragment.getTitle());
        changeNavBtn(id);
    }

    private void hideFragment(String tag)
    {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            return;
        }
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commit();
    }

    private void hideAllFragments()
    {
        List<Fragment> fragments = mFragmentManager.getFragments();
        if (fragments == null) {
            return;
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        for (Fragment fragment: fragments) {
            fragmentTransaction.hide(fragment);
        }
        fragmentTransaction.commit();
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
