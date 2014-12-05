package com.edusoho.kuozhi.ui.fragment.course;

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
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;

import extensions.PagerSlidingTabStrip;

/**
 * Created by howzhi on 14/12/1.
 */
public class CoursePageFragment extends BaseFragment {

    private PagerSlidingTabStrip mTabs;
    protected ViewPager mFragmentPager;
    private MyPagerAdapter fragmentAdapter;
    protected String[] fragmentArrayList;
    protected String[] titles;

    protected String mFragmentName = null;
    private final Handler handler = new Handler();

    private Drawable oldBackground = null;
    private int currentColor = R.color.action_bar_bg;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_page_layout);
        titles = new String[] { "简介",  "教师" };
        fragmentArrayList = new String[] { "CourseIntroductionFragment", "CourseIntroductionFragment" };
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        initFragmentPaper(view);
    }

    protected void initFragmentPaper(View view)
    {
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.course_info_column_tabs);
        mFragmentPager = (ViewPager) view.findViewById(R.id.course_info_column_pager);
        fragmentAdapter = new MyPagerAdapter(
                mActivity.getSupportFragmentManager(), fragmentArrayList, titles);

        mTabs.setIndicatorColorResource(R.color.action_bar_bg);
        mFragmentPager.setAdapter(fragmentAdapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mFragmentPager.setPageMargin(pageMargin);
        mTabs.setViewPager(mFragmentPager);


        changeColor(currentColor);
        setPagetItem(mFragmentName);
        mFragmentPager.setOffscreenPageLimit(fragmentArrayList.length);
    }

    private void setPagetItem(String name)
    {
        Log.d(null, "setPagetItem fragment->" + name);
        for(int i=0; i < fragmentArrayList.length; i++) {
            if (fragmentArrayList[i].equals(name)) {
                mFragmentPager.setCurrentItem(i);
                return;
            }
        }
    }

    private void changeColor(int newColor)
    {
        mTabs.setIndicatorColor(newColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = new ColorDrawable(0);
            LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

            if (oldBackground == null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                }

            } else {
                TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                }

                td.startTransition(200);
            }

            oldBackground = ld;
        }

        currentColor = newColor;
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private String[] fragments;
        private String[] titles;

        public MyPagerAdapter(
                FragmentManager fm, String[] fragments, String[] titles) {
            super(fm);
            this.titles = titles;
            this.fragments = fragments;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(final int position) {
            Fragment fragment = app.mEngine.runPluginWithFragment(
                    fragments[position], mActivity, new PluginFragmentCallback() {
                        @Override
                        public void setArguments(Bundle bundle) {
                            Bundle data = getArguments();
                            bundle.putAll(data);
                        }
                    });
            Log.d(null, "fragment name->" + fragments[position]);
            return fragment;
        }

    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };
}
