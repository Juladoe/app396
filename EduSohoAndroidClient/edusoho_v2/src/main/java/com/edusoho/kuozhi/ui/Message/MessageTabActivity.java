package com.edusoho.kuozhi.ui.message;

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
    private static final String TAG = "MessageTabActivity";
    private PagerSlidingTabStrip mTabs;
    private ViewPager mViewPagers;

    private final Handler mHandler = new Handler();
    private Drawable oldBackground = null;
    private int currentColor = R.color.action_bar_bg;

    private String mTitle;
    private String mFragmentName;
    private String[] mTabTitles;
    private String[] mFragmentArrayList;

    public static final String TAB_TITLES = "title";
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
            mFragmentName = intentData.getStringExtra(FRAGMENT_NAME);
            mFragmentArrayList = intentData.getStringArrayExtra(FRAGMENT_LIST);
            mTabTitles = intentData.getStringArrayExtra(TAB_TITLES);
            mTitle = intentData.getStringExtra(Const.ACTIONBAR_TITLE);
        }
//
//        if (mTabTitles == null || mFragmentArrayList == null) {
//            longToast("无效列表数据！");
//            return;
//        }
    }

    private void initViews() {
        try {
            mTabs = (PagerSlidingTabStrip) findViewById(R.id.message_varity_tab);
            mViewPagers = (ViewPager) findViewById(R.id.message_varity_pager);
            MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), Const.MESSAGE_TAB_TITLE, this.mFragmentArrayList);
            mTabs.setIndicatorColorResource(R.color.action_bar_bg);
            mViewPagers.setAdapter(pagerAdapter);

            final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                    .getDisplayMetrics());
            mViewPagers.setPageMargin(pageMargin);
            mTabs.setViewPager(mViewPagers);

            changeColor(currentColor);
            setPageItem(mFragmentName);
            mViewPagers.setOffscreenPageLimit(mFragmentArrayList.length);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }

    private void setPageItem(String name) {
        Log.d(null, "setPageItem fragment->" + name);
        for (int i = 0; i < mFragmentArrayList.length; i++) {
            if (mFragmentArrayList[i].equals(name)) {
                mViewPagers.setCurrentItem(i);
                return;
            }
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private String[] mTitles;
        private String[] mLists;

        public MyPagerAdapter(FragmentManager fm, String[] titles, String[] fragmentLists) {
            super(fm);
            this.mTitles = titles;
            this.mLists = fragmentLists;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = app.mEngine.runPluginWithFragment(mLists[i], mActivity, new PluginFragmentCallback() {
                @Override
                public void setArguments(Bundle bundle) {
                    bundle.putAll(getIntent().getBundleExtra(FRAGMENT_DATA));
                }
            });
            return fragment;
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().getFragments().get(1);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
