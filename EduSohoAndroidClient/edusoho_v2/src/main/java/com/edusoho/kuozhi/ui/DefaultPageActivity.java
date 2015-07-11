package com.edusoho.kuozhi.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
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
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.StatusCallback;

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
    protected int mSelectBtn;

    private EduSohoTextBtn mSchoolRoomBtn;
    private EduSohoTextBtn mRecommendBtn;
    private EduSohoTextBtn mFoundBtn;
    private EduSohoTextBtn mMySelfBtn;

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

        AppUtil.checkUpateApp(mActivity, new StatusCallback<AppUpdateInfo>() {
            @Override
            public void success(AppUpdateInfo obj) {
                Log.d(null, "new verson" + obj.androidVersion);
                if (obj.show) {
                    showUpdateDlg(obj);
                }
                moreBtn.setUpdateIcon();
                app.addNotify("app_update", null);
            }
        });

        logSchoolInfoToServer();
    }

    private void showUpdateDlg(final AppUpdateInfo result) {
        PopupDialog popupDialog = PopupDialog.createMuilt(
                mActivity,
                "版本更新",
                "更新内容\n" + result.updateInfo, new PopupDialog.PopupClickListener() {
                    @Override
                    public void onClick(int button) {
                        if (button == PopupDialog.OK) {
                            app.startUpdateWebView(result.updateUrl);
                            app.removeNotify("app_update");
                        }
                    }
                });

        popupDialog.setOkText("更新");
        popupDialog.show();
    }

    private void logSchoolInfoToServer() {
        Map<String, String> params = app.getPlatformInfo();
        School school = app.defaultSchool;
        params.put("siteHost", school.name);
        params.put("siteName", school.host);
        if (checkSchoolHasLogined(school.host)) {
            params.put("firstInstall", "true");
        }
        Log.d(null, "MOBILE_SCHOOL_LOGIN");
        app.logToServer(Const.MOBILE_SCHOOL_LOGIN, params, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                super.callback(url, object, status);
                Log.d(null, "MOBILE_SCHOOL_LOGIN->" + object);
            }
        });
    }

    private boolean checkSchoolHasLogined(String host) {
        if (host.startsWith("http://")) {
            host = host.substring(7);
            Log.d(null, "host->" + host);
        }
        SharedPreferences sp = getSharedPreferences("search_history", MODE_PRIVATE);
        if (sp.contains(host)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotify();
    }

    private void checkNotify() {
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

    protected void initView() {
        moreBtn = (EduSohoTextBtn) findViewById(R.id.nav_me_btn);
        mNavBtnClickListener = new NavBtnClickListener();
        bindNavOnClick();
        initNavSelected();
    }

    protected void initNavSelected() {
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

    private void returnHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }

    protected void selectNavBtn(int id) {
        String tag = null;
        boolean showIcon = false;
        BaseFragment fragment = null;
        String title = "";

        if (id == R.id.nav_recommend_btn) {
            tag = "RecommendFragment";
            title = getString(R.string.title_recommend);
        } else if (id == R.id.nav_found_btn) {
            tag = "FoundFragment";
            title = getString(R.string.title_found);
            showIcon = true;
        } else if (id == R.id.nav_schoolroom_btn) {
            tag = "SchoolRoomFragment";
            title = getString(R.string.title_school);
            //tag = "MyInfoFragment";
        } else if (id == R.id.nav_me_btn) {
            tag = "MineFragment";
            title = getString(R.string.title_me);
        } else {
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
        setTitle(title, tag, showIcon);
        changeNavBtn(id);
        changeBtnIcon(id);
    }

    private void hideFragment(String tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            return;
        }
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commit();
    }

    private void hideAllFragments() {
        List<Fragment> fragments = mFragmentManager.getFragments();
        if (fragments == null) {
            return;
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        for (Fragment fragment : fragments) {
            fragmentTransaction.hide(fragment);
        }
        fragmentTransaction.commit();
    }

    private void changeNavBtn(int id) {
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

    private void changeBtnIcon(int id) {
        mSchoolRoomBtn.setIcon(R.string.schoolroom);
        mRecommendBtn.setIcon(R.string.font_recommend);
        mFoundBtn.setIcon(R.string.font_found);
        mMySelfBtn.setIcon(R.string.font_me);
        if (id == R.id.nav_schoolroom_btn) {
            mSchoolRoomBtn.setIcon(R.string.schoolroom_press);
        } else if (id == R.id.nav_recommend_btn) {
            mRecommendBtn.setIcon(R.string.font_recommend_press);
        } else if (id == R.id.nav_found_btn) {
            mFoundBtn.setIcon(R.string.font_found_press);
        } else if (id == R.id.nav_me_btn) {
            mMySelfBtn.setIcon(R.string.font_me_press);
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
        mSchoolRoomBtn = (EduSohoTextBtn) findViewById(R.id.nav_schoolroom_btn);
        mRecommendBtn = (EduSohoTextBtn) findViewById(R.id.nav_recommend_btn);
        mFoundBtn = (EduSohoTextBtn) findViewById(R.id.nav_found_btn);
        mMySelfBtn = (EduSohoTextBtn) findViewById(R.id.nav_me_btn);
        int count = mNavBtnLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mNavBtnLayout.getChildAt(i);
            child.setOnClickListener(mNavBtnClickListener);
        }
    }

}
