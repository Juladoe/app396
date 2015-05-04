package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.readystatesoftware.viewbadger.BadgeView;

/**
 * Created by JesseHuang on 15/4/27.
 */
public class FragmentNavigationDrawer extends BaseFragment {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private View mDrawerFragment;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private final int mRadioIds[] = {
            R.id.radio0,
            R.id.radio1,
            R.id.radio2,
            R.id.radio3,
    };

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
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
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
//        for (int i = 0; i < mBadges.length; i++) {
//            mBadges[i] = (BadgeView) getView().findViewById(mBadgeIds[i]);
//
//        }
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

    private void selectItem(int position) {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mDrawerFragment);
        }
        CommonUtil.longToast(mActivity, mRadioButtons[position].getText().toString());
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
}
