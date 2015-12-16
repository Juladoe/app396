package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.StatusCallback;
import com.edusoho.kuozhi.v3.model.sys.AppUpdateInfo;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.service.EdusohoMainService;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.VolleySingleton;
import com.edusoho.kuozhi.v3.view.EduSohoTextBtn;
import com.edusoho.kuozhi.v3.view.EduToolBar;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.kuozhi.v3.view.webview.ESWebViewRequestManager;

import java.util.HashMap;

/**
 * Created by JesseHuang on 15/4/24.
 */
public class DefaultPageActivity extends ActionBarBaseActivity implements MessageEngine.MessageCallback {
    public static final String TAG = "DefaultPageActivity";

    private String mCurrentTag;
    private int mSelectBtn;
    private LinearLayout mNavLayout;
    private EduSohoTextBtn mDownTabNews;
    private EduSohoTextBtn mDownTabFind;
    private EduSohoTextBtn mDownTabFriends;
    private EduSohoTextBtn mDownTabMine;
    private EduToolBar mToolBar;
    private NavDownTabClickListener mNavDownTabClickListener;

    private boolean mLogoutFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_default);
        initView();

        if (mService != null) {
            mService.sendMessage(EdusohoMainService.LOGIN_WITH_TOKEN, null);
        }

        AppUtil.checkUpateApp(mActivity, new StatusCallback<AppUpdateInfo>() {
            @Override
            public void success(AppUpdateInfo obj) {
                Log.d(null, "new verson" + obj.androidVersion);
                if (obj.show) {
                    showUpdateDlg(obj);
                }
                app.addNotify("app_update", null);
            }
        });

        logSchoolInfoToServer();
        if (getIntent().hasExtra(Const.INTENT_TARGET) || getIntent().hasExtra(Const.SWITCH_NEWS_TAB)) {
            processIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
        setIntent(intent);
    }

    private void processIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        if (intent.hasExtra(Const.INTENT_TARGET)) {
            Class target = (Class) intent.getSerializableExtra(Const.INTENT_TARGET);
            Intent targetIntent = new Intent(mContext, target);
            targetIntent.putExtras(intent.getExtras());
            targetIntent.setFlags(intent.getFlags());
            startActivity(targetIntent);
        } else if (!intent.hasExtra(Const.SWITCH_NEWS_TAB)) {
            selectDownTab(R.id.nav_tab_find);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    private void initView() {
        mNavLayout = (LinearLayout) findViewById(R.id.nav_bottom_layout);
        mDownTabNews = (EduSohoTextBtn) findViewById(R.id.nav_tab_news);
        mDownTabFind = (EduSohoTextBtn) findViewById(R.id.nav_tab_find);
        mDownTabFriends = (EduSohoTextBtn) findViewById(R.id.nav_tab_friends);
        mDownTabMine = (EduSohoTextBtn) findViewById(R.id.nav_tab_mine);
        mToolBar = (EduToolBar) findViewById(R.id.toolbar);
        mNavDownTabClickListener = new NavDownTabClickListener();
        mToolBar.setTitle("");
        mToolBar.setNavigationIcon(R.drawable.empty_icon);
        setSupportActionBar(mToolBar);


        int count = mNavLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mNavLayout.getChildAt(i);
            child.setOnClickListener(mNavDownTabClickListener);
        }
        if (TextUtils.isEmpty(app.token) || app.loginUser == null) {
            mSelectBtn = R.id.nav_tab_find;
        } else {
            mSelectBtn = R.id.nav_tab_news;
        }
        selectDownTab(mSelectBtn);
        mToast = Toast.makeText(getApplicationContext(), getString(R.string.app_exit_msg), Toast.LENGTH_SHORT);
    }

    private class NavDownTabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            selectDownTab(v.getId());
        }
    }

    private void selectDownTab(int id) {
        String tag;
        if (app.loginUser == null && id != R.id.nav_tab_find) {
            app.mEngine.runNormalPluginWithAnim("LoginActivity", mContext, null, new NormalCallback() {
                @Override
                public void success(Object obj) {
                    mActivity.overridePendingTransition(R.anim.down_to_up, R.anim.none);
                }
            });
            return;
        }

        if (id == R.id.nav_tab_find) {
            tag = "FindFragment";
            mToolBar.setVisibility(View.GONE);
        } else if (id == R.id.nav_tab_news) {
            tag = "NewsFragment";
            mToolBar.setCenterTitle(getString(R.string.title_news));
            mToolBar.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_tab_friends) {
            tag = "FriendFragment";
            mToolBar.setCenterTitle(getString(R.string.title_friends));
            mToolBar.setVisibility(View.VISIBLE);
        } else {
            tag = "MineFragment";
            mToolBar.setCenterTitle(getString(R.string.title_mine));
            mToolBar.setVisibility(View.VISIBLE);
        }
        if (tag.equals(mCurrentTag)) {
            return;
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
        Fragment fragment;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragment = mFragmentManager.findFragmentByTag(tag);

        if (fragment != null) {
            fragmentTransaction.show(fragment);
        } else {
            fragment = app.mEngine.runPluginWithFragment(tag, mActivity, null);
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
        mDownTabMine.setTextColor(getResources().getColor(R.color.nav_btn_normal));
        mDownTabNews.setIcon(getResources().getString(R.string.font_news));
        mDownTabFind.setIcon(getResources().getString(R.string.font_find));
        mDownTabFriends.setIcon(getResources().getString(R.string.font_friends));
        mDownTabMine.setIcon(getResources().getString(R.string.font_mine));
        if (id == R.id.nav_tab_news) {
            mDownTabNews.setIcon(getResources().getString(R.string.font_news_pressed));
            mDownTabNews.setTextColor(getResources().getColor(R.color.nav_btn_pressed));
        } else if (id == R.id.nav_tab_find) {
            mDownTabFind.setIcon(getResources().getString(R.string.font_find_pressed));
            mDownTabFind.setTextColor(getResources().getColor(R.color.nav_btn_pressed));
        } else if (id == R.id.nav_tab_friends) {
            mDownTabFriends.setIcon(getResources().getString(R.string.font_friends_pressed));
            mDownTabFriends.setTextColor(getResources().getColor(R.color.nav_btn_pressed));
        } else if (id == R.id.nav_tab_mine) {
            mDownTabMine.setIcon(getResources().getString(R.string.font_mine_pressed));
            mDownTabMine.setTextColor(getResources().getColor(R.color.nav_btn_pressed));
        }
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
        if (messageType.type.equals(Const.LOGIN_SUCCESS)) {
            mLogoutFlag = true;
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (getIntent().hasExtra(Const.SWITCH_NEWS_TAB)) {
                        selectDownTab(R.id.nav_tab_find);
                    } else {
                        selectDownTab(R.id.nav_tab_news);
                    }
                    mLogoutFlag = false;
                }
            });
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[]{
                new MessageType(Const.OPEN_COURSE_CHAT, source),
                new MessageType(Const.SWITCH_TAB, source), new MessageType(Const.LOGIN_SUCCESS)};
    }

    private Toast mToast;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (null == mToast.getView().getParent()) {
                    mToast.show();
                } else {
                    finish();
                    app.exit();
                }
                return true;
            case KeyEvent.KEYCODE_MENU:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        ESWebViewRequestManager.clear();
        VolleySingleton.getInstance(getApplicationContext()).cancelAll();
    }

    @Override
    public void finish() {
        super.finish();
        //this.onDestroy();
        Log.d(TAG, "finish");
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
        HashMap<String, String> params = app.getPlatformInfo();
        School school = app.defaultSchool;
        params.put("siteHost", school.name);
        params.put("siteName", school.host);
        if (checkSchoolHasLogined(school.host)) {
            params.put("firstInstall", "true");
        }
        RequestUrl url = new RequestUrl(Const.MOBILE_SCHOOL_LOGIN);
        url.setParams(params);

        ajaxPost(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "MOBILE_SCHOOL_LOGIN success!");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "logSchoolInfoToServer failed");
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
}
