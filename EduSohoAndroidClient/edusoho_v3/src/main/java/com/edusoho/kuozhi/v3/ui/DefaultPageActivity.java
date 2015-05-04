package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivityWithCordova;
import com.edusoho.kuozhi.v3.ui.fragment.FragmentNavigationDrawer;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.VolleySingleton;
import com.edusoho.kuozhi.v3.view.EduSohoTextBtn;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaWebView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JesseHuang on 15/4/24.
 */
public class DefaultPageActivity extends ActionBarBaseActivityWithCordova {

    private String mCurrentTag;
    private boolean mIsExit;
    private int mSelectBtn;
    private Timer mExitTimer;
    private LinearLayout mNavLayout;
    private EduSohoTextBtn mDownTabNews;
    private EduSohoTextBtn mDownTabFind;
    private EduSohoTextBtn mDownTabFriends;
    private Toolbar mToolBar;
    private NavDownTabClickListener mNavDownTabClickListener;

    private DrawerLayout mDrawerLayout;
    private FragmentNavigationDrawer mFragmentNavigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        initView();
        initCordovaWebView();
        mExitTimer = new Timer();
        app.addTask("DefaultPageActivity", this);

        if (savedInstanceState == null) {
            //selectItem(0);
        }

        setTitle(R.string.title_find);
    }

    @Override
    public void initCordovaWebView() {
        webView = (CordovaWebView) findViewById(R.id.webView);
        Config.init(this);
        webView.loadUrl("http://www.baidu.com", 5000);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void initView() {
        mNavLayout = (LinearLayout) findViewById(R.id.nav_bottom_layout);
        mDownTabNews = (EduSohoTextBtn) findViewById(R.id.nav_tab_news);
        mDownTabFind = (EduSohoTextBtn) findViewById(R.id.nav_tab_find);
        mDownTabFriends = (EduSohoTextBtn) findViewById(R.id.nav_tab_friends);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
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
//        String tag;
//        String title = "";
//        BaseFragment fragment;
//
//        if (id == R.id.nav_tab_news) {
//            tag = "NewsFragment";
//            title = getString(R.string.title_news);
//        } else if (id == R.id.nav_tab_find) {
//            tag = "FindFragment";
//            title = getString(R.string.title_find);
//        } else if (id == R.id.nav_tab_friends) {
//            tag = "FriendFragment";
//            title = getString(R.string.title_friends);
//        } else return;
//
//        hideFragment(mCurrentTag);
//        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//        fragment = (BaseFragment) mFragmentManager.findFragmentByTag(tag);
//        if (fragment != null) {
//            fragmentTransaction.show(fragment);
//        } else {
//            fragment = (BaseFragment) app.mEngine.runPluginWithFragment(tag, mActivity, null);
//            fragmentTransaction.add(R.id.fragment_container, fragment, tag);
//        }
//
//        fragmentTransaction.commit();
//
//        mCurrentTag = tag;

        changeNavBtn(id);
        changeBtnIcon(id);
        mSelectBtn = id;
        this.invalidateOptionsMenu();
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
        mDownTabNews.setIcon(R.string.font_news);
        mDownTabFind.setIcon(R.string.font_find);
        mDownTabFriends.setIcon(R.string.font_friends);
        if (id == R.id.nav_tab_news) {
            setTitle(R.string.title_news);
            mDownTabNews.setIcon(R.string.font_news_press);
        } else if (id == R.id.nav_tab_find) {
            setTitle(R.string.title_find);
            mDownTabFind.setIcon(R.string.font_find_press);
        } else if (id == R.id.nav_tab_friends) {
            setTitle(R.string.title_friends);
            mDownTabFriends.setIcon(R.string.font_friends_press);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mSelectBtn == R.id.nav_tab_find) {
            getMenuInflater().inflate(R.menu.find_menu, menu);
        } else if (mSelectBtn == R.id.nav_tab_friends) {
            getMenuInflater().inflate(R.menu.friends_menu, menu);
        } else {
            this.closeOptionsMenu();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            synchronized (mContext) {
                if (mIsExit) {
                    mIsExit = false;
                    app.exit();
                }
                CommonUtil.longToast(mContext, getString(R.string.app_exit_msg));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExitTimer.cancel();
        mExitTimer = null;
        VolleySingleton.getInstance(getApplicationContext()).cancelAll();
    }
}
