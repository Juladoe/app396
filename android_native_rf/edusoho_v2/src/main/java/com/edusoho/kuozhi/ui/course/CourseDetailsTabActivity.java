package com.edusoho.kuozhi.ui.course;

import android.content.Intent;
import android.graphics.Color;
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

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.model.CourseDetailsResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.fragment.CourseInfoFragment;
import com.edusoho.kuozhi.ui.fragment.ReviewInfoFragment;
import com.edusoho.kuozhi.ui.fragment.TeacherInfoFragment;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-8-31.
 */
public class CourseDetailsTabActivity extends ActionBarBaseActivity {

    private final Handler handler = new Handler();
    private PagerSlidingTabStrip mTabs;
    private ViewPager mFragmentPager;
    private MyPagerAdapter fragmentAdapter;
    private String[] fragmentArrayList;
    private String[] titles;
    private String mFragmentName = "TeacherInfoFragment";

    private Drawable oldBackground = null;
    private int currentColor = R.color.action_bar_bg;

    public static final String FRAGMENT = "fragment";
    public static final String LIST = "list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_details_info);
        initView();
    }

    public String[] getDefaultTitles()
    {
        return new String[]{ "课程", "教师", "评价"};
    }

    public String[] getDefaultFragments()
    {
        return new String[] {
                "CourseInfoFragment",
                "TeacherInfoFragment",
                "ReviewInfoFragment"
        };
    }

    private void initView()
    {
        setBackMode(BACK, "课程详细");

        Intent data = getIntent();
        if (data != null) {
            mFragmentName = data.hasExtra(FRAGMENT) ? data.getStringExtra(FRAGMENT) : "TeacherInfoFragment";
        }

        titles = getDefaultTitles();
        fragmentArrayList = getDefaultFragments();

        mTabs = (PagerSlidingTabStrip) findViewById(R.id.course_details_info_tabs);
        mFragmentPager = (ViewPager) findViewById(R.id.course_details_info_pager);
        fragmentAdapter = new MyPagerAdapter(
                getSupportFragmentManager(), fragmentArrayList, titles);

        mTabs.setIndicatorColorResource(R.color.action_bar_bg);
        mFragmentPager.setAdapter(fragmentAdapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mFragmentPager.setPageMargin(pageMargin);
        mTabs.setViewPager(mFragmentPager);

        changeColor(currentColor);
        setPagetItem(mFragmentName);
    }

    private void setPagetItem(String name)
    {
        for(int i=0; i < fragmentArrayList.length; i++) {
            if (name.equals(fragmentArrayList[i])) {
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
                    Intent data = getIntent();
                    switch (position){
                        case 0:
                            bundle.putSerializable(CourseInfoFragment.COURSE, data.getSerializableExtra(
                                    CourseInfoFragment.COURSE));
                            break;
                        case 1:
                            bundle.putInt(TeacherInfoFragment.TEACHER_ID, data.getIntExtra(
                                    TeacherInfoFragment.TEACHER_ID, 0));
                            break;
                        case 2:
                            bundle.putString(ReviewInfoFragment.COURSE_ID, data.getStringExtra(
                                    ReviewInfoFragment.COURSE_ID));
                            break;
                    }
                }
            });
            return fragment;
        }

    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            //getActionBar().setBackgroundDrawable(who);
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
