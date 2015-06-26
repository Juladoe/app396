package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.service.EdusohoMainService;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.fragment.FindFragment;
import com.edusoho.kuozhi.v3.ui.fragment.FragmentNavigationDrawer;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.VolleySingleton;
import com.edusoho.kuozhi.v3.view.EduSohoTextBtn;
import com.edusoho.kuozhi.v3.view.EduToolBar;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;

import org.apache.cordova.CordovaWebView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JesseHuang on 15/4/24.
 */
public class DefaultPageActivity extends ActionBarBaseActivity implements MessageEngine.MessageCallback {
    public static final String TAG = "DefaultPageActivity";
    public static final int XG_PUSH_REGISTER = 0x01;

    private String mCurrentTag;
    private boolean mIsExit;
    private boolean isKeyBack;
    private int mSelectBtn;
    private Timer mExitTimer;
    private LinearLayout mNavLayout;
    private EduSohoTextBtn mDownTabNews;
    private EduSohoTextBtn mDownTabFind;
    private EduSohoTextBtn mDownTabFriends;
    private EduToolBar mToolBar;
    private NavDownTabClickListener mNavDownTabClickListener;

    private DrawerLayout mDrawerLayout;
    private FragmentNavigationDrawer mFragmentNavigationDrawer;
    private final byte[] mLock = new byte[1];
    private boolean mLogoutFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_default);
        initView();

        if (savedInstanceState == null) {
            //selectItem(0);
        }
        mService.sendMessage(EdusohoMainService.LOGIN_WITH_TOKEN, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    private void initView() {
        mNavLayout = (LinearLayout) findViewById(R.id.nav_bottom_layout);
        mDownTabNews = (EduSohoTextBtn) findViewById(R.id.nav_tab_news);
        mDownTabFind = (EduSohoTextBtn) findViewById(R.id.nav_tab_find);
        mDownTabFriends = (EduSohoTextBtn) findViewById(R.id.nav_tab_friends);
        mToolBar = (EduToolBar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavDownTabClickListener = new NavDownTabClickListener();

        setSupportActionBar(mToolBar);

        int count = mNavLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mNavLayout.getChildAt(i);
            child.setOnClickListener(mNavDownTabClickListener);
        }
        if (TextUtils.isEmpty(app.token)) {
            mSelectBtn = R.id.nav_tab_find;
        } else {
            mSelectBtn = R.id.nav_tab_news;
        }

        selectDownTab(mSelectBtn);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mFragmentNavigationDrawer = (FragmentNavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mFragmentNavigationDrawer.initDrawer(mDrawerLayout, R.id.navigation_drawer);

    }

    public EduToolBar getToolBar() {
        return mToolBar;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
//        Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//
//        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[position]);
//        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private class NavDownTabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            selectDownTab(v.getId());
        }
    }

    private void selectDownTab(int id) {
        String tag;
        if (TextUtils.isEmpty(app.token) && id != R.id.nav_tab_find) {
            app.sendMsgToTarget(Const.MAIN_MENU_OPEN, null, FragmentNavigationDrawer.class);
            return;
        }

        if (id == R.id.nav_tab_find) {
            tag = "FindFragment";
            mToolBar.setVisibility(View.GONE);
        } else if (id == R.id.nav_tab_news) {
            tag = "NewsFragment";
            mToolBar.setCenterTitle(getString(R.string.title_news));
            mToolBar.setVisibility(View.VISIBLE);
        } else {
            tag = "FriendFragment";
            mToolBar.setCenterTitle(getString(R.string.title_friends));
            mToolBar.setVisibility(View.VISIBLE);
        }
        hideFragment(mCurrentTag);
        showFragment(tag);
        changeNavBtn(id);
        changeBtnIcon(id);
        mSelectBtn = id;
    }

    private void hideFragment(String tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentTransaction.hide(fragment);
            if (!mLogoutFlag) {
                fragmentTransaction.commit();
            } else {
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    }

    private void showFragment(String tag) {
        BaseFragment fragment;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragment = (BaseFragment) mFragmentManager.findFragmentByTag(tag);

        if (fragment != null) {
            fragmentTransaction.show(fragment);
        } else {
            fragment = (BaseFragment) app.mEngine.runPluginWithFragment(tag, mActivity, null);
            fragmentTransaction.add(R.id.fragment_container, fragment, tag);
        }

        if (!mLogoutFlag) {
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.commitAllowingStateLoss();
        }
        mCurrentTag = tag;
    }

    private void changeNavBtn(int id) {
        int count = mNavLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mNavLayout.getChildAt(i);
            if (child.getId() == id) {
                child.setEnabled(false);
            } else {
                child.setEnabled(true);
            }
        }
    }

    private void changeBtnIcon(int id) {
        mDownTabNews.setTextColor(getResources().getColor(R.color.nav_btn_normal));
        mDownTabFind.setTextColor(getResources().getColor(R.color.nav_btn_normal));
        mDownTabFriends.setTextColor(getResources().getColor(R.color.nav_btn_normal));
        if (id == R.id.nav_tab_news) {
            mDownTabNews.setTextColor(getResources().getColor(R.color.nav_btn_pressed));
        } else if (id == R.id.nav_tab_find) {
            mDownTabFind.setTextColor(getResources().getColor(R.color.nav_btn_pressed));
        } else if (id == R.id.nav_tab_friends) {
            mDownTabFriends.setTextColor(getResources().getColor(R.color.nav_btn_pressed));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void registerXgPush() {
        XGPushConfig.enableDebug(this, true);
        XGPushManager.registerPush(mContext, app.loginUser.id + "", new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                Log.w(Constants.LogTag,
                        "+++ register push success. token:" + data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.w(Constants.LogTag,
                        "+++ register push fail. token:" + data
                                + ", errCode:" + errCode + ",msg:"
                                + msg);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        switch (messageType.code) {
            case Const.OPEN_COURSE_CHAT:
                app.mEngine.runNormalPlugin("ChatActivity", mContext, null);
                break;
            case XG_PUSH_REGISTER:
                registerXgPush();
                break;
            case Const.SWITCH_TAB:
                try {
                    mLogoutFlag = true;
                    selectDownTab(R.id.nav_tab_find);
                    mLogoutFlag = false;
                } catch (Exception ex) {
                    Log.d(TAG, ex.getMessage());
                }
                break;
            default:
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(Const.OPEN_COURSE_CHAT, source),
                new MessageType(XG_PUSH_REGISTER, source),
                new MessageType(Const.SWITCH_TAB, source)};
        return messageTypes;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isKeyBack = true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mFragmentNavigationDrawer.isDrawerOpen()) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            }

            Fragment fragment = mFragmentManager.findFragmentByTag("FindFragment");
            if (fragment instanceof FindFragment) {
                CordovaWebView webView = ((FindFragment) fragment).getView().getWebView();
                if (webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
            }

            synchronized (mLock) {
                if (mIsExit) {
                    mIsExit = false;
                    app.exit();
                }
                CommonUtil.longToast(mContext, getString(R.string.app_exit_msg));
                mIsExit = true;
                if (mExitTimer == null) {
                    mExitTimer = new Timer();
                }
                mExitTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mExitTimer != null) {
            Log.d(TAG, "mExitTimer.cancel()");
            mExitTimer.cancel();
            mExitTimer = null;
        }
        VolleySingleton.getInstance(getApplicationContext()).cancelAll();
    }

    @Override
    public void finish() {
        if (isKeyBack) {
            isKeyBack = false;
            return;
        }
        super.finish();
        Log.d(TAG, "finish");
    }
}
