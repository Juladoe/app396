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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.viewbadger.BadgeView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JesseHuang on 15/4/27.
 */
public class FragmentNavigationDrawer extends BaseFragment implements MessageEngine.MessageCallback {

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
    };

    private Button btnSetting;
    private Button btnFeedBack;
    private TextView tvNickname;
    private ImageView ivLogin;
    private CircleImageView civAvatar;


    private final RadioButton[] mRadioButtons = new RadioButton[mRadioIds.length];

    private final int mBadgeIds[] = {
            R.id.badge0,
            R.id.badge1,
            R.id.badge2,
    };

    private final BadgeView[] mBadges = new BadgeView[mBadgeIds.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_navigation_drawer);
        app.registMsgSource(this);
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
        ActionBar actionBar = mActivity.getSupportActionBar();
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

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
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        mActivity.app.mEngine.runNormalPlugin("SettingActivity", mContext, null);
                        break;
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
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initView() {
        for (int i = 0; i < mRadioButtons.length; i++) {
            mRadioButtons[i] = (RadioButton) getView().findViewById(mRadioIds[i]);
            mRadioButtons[i].setOnClickListener(mRadioBtnClickListener);
        }

        btnSetting = (Button) mActivity.findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(mSettingClickListener);
        btnFeedBack = (Button) mActivity.findViewById(R.id.btnFeedBack);
        btnFeedBack.setOnClickListener(mFeedBackClickListener);
        tvNickname = (TextView) mActivity.findViewById(R.id.tv_nickname);
        ivLogin = (ImageView) mActivity.findViewById(R.id.iv_login);
        ivLogin.setOnClickListener(mLoginClickListener);
        civAvatar = (CircleImageView) mActivity.findViewById(R.id.circleIcon);
    }

    View.OnClickListener mSettingClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mDrawerLayout != null) {
                mDrawerLayout.closeDrawer(mDrawerFragment);
            }
            mPosition = 3;
        }
    };

    View.OnClickListener mFeedBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mDrawerLayout != null) {
                mDrawerLayout.closeDrawer(mDrawerFragment);
            }
            mPosition = 4;
        }
    };

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

    private void selectItem(int position) {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mDrawerFragment);
        }
        mPosition = position;
        //CommonUtil.longToast(mActivity, mRadioButtons[position].getText().toString());
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

    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public static Runnable mRun = new Runnable() {
        @Override
        public void run() {
            Message msg = mHandler.obtainMessage(2);
            mHandler.sendMessage(msg);
            msg.sendToTarget();

        }
    };

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (Const.LOGIN_SUCCESS.equals(messageType.type)) {
            tvNickname.setText(mActivity.app.loginUser.nickname);
            ImageLoader.getInstance().displayImage(app.loginUser.mediumAvatar, civAvatar, mActivity.app.mOptions);
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(Const.LOGIN_SUCCESS)
        };
        return messageTypes;
    }
}
