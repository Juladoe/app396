package com.edusoho.kuozhi.ui.course;

import android.content.Intent;
import android.graphics.Rect;
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
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseDetailsResult;
import com.edusoho.kuozhi.model.Member;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.fragment.course.CourseIntroductionFragment;
import com.edusoho.kuozhi.ui.fragment.course.CourseTeacherInfoFragment;
import com.edusoho.kuozhi.ui.widget.ScrollWidget;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.cordova.App;

import extensions.PagerSlidingTabStrip;

/**
 * Created by howzhi on 14/12/2.
 */
public class CorusePaperActivity extends ActionBarBaseActivity {

    private PagerSlidingTabStrip mTabs;
    protected ViewPager mFragmentPager;
    private ScrollWidget mCourseScrollView;
    private FrameLayout mRootView;
    private ImageView mCoursePicView;
    private ViewGroup mTabsParent;
    private MyPagerAdapter fragmentAdapter;
    protected String[] fragmentArrayList;
    protected String[] titles;

    /**
     * Course info
     */
    private TextView mCourseTitleView;
    private TextView mCourseStudentNumView;
    private TextView mCourseStarView;
    private TextView mCoursePriceView;

    protected String mFragmentName = null;
    private final Handler handler = new Handler();

    private DisplayImageOptions mOptions;
    private Drawable oldBackground = null;
    private Drawable mColorDrawable = null;
    private Drawable mMarkDrawable = null;
    private int mAlpha = 0;
    private int currentColor = R.color.action_bar_bg;

    private String mTitle;
    private int mCourseId;
    private String mCoursePic;
    private CourseDetailsResult mCourseDetailsResult;

    public static final String COURSE_PIC = "picture";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.EdusohoTranslucentActionBar);
        super.onCreate(savedInstanceState);

        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
        mMarkDrawable = getResources().getDrawable(R.drawable.action_bar_bg_mark);
        setActionBarBackground();
        setContentView(R.layout.course_page_layout);

        titles = new String[] { "简介",  "教师", "课程列表",  "评价" };
        fragmentArrayList = new String[] {
                "CourseIntroductionFragment", "CourseTeacherInfoFragment",
                "CourseLessonsFragment", "CourseReviewFragment"
        };

        initIntentData();
        initFragmentPaper();
    }

    private void initIntentData()
    {
        Intent data = getIntent();
        if (data != null) {
            mTitle = data.getStringExtra(Const.ACTIONBAT_TITLE);
            mCoursePic = data.getStringExtra(COURSE_PIC);
            mCourseId = data.getIntExtra(Const.COURSE_ID, 0);
        }

        setBackMode(BACK, TextUtils.isEmpty(mTitle) ? "课程标题" : mTitle);
    }

    private void setActionBarBackground()
    {
        mColorDrawable = getResources().getDrawable(R.drawable.action_bar_bg);
        LayerDrawable ld = new LayerDrawable(new Drawable[] { mColorDrawable, mMarkDrawable });

        mColorDrawable.setAlpha(mAlpha);
        getSupportActionBar().setBackgroundDrawable(ld);
    }

    protected void initFragmentPaper()
    {
        mTabsParent = (ViewGroup) findViewById(R.id.course_info_column_tabs_layout);
        mRootView = (FrameLayout) findViewById(R.id.course_root_view);
        mCoursePicView = (ImageView) findViewById(R.id.course_pic);
        mCourseScrollView = (ScrollWidget) findViewById(R.id.course_scroolview);
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.course_info_column_tabs);
        mFragmentPager = (ViewPager) findViewById(R.id.course_info_column_pager);

        loadCourseInfo();
    }

    private void initCourseInfo()
    {
        mCourseTitleView = (TextView) findViewById(R.id.course_title);
        mCourseStudentNumView= (TextView) findViewById(R.id.course_student_num);
        mCourseStarView = (TextView) findViewById(R.id.course_student_star);
        mCoursePriceView = (TextView) findViewById(R.id.course_student_price);

        Course course = mCourseDetailsResult.course;
        mCourseTitleView.setText(course.title);
        mCourseStudentNumView.setText(course.studentNum);
        mCourseStarView.setText(course.ratingNum);
        mCoursePriceView.setText(String.format("%.1f", course.price));
    }

    private void addScrollListener()
    {
        mCourseScrollView.setScrollChangeListener(new ScrollWidget.ScrollChangeListener() {
            @Override
            public void onBottom() {
            }

            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                if (mAlpha <= 255 && mAlpha >= 0) {
                    mAlpha = t > 255 ? 255 : t < 0 ? 0 : t;
                    Log.d(null, "mAlpha->" + mAlpha);
                    setActionBarBackground();
                }

                int[] locations = new int[2];
                mTabsParent.getLocationOnScreen(locations);

                int top = mTabsParent.getTop() - mActionBar.getHeight();
                if (t > top && mTabsParent.getChildCount() > 0) {
                    int height = mTabs.getHeight();
                    mTabsParent.removeView(mTabs);
                    mRootView.addView(mTabs);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.topMargin = mActionBar.getHeight();
                    layoutParams.height = height;
                    mTabs.setLayoutParams(layoutParams);
                } else if (t < top && mTabsParent.getChildCount() == 0) {
                    mRootView.removeView(mTabs);
                    mTabsParent.addView(mTabs);
                }
            }
        });
    }

    private View getLoadView() {
        View loadView = LayoutInflater.from(mContext).inflate(R.layout.page_loading_layout, null);
        return loadView;
    }

    private void loadCourseInfo() {
        final View mLoadView = getLoadView();
        mRootView.addView(mLoadView);

        RequestUrl url = app.bindUrl(Const.COURSE, true);
        url.setParams(new String[]{
                "courseId", mCourseId + ""
        });

        setProgressBarIndeterminateVisibility(true);
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mLoadView.setVisibility(View.GONE);
                parseRequestData(object);
            }

            @Override
            public void update(String url, String object, AjaxStatus ajaxStatus) {
            }
        });
    }

    private void loadCoursePic() {
        ImageLoader.getInstance().displayImage(mCoursePic, mCoursePicView, mOptions);
    }

    private void parseRequestData(String object) {
        addScrollListener();
        mCourseDetailsResult = mActivity.parseJsonValue(
                object, new TypeToken<CourseDetailsResult>() {
        });

        if (mCourseDetailsResult == null || mCourseDetailsResult.course == null) {
            longToast("加载课程信息出现错误！请尝试重新打开课程！");
            return;
        }

        if (TextUtils.isEmpty(mCoursePic)) {
            mCoursePic = mCourseDetailsResult.course.largePicture;
            loadCoursePic();
        }

        if (TextUtils.isEmpty(mTitle)) {
            mTitle = mCourseDetailsResult.course.title;
            setTitle(mTitle);
        }
        initCourseInfo();
        fragmentAdapter = new MyPagerAdapter(
                mActivity.getSupportFragmentManager(), fragmentArrayList, titles);

        mTabs.setIndicatorColorResource(R.color.action_bar_bg);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mFragmentPager.setPageMargin(pageMargin);
        mFragmentPager.setOffscreenPageLimit(fragmentArrayList.length);

        mFragmentPager.setAdapter(fragmentAdapter);
        mTabs.setViewPager(mFragmentPager);

        changeColor(currentColor);
        setPagetItem(mFragmentName);
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
            final Fragment fragment = app.mEngine.runPluginWithFragment(
                    fragments[position], mActivity, new PluginFragmentCallback() {
                        @Override
                        public void setArguments(Bundle bundle) {
                            Intent data = getIntent();
                            bundle.putAll(getBundle(fragments[position]));
                        }
                    });
            Log.d(null, "fragment name->" + fragments[position]);
            return fragment;
        }
    }

    private Bundle getBundle(String fragmentName)
    {
        Course course = mCourseDetailsResult.course;
        Bundle bundle = new Bundle();
        if (fragmentName.equals("CourseTeacherInfoFragment")) {
            bundle.putSerializable(CourseTeacherInfoFragment.IDS, course.teachers);
        } else if (fragmentName.equals("CourseIntroductionFragment")) {
            bundle.putStringArray(CourseIntroductionFragment.TITLES, new String[] {
                    "课程目标", "适应人群", "课程介绍"
            });
            bundle.putStringArray(CourseIntroductionFragment.CONTENTS, new String[] {
                    AppUtil.goalsToStr(course.goals),
                    AppUtil.audiencesToStr(course.audiences),
                    AppUtil.coverCourseAbout(course.about)
            });
        } else if (fragmentName.equals("CourseLessonsFragment")) {
            bundle.putInt(Const.COURSE_ID, course.id);
        } else if (fragmentName.equals("CourseReviewFragment")) {
            bundle.putInt(Const.COURSE_ID, course.id);
        }

        return bundle;
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
