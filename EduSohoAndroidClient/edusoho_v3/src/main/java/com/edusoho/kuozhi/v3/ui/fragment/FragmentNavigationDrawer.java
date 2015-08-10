package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.ui.WebViewActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoIconView;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.lang.ref.WeakReference;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JesseHuang on 15/4/27.
 */
public class FragmentNavigationDrawer extends BaseFragment {

    public static final String TAG = "FragmentDrawer";
    public static final int THIRD_PARTY_LOGIN = 0x01;
    private static final int LOGIN_SUCCESS = 0x4;
    public static final int OPEN_DRAWER = 0x02;
    public static final int CLOSE_DRAWER = 0x03;
    public static final int DRAWER_REGISTER = 0x11;
    private static final int LOGOUT_SUCCESS = 0x05;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private View mDrawerFragment;
    private int mPosition;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private final int mRadioIds[] = {
            R.id.radio0,
            R.id.radio1,
            R.id.radio2,
            R.id.radio3,
            R.id.radio4,
    };

    private TextView tvNickname;
    private TextView tvTitle;
    private TextView tvLogin;
    private CircleImageView civAvatar;
    private DrawerHandler mHandler;
    private View vItems;
    private View vLogin;
    private Button btnLogin;
    private Button btnRegister;
    private View userInfoLayout;
    private EduSohoIconView ivSetting;

    private final RadioButton[] mRadioButtons = new RadioButton[mRadioIds.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_navigation_drawer);
        mHandler = new DrawerHandler(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void initDrawer(DrawerLayout drawerLayout, int fragmentDrawerId) {
        initView();
        mDrawerFragment = mActivity.findViewById(fragmentDrawerId);
        mTitle = mDrawerTitle = mActivity.getTitle();

        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        final ActionBar actionBar = mActivity.getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                mActivity,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                mActivity.setTitle(mTitle);
//                mActivity.invalidateOptionsMenu();
                switch (mPosition) {
                    case 0:
                        //我的学习
                        mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.MY_LEARN);
                                startIntent.putExtra(WebViewActivity.URL, url);
                            }
                        });
                        break;
                    case 1:
                        //我的下载
                        mActivity.app.mEngine.runNormalPlugin("DownloadManagerActivity1", mContext, null);
                        break;
                    case 2:
                        //开通会员
                        mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.VIP_LIST);
                                startIntent.putExtra(WebViewActivity.URL, url);
                            }
                        });
                        break;
                    case 3:
                        //我的收藏
                        mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.MY_FAVORITE);
                                startIntent.putExtra(WebViewActivity.URL, url);
                            }
                        });
                        break;
                    case 4:
                        //我的设置
                        mActivity.app.mEngine.runNormalPlugin("SettingActivity", mContext, null);
                        break;
                    default:
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mPosition = -1;
//                mActivity.setTitle(mDrawerTitle);
//                mActivity.invalidateOptionsMenu();
            }
        };
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
                actionBar.setHomeAsUpIndicator(R.drawable.drawer_open);
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initView() {
        for (int i = 0; i < mRadioButtons.length; i++) {
            mRadioButtons[i] = (RadioButton) getView().findViewById(mRadioIds[i]);
            mRadioButtons[i].setOnClickListener(mRadioBtnClickListener);
        }

        userInfoLayout = mActivity.findViewById(R.id.navigation_userinfo_layout);
        tvNickname = (TextView) mActivity.findViewById(R.id.tv_nickname);
        tvTitle = (TextView) mActivity.findViewById(R.id.tv_user_title);
        tvLogin = (TextView) mActivity.findViewById(R.id.tv_login);
        civAvatar = (CircleImageView) mActivity.findViewById(R.id.circleIcon);
        vItems = mActivity.findViewById(R.id.ll_item);
        vLogin = mActivity.findViewById(R.id.ll_login);
        btnLogin = (Button) mActivity.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(mLoginClickListener);
        btnRegister = (Button) mActivity.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(mRegisterClickListener);
        ivSetting = (EduSohoIconView) mActivity.findViewById(R.id.iv_setting);
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.app.mEngine.runNormalPlugin("SettingActivity", mContext, null);
            }
        });
        if (app.loginUser == null) {
            setLoginStatus(Const.LOGOUT_SUCCESS);
        } else {
            setLoginStatus(Const.LOGIN_SUCCESS);
        }

        userInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.loginUser == null) {
                    return;
                }

                mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.MY_INFO);
                        startIntent.putExtra(WebViewActivity.URL, url);
                    }
                });
            }
        });
    }

    View.OnClickListener mRadioBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mRadioButtons.length; i++) {
                if (v.equals(mRadioButtons[i])) {
                    selectItem(i);
                } else {
                    mRadioButtons[i].setChecked(false);
                }
            }
        }
    };

    View.OnClickListener mLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (app.loginUser == null) {
                LoginActivity.startLogin(mActivity);
            }
        }
    };

    View.OnClickListener mRegisterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.app.mEngine.runNormalPluginForResult("RegisterActivity", mActivity, Const.DRAWER_REGISTER, null);
        }
    };

    private void selectItem(int position) {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mDrawerFragment);
        }
        mPosition = position;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawerFragment);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mDrawerLayout != null && isDrawerOpen()) {

        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (messageType.code == Const.MAIN_MENU_OPEN) {
            Message msg = mHandler.obtainMessage();
            msg.what = OPEN_DRAWER;
            mHandler.sendMessage(msg);
        } else if (messageType.code == Const.MAIN_MENU_CLOSE) {
            Message msg = mHandler.obtainMessage();
            msg.what = CLOSE_DRAWER;
            mHandler.sendMessage(msg);
        } else {
            Message msg = mHandler.obtainMessage();
            switch (messageType.type) {
                case Const.LOGIN_SUCCESS:
                    msg.what = LOGIN_SUCCESS;
                    msg.obj = messageType.type;
                    mHandler.sendMessage(msg);
                    break;
                case Const.THIRD_PARTY_LOGIN_SUCCESS:
                    try {
                        msg.what = THIRD_PARTY_LOGIN;
                        mHandler.sendMessage(msg);
                    } catch (Exception e) {
                        throw e;
                    }
                    break;
                case Const.LOGOUT_SUCCESS:
                    msg.what = LOGOUT_SUCCESS;
                    msg.obj = messageType.type;
                    mHandler.sendMessage(msg);
                    break;
                default:
            }
        }
    }

    private static class DrawerHandler extends Handler {
        private WeakReference<FragmentNavigationDrawer> mWeakReference;

        public DrawerHandler(FragmentNavigationDrawer fragment) {
            mWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final FragmentNavigationDrawer mFragment = mWeakReference.get();
            if (mFragment != null) {
                switch (msg.what) {
                    case OPEN_DRAWER:
                        mFragment.mDrawerLayout.openDrawer(Gravity.LEFT);
                        break;
                    case CLOSE_DRAWER:
                        mFragment.mDrawerLayout.closeDrawer(Gravity.LEFT);
                        //通知主页面跳到发现
                        mFragment.app.sendMsgToTarget(Const.SWITCH_TAB, null, DefaultPageActivity.class);
                        break;
                    case THIRD_PARTY_LOGIN:
                        mFragment.setLoginStatus(Const.THIRD_PARTY_LOGIN_SUCCESS);
                        mFragment.tvNickname.setText(mFragment.mActivity.app.loginUser.nickname);
                        ImageLoader.getInstance().displayImage(mFragment.app.loginUser.mediumAvatar,
                                mFragment.civAvatar, mFragment.mActivity.app.mOptions);
                        break;
                    case LOGIN_SUCCESS:
                        mFragment.tvNickname.setText(mFragment.mActivity.app.loginUser.nickname);
                        mFragment.tvTitle.setText(mFragment.mActivity.app.loginUser.title);
                        ImageLoader.getInstance().displayImage(mFragment.app.loginUser.mediumAvatar, mFragment.civAvatar, mFragment.mActivity.app.mOptions);
                        mFragment.setLoginStatus(String.valueOf(msg.obj));
                        break;
                    case LOGOUT_SUCCESS:
                        mFragment.tvNickname.setText(mFragment.getString(R.string.drawer_nickname));
                        mFragment.civAvatar.setImageResource(R.drawable.user_avatar);
                        mFragment.setLoginStatus(String.valueOf(msg.obj));
                        break;
                }
            }
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(Const.LOGIN_SUCCESS),
                new MessageType(Const.LOGOUT_SUCCESS),
                new MessageType(Const.THIRD_PARTY_LOGIN_SUCCESS),
                new MessageType(Const.MAIN_MENU_OPEN, source),
                new MessageType(Const.MAIN_MENU_CLOSE, source)
        };
        return messageTypes;
    }

    private void setLoginStatus(String str) {
        if (str.equals(Const.LOGOUT_SUCCESS)) {
            tvLogin.setVisibility(View.VISIBLE);
            tvNickname.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
            vItems.setVisibility(View.GONE);
            vLogin.setVisibility(View.VISIBLE);
            ivSetting.setVisibility(View.VISIBLE);
        } else {
            tvLogin.setVisibility(View.GONE);
            tvNickname.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
            vItems.setVisibility(View.VISIBLE);
            vLogin.setVisibility(View.GONE);
            ivSetting.setVisibility(View.GONE);
        }
    }

}
