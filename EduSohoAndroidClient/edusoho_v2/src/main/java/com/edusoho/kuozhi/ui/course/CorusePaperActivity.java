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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseDetailsResult;
import com.edusoho.kuozhi.model.Member;
import com.edusoho.kuozhi.model.PayStatus;
import com.edusoho.kuozhi.model.Vip;
import com.edusoho.kuozhi.model.VipLevel;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.fragment.course.CourseIntroductionFragment;
import com.edusoho.kuozhi.ui.fragment.course.CourseReviewFragment;
import com.edusoho.kuozhi.ui.fragment.course.CourseTeacherInfoFragment;
import com.edusoho.kuozhi.ui.widget.ScrollWidget;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.FixHeightViewPager;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.cordova.App;

import extensions.PagerSlidingTabStrip;

/**
 * Created by howzhi on 14/12/2.
 */
public class CorusePaperActivity extends ActionBarBaseActivity {

    private PagerSlidingTabStrip mTabs;
    protected FixHeightViewPager mFragmentPager;
    private ScrollWidget mCourseScrollView;
    private RelativeLayout mRootView;
    private ImageView mCoursePicView;
    private ViewGroup mTabsParent;
    private MyPagerAdapter fragmentAdapter;
    protected String[] fragmentArrayList;
    protected String[] titles;
    private int mTabOffset = -1;

    /**
     * Course info
    */
    private TextView mCourseTitleView;
    private TextView mCourseStudentNumView;
    private TextView mCourseStarView;
    private TextView mCoursePriceView;
    private View mPayBtn;

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
        mPayBtn = findViewById(R.id.course_pay_btn);
        mTabsParent = (ViewGroup) findViewById(R.id.course_info_column_tabs_layout);
        mRootView = (RelativeLayout) findViewById(R.id.course_root_view);
        mCoursePicView = (ImageView) findViewById(R.id.course_pic);
        mCourseScrollView = (ScrollWidget) findViewById(R.id.course_scroolview);
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.course_info_column_tabs);
        mFragmentPager = (FixHeightViewPager) findViewById(R.id.course_info_column_pager);

        mPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayBtnAnim(0, 135);
                showPayBtn(mPayBtn);
            }
        });
        loadCourseInfo();
    }

    private void startPayBtnAnim(int start, int end)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mPayBtn, "rotation", start, end);
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private void initPayBtn(View contentView)
    {
        TextView vipLearnBtn = (TextView) contentView.findViewById(R.id.course_details_vip_learnbtn);
        TextView learnBtn = (TextView) contentView.findViewById(R.id.course_details_learnbtn);

        Course course = mCourseDetailsResult.course;
        String vipLevelName = getVipLevelName(course.vipLevelId, mCourseDetailsResult.vipLevels);

        if (course.price <= 0) {
            learnBtn.setText("加入学习");
        }
        if (course.vipLevelId != 0 && !TextUtils.isEmpty(vipLevelName)) {
            vipLearnBtn.setText(vipLevelName + "免费学");
        } else {
            vipLearnBtn.setVisibility(View.GONE);
        }

        learnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                learnCourse();
            }
        });
        vipLearnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (app.loginUser != null) {
                    Course course = mCourseDetailsResult.course;
                    Vip userVip = app.loginUser.vip;
                    if (userVip == null) {
                        longToast("不是会员！无法使用会员服务！");
                        return;
                    }
                    if (userVip.levelId < course.vipLevelId) {
                        longToast("会员等级不够！");
                        return;
                    }
                    learnCourseByVip();
                } else {
                    LoginActivity.start(mActivity);
                }
            }
        });
    }

    public Course getCourse()
    {
        return mCourseDetailsResult.course;
    }

    /*
    学习课程
    */
    private void learnCourse() {
        RequestUrl url = app.bindUrl(Const.PAYCOURSE, true);
        url.setParams(new String[]{
                "payment", "alipay",
                "courseId", String.valueOf(mCourseId)
        });
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                PayStatus payStatus = parseJsonValue(
                        object, new TypeToken<PayStatus>() {
                        });

                if (payStatus == null) {
                    longToast("加入学习失败！");
                    return;
                }

                if (!Const.RESULT_OK.equals(payStatus.status)) {
                    longToast(payStatus.message);
                    return;
                }

                if (payStatus.paid) {
                    //免费课程
                    selectLessonFragment();
                }
            }
        });
    }

    private void selectLessonFragment()
    {
        mFragmentPager.setCurrentItem(2);
        setFragmentPagerHeight(2);
    }

    private String getVipLevelName(int level, VipLevel[] vipLevels)
    {
        for(VipLevel vipLevel : vipLevels) {
            if (level == vipLevel.id) {
                return vipLevel.name;
            }
        }

        return "";
    }

    private void learnCourseByVip() {
        RequestUrl url = app.bindUrl(Const.VIP_LEARN_COURSE, true);
        url.setParams(new String[]{
                "courseId", mCourseId + ""
        });
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                boolean status = parseJsonValue(
                        object, new TypeToken<Boolean>() {
                        });

                if (status) {
                    longToast("加入学习成功!");
                    mPayBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showPayBtn(View parent)
    {
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.course_paybtn_layout, null);
        initPayBtn(contentView);

        PopupWindow popupWindow = new PopupWindow(
                contentView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setAnimationStyle(R.style.PopWindowAnimationShow);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                startPayBtnAnim(135, 0);
            }
        });

        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        contentView.measure(0, 0);
        popupWindow.showAtLocation(
                parent, Gravity.LEFT|Gravity.TOP,
                location[0] - contentView.getMeasuredWidth() + 16,
                location[1] - contentView.getMeasuredHeight() + 16
        );
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
        mCoursePriceView.setText(course.price > 0 ? String.format("%.1f", course.price) : "免费");

        //初始化tab组件位置
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = mTabsParent.getTop();
        layoutParams.height = mTabs.getHeight();
        mTabs.setLayoutParams(layoutParams);

        Member member = mCourseDetailsResult.member;
        if (member != null) {
            mPayBtn.setVisibility(View.GONE);
            selectLessonFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_learning_menu, menu);
        return true;
    }

    private void addScrollListener()
    {
        mCourseScrollView.setScrollChangeListener(new ScrollWidget.ScrollChangeListener() {
            @Override
            public void onBottom() {
            }

            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                Log.d(null, "t->" + t);
                if (mAlpha <= 255 && mAlpha >= 0) {
                    mAlpha = t > 255 ? 255 : t < 0 ? 0 : t;
                    setActionBarBackground();
                }

                //辅助layout的top 减去滑动组件top差距
                int top = mTabsParent.getTop() - t;
                int tabTop = mTabs.getTop();
                if (top > mActionBar.getHeight()) {
                    mTabOffset = -1;
                    setTabsLayoutParams(top);
                } else  {
                    if (tabTop != mActionBar.getHeight()) {
                        mTabOffset = t;
                        setFragmentPagerHeight(mFragmentPager.getCurrentItem());
                        setTabsLayoutParams(mActionBar.getHeight());
                    }
                }
            }
        });
    }

    private void setTabsLayoutParams(int top)
    {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTabs.getLayoutParams();
        layoutParams.topMargin = top;
        mTabs.setLayoutParams(layoutParams);
    }

    private View getLoadView() {
        View loadView = LayoutInflater.from(mContext).inflate(R.layout.page_loading_layout, null);
        return loadView;
    }

    private void loadCourseInfo() {
        final View mLoadView = getLoadView();
        mRootView.addView(
                mLoadView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        RequestUrl url = app.bindUrl(Const.COURSE, true);
        url.setParams(new String[]{
                "courseId", mCourseId + ""
        });

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

        fragmentAdapter = new MyPagerAdapter(
                mActivity.getSupportFragmentManager(), fragmentArrayList, titles);

        mTabs.setIndicatorColorResource(R.color.action_bar_bg);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mFragmentPager.setPageMargin(pageMargin);
        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                if (mTabOffset != -1) {
                    mCourseScrollView.scrollTo(0, mTabOffset);
                    setFragmentPagerHeight(i);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        //mFragmentPager.setOffscreenPageLimit(fragmentArrayList.length);

        mFragmentPager.setAdapter(fragmentAdapter);
        mTabs.setViewPager(mFragmentPager);

        changeColor(currentColor);
        initCourseInfo();
        //setPagetItem(mFragmentName);
    }

    private void setFragmentPagerHeight(int i)
    {
        int childHeight = mFragmentPager.getChildHeight(i);
        int realFragmentPagerHeight = EdusohoApp.screenH
                - mActionBar.getHeight()
                - mTabs.getHeight();

        int fragmentPagerHeight = childHeight;
        if (childHeight < realFragmentPagerHeight) {
            fragmentPagerHeight = realFragmentPagerHeight;
        }

        int oldFragmentPagerHeight = mFragmentPager.getHeight();
        if (oldFragmentPagerHeight > fragmentPagerHeight
                && (oldFragmentPagerHeight - fragmentPagerHeight) < 20) {
            return;
        }
        mFragmentPager.setIsMeasure(true);
        ViewGroup.LayoutParams layoutParams = mFragmentPager.getLayoutParams();
        layoutParams.height = fragmentPagerHeight;
        mFragmentPager.setLayoutParams(layoutParams);
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
            bundle.putDouble(CourseReviewFragment.RATING, course.rating);
            bundle.putString(CourseReviewFragment.RATING_NUM, course.ratingNum);
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
