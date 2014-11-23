package com.edusoho.kuozhi.ui.Message;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.Const;

import extensions.PagerSlidingTabStrip;

/**
 * Created by hby on 14/11/23.
 */
public class MessageTabActivity extends ActionBarBaseActivity {
    private PagerSlidingTabStrip mTabs;
    private ViewPager mViewPagers;

    private final Handler mHandler = new Handler();
    private Drawable oldBackground = null;
    private int currentColor = R.color.action_bar_bg;

    private String mTitle;
    private String mFragmentName;
    private String[] mTabTitles;
    private String[] mFragmentArrayList;

    public static final String TAB_TITLE = "title";
    public static final String FRAGMENT_LIST = "fragment_list";
    public static final String FRAGMENT_NAME = "fragment_name";
    public static final String FRAGMENT_DATA = "fragment_data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_tab_layout);
        initDatas();
        setBackMode(BACK, mTitle);
        initViews();
    }

    private void initDatas() {
        Intent intentData = getIntent();
        if (intentData != null) {
            mTitle = intentData.getStringExtra(Const.ACTIONBAT_TITLE);
            mTabTitles = intentData.getStringArrayExtra(TAB_TITLE);
            mFragmentArrayList = intentData.getStringArrayExtra(FRAGMENT_LIST);
            mFragmentName = intentData.getStringExtra(FRAGMENT_NAME);
        }

        if (mTabTitles == null || mFragmentArrayList == null) {
            longToast("无效列表数据！");
            return;
        }
    }

    private void initViews() {
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.message_varity_tab);
        mViewPagers = (ViewPager) findViewById(R.id.message_varity_pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), this.mTabTitles, this.mFragmentArrayList);
        mViewPagers.setAdapter(pagerAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mViewPagers.setPageMargin(pageMargin);

        changeColor(currentColor);
        setPagetItem(mFragmentName);
        mViewPagers.setOffscreenPageLimit(mFragmentArrayList.length);
    }

    private void setPagetItem(String name) {
        Log.d(null, "setPagetItem fragment->" + name);
        for (int i = 0; i < mFragmentArrayList.length; i++) {
            if (mFragmentArrayList[i].equals(name)) {
                mViewPagers.setCurrentItem(i);
                return;
            }
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private String[] mTabTitles;
        private String[] mFragmentLists;

        public MyPagerAdapter(FragmentManager fm, String[] titles, String[] fragmentLists) {
            super(fm);
            this.mTabTitles = titles;
            this.mFragmentLists = fragmentLists;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = app.mEngine.runPluginWithFragment(mFragmentLists[i], mActivity, new PluginFragmentCallback() {
                @Override
                public void setArguments(Bundle bundle) {
                    bundle.putAll(getIntent().getBundleExtra(FRAGMENT_DATA));
                }
            });
            return fragment;
        }

        @Override
        public int getCount() {
            return mTabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }

    private void changeColor(int newColor) {
        mTabs.setIndicatorColor(newColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = new ColorDrawable(0);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});

            if (oldBackground == null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                }

            } else {
                TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                }

                td.startTransition(200);
            }

            oldBackground = ld;
        }

        currentColor = newColor;
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            //getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            mHandler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            mHandler.removeCallbacks(what);
        }
    };
}
