package com.edusoho.kuozhi.ui.schoolroom;

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
 * Created by JesseHuang on 15/1/6.
 * 在学课堂
 */
public class LearningRoomActivity extends ActionBarBaseActivity {
    private static String TAG = "LearningRoomActivity";
    private String mActivityTitle;
    private PagerSlidingTabStrip mPagerTab;
    private ViewPager mViewPagers;

    private final Handler mHandler = new Handler();
    private Drawable oldBackground = null;
    private int currentColor = R.color.action_bar_bg;


    public static final String TAB_TITLES = "title";
    public static final String FRAGMENT_LIST = "fragment_list";
    public static final String FRAGMENT_TITLES = "fragment_titles";
    public static final String FRAGMENT_NAME = "fragment_name";
    public static final String FRAGMENT_DATA = "fragment_data";

    private String mCurrFragmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learning_room_layout);
        initData();
        initView();
    }

    private void initView() {
        try {
            setBackMode(BACK, mActivityTitle);
            mPagerTab = (PagerSlidingTabStrip) findViewById(R.id.learning_room_pager_slide);
            mViewPagers = (ViewPager) findViewById(R.id.learning_room_viewpager);
            MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), Const.SCHOOL_ROOM_COURSE, Const.SCHOOLROOM_COURSE_FRAGMENT);
            mViewPagers.setAdapter(myPagerAdapter);
            final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                    .getDisplayMetrics());
            mViewPagers.setPageMargin(pageMargin);
            mPagerTab.setViewPager(mViewPagers);

            changeColor(currentColor);
            setPageItem(mCurrFragmentName);
            mViewPagers.setOffscreenPageLimit(Const.SCHOOLROOM_COURSE_FRAGMENT.length);
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mActivityTitle = bundle.getString(Const.ACTIONBAR_TITLE);
            mCurrFragmentName = bundle.getString(FRAGMENT_NAME);
        }
    }

    private void setPageItem(String name) {
        for (int i = 0; i < Const.SCHOOLROOM_COURSE_FRAGMENT.length; i++) {
            if (Const.SCHOOLROOM_COURSE_FRAGMENT[i].equals(name)) {
                mViewPagers.setCurrentItem(i);
                return;
            }
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private String[] mTitles;
        private String[] mLists;

        public MyPagerAdapter(FragmentManager fm, String[] titles, String[] list) {
            super(fm);
            mTitles = titles;
            mLists = list;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = app.mEngine.runPluginWithFragment(mLists[i], mActivity, new PluginFragmentCallback() {
                @Override
                public void setArguments(Bundle bundle) {

                }
            });
            return fragment;
        }

        @Override
        public int getCount() {
            return mLists.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

    private void changeColor(int newColor) {
        mPagerTab.setIndicatorColor(newColor);

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
