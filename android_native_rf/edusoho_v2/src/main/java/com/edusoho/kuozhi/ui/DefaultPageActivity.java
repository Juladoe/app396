package com.edusoho.kuozhi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;

import com.edusoho.kuozhi.Service.EdusohoMainService;
import com.edusoho.kuozhi.model.AppUpdateInfo;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EduSohoTextBtn;
import com.edusoho.listener.StatusCallback;
import com.plugin.edusoho.bdvideoplayer.BdPlayerManager;

import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private EduSohoTextBtn moreBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defalt_layout);
        setBackMode(null, "推荐");
        initView();
        mExitTimer = new Timer();
        mService.sendMessage(EdusohoMainService.LOGIN_WITH_TOKEN, null);
        app.addTask("DefaultPageActivity", this);

        AppUtil.checkUpateApp(mActivity, new StatusCallback<AppUpdateInfo>(){
            @Override
            public void success(AppUpdateInfo obj) {
                Log.d(null, "new verson");
                moreBtn.setUpdateIcon();
                app.addNotify("app_update", null);
            }
        });

        BdPlayerManager bdPlayerManager = new BdPlayerManager(mContext);
        bdPlayerManager.checkPlayerVersion(new BdPlayerManager.CheckPlayerVersionCallback() {
            @Override
            public void success() {

            }

            @Override
            public void fail() {

            }
        });
        logSchoolInfoToServer();
    }

    private void logSchoolInfoToServer()
    {
        Map<String, String> params = app.getPlatformInfo();
        School school = app.defaultSchool;
        params.put("siteHost", school.name);
        params.put("siteName", school.host);
        Log.d(null, "MOBILE_SCHOOL_LOGIN");
        app.logToServer(Const.MOBILE_SCHOOL_LOGIN, params, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotify();
    }

    private void checkNotify()
    {
        Set<String> notifys = app.getNotifys();
        moreBtn.clearUpdateIcon();
        for (String type : notifys) {
            if (moreBtn.hasNotify(type)) {
                moreBtn.setUpdateIcon();
                continue;
            }
        }
    }

    @Override
    public void onLowMemory() {
        Log.d(null, "on LowMemory-> main");
        super.onLowMemory();
    }

    private void initView() {
        moreBtn = (EduSohoTextBtn) findViewById(R.id.nav_more_btn);
        mNavBtnClickListener = new NavBtnClickListener();
        bindNavOnClick();
        if (app.token == null || "".equals(app.token)) {
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
            //returnHome();
            synchronized (mContext) {
                if (mIsExit) {
                    mIsExit = false;
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
            if (child.getId() == id) {
                child.setEnabled(false);
            } else {
                child.setEnabled(true);
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
