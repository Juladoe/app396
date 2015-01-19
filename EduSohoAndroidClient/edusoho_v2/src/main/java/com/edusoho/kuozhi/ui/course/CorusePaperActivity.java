package com.edusoho.kuozhi.ui.course;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseDetailsResult;
import com.edusoho.kuozhi.model.Member;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.PayStatus;
import com.edusoho.kuozhi.model.Vip;
import com.edusoho.kuozhi.model.VipLevel;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.shard.ShareHandler;
import com.edusoho.kuozhi.shard.ShareUtil;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.fragment.course.CourseIntroductionFragment;
import com.edusoho.kuozhi.ui.fragment.course.CourseReviewFragment;
import com.edusoho.kuozhi.ui.fragment.course.CourseTeacherInfoFragment;
import com.edusoho.kuozhi.ui.fragment.course.ViewPagerBaseFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.edusoho.kuozhi.view.FixHeightViewPager;
import com.edusoho.kuozhi.view.dialog.ExitCoursePopupDialog;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import extensions.PagerSlidingTabStrip;

/**
 * Created by howzhi on 14/12/2.
 */
public class CorusePaperActivity extends ActionBarBaseActivity
        implements MessageEngine.MessageCallback {

    public static final int PAY_COURSE_SUCCESS = 0005;
    public static final int PAY_COURSE_REQUEST = 0006;

    private PagerSlidingTabStrip mTabs;
    protected FixHeightViewPager mFragmentPager;
    private RelativeLayout mCourseScrollView;
    private RelativeLayout mRootView;
    private ImageView mCoursePicView;
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
    private View mPayBtn;

    private final Handler handler = new Handler();

    private DisplayImageOptions mOptions;
    private Drawable oldBackground = null;
    private Drawable mColorDrawable = null;
    private Drawable mMarkDrawable = null;
    private int mAlpha = 0;
    private int mCourseScrollHeight;
    private int currentColor = R.color.action_bar_bg;

    private String mTitle;
    private int mCourseId;
    private String mCoursePic;
    private CourseDetailsResult mCourseDetailsResult;

    public static final String COURSE_PIC = "picture";
    public static final int RELOAD_REVIEW_INFO = 0010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.EdusohoTranslucentActionBar);
        super.onCreate(savedInstanceState);

        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
        mMarkDrawable = getResources().getDrawable(R.drawable.action_bar_bg_mark);
        setActionBarBackground();
        setContentView(R.layout.course_page_layout);

        titles = new String[]{"简介", "教师", "课程列表", "评价"};
        fragmentArrayList = new String[]{
                "CourseIntroductionFragment", "CourseTeacherInfoFragment",
                "CourseLessonsFragment", "CourseReviewFragment"
        };

        initIntentData();
        initFragmentPaper();

        app.startPlayCacheServer(mActivity);
        app.registMsgSource(this);
    }

    @Override
    public void invoke(WidgetMessage message) {
        int type = message.type.code;
        switch (type) {
            case RELOAD_REVIEW_INFO:
                loadCourse(new NormalCallback<String>() {
                    @Override
                    public void success(String object) {
                        mCourseDetailsResult = mActivity.parseJsonValue(
                                object, new TypeToken<CourseDetailsResult>() {
                                }
                        );
                        if (mCourseDetailsResult == null || mCourseDetailsResult.course == null) {
                            return;
                        }
                        Course course = mCourseDetailsResult.course;
                        mCourseStarView.setText(String.format("%.1f分", course.rating));

                        Bundle bundle = new Bundle();
                        bundle.putDouble(CourseReviewFragment.RATING, course.rating);
                        bundle.putString(CourseReviewFragment.RATING_NUM, course.ratingNum);
                        app.sendMsgToTarget(
                                CourseReviewFragment.RELOAD_INFO, bundle, CourseReviewFragment.class);
                    }
                });
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(RELOAD_REVIEW_INFO, source)
        };
        return messageTypes;
    }

    private void initIntentData() {
        Intent data = getIntent();
        if (data != null) {
            mTitle = data.getStringExtra(Const.ACTIONBAR_TITLE);
            mCoursePic = data.getStringExtra(COURSE_PIC);
            mCourseId = data.getIntExtra(Const.COURSE_ID, 0);
        }

        setBackMode(BACK, TextUtils.isEmpty(mTitle) ? "课程标题" : mTitle);
    }

    private void setActionBarBackground() {
        mColorDrawable = getResources().getDrawable(R.drawable.action_bar_bg);
        LayerDrawable ld = new LayerDrawable(new Drawable[]{mColorDrawable, mMarkDrawable});

        mColorDrawable.setAlpha(mAlpha);
        getSupportActionBar().setBackgroundDrawable(ld);
    }

    protected void initFragmentPaper() {
        mPayBtn = findViewById(R.id.course_pay_btn);
        mRootView = (RelativeLayout) findViewById(R.id.course_root_view);
        mCoursePicView = (ImageView) findViewById(R.id.course_pic);
        mCourseScrollView = (RelativeLayout) findViewById(R.id.course_scroolview);
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.course_info_column_tabs);
        mFragmentPager = (FixHeightViewPager) findViewById(R.id.course_info_column_pager);

        //mTabs.setAlpha(0.87f);
        mPayBtn.setAlpha(0.8f);
        mPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayBtnAnim(0, 135);
                showPayBtn(mPayBtn);
            }
        });
        loadCourseInfo();
    }

    private void startPayBtnAnim(int start, int end) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mPayBtn, "rotation", start, end);
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private void initPayBtn(View contentView) {
        TextView vipLearnBtn = (TextView) contentView.findViewById(R.id.course_details_vip_learnbtn);
        TextView learnBtn = (TextView) contentView.findViewById(R.id.course_details_learnbtn);

        final Course course = mCourseDetailsResult.course;
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
                if (app.loginUser == null) {
                    LoginActivity.startForResult(mActivity);
                    return;
                }
                if (course.price > 0) {
                    app.mEngine.runNormalPluginForResult(
                            "PayCourseActivity", mActivity, PAY_COURSE_REQUEST, new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    startIntent.putExtra("price", course.price);
                                    startIntent.putExtra("title", mTitle);
                                    startIntent.putExtra("payurl", mTitle);
                                    startIntent.putExtra("courseId", mCourseId);
                                }
                            });
                } else {
                    learnCourse();
                }
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
                    LoginActivity.startForResult(mActivity);
                }
            }
        });
    }

    public Course getCourse() {
        return mCourseDetailsResult.course;
    }

    /**
     * 更新课程会员信息
     */
    private void loadCouseMember(final LoadDialog loadDialog) {
        RequestUrl url = app.bindUrl(Const.COURSE_MEMBER, true);
        url.setParams(new String[]{
                "courseId", String.valueOf(mCourseId)
        });

        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                if (loadDialog != null) {
                    loadDialog.dismiss();
                }
                Member member = parseJsonValue(
                        object, new TypeToken<Member>() {
                }
                );

                Log.d(null, "Member->" + member);
                mCourseDetailsResult.member = member;
                invalidateOptionsMenu();//刷新菜单
                if (member != null) {
                    selectLessonFragment();
                }
            }
        });
    }

    /*
    学习课程
    */
    private void learnCourse() {
        final LoadDialog loadDialog = LoadDialog.create(mContext);
        loadDialog.show();

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
                        }
                );

                if (payStatus == null) {
                    loadDialog.dismiss();
                    longToast("加入学习失败！");
                    return;
                }

                if (!Const.RESULT_OK.equals(payStatus.status)) {
                    loadDialog.dismiss();
                    longToast(payStatus.message);
                    return;
                }

                //免费课程
                if (payStatus.paid) {
                    loadCouseMember(loadDialog);
                    return;
                }
                loadDialog.dismiss();
            }
        });
    }

    private void selectLessonFragment() {
        mFragmentPager.setCurrentItem(2, false);
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                hideCourseInfo();
            }
        }, SystemClock.uptimeMillis() + 240);

        if (mPayPopupWindow != null) {
            mPayPopupWindow.dismiss();
        }
        mPayBtn.setVisibility(View.GONE);
    }

    private String getVipLevelName(int level, VipLevel[] vipLevels) {
        for (VipLevel vipLevel : vipLevels) {
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
                        }
                );

                if (status) {
                    LoadDialog loadDialog = LoadDialog.create(mActivity);
                    loadDialog.show();
                    loadCouseMember(loadDialog);
                    longToast("加入学习成功!");
                }
            }
        });
    }

    private PopupWindow mPayPopupWindow;

    private void showPayBtn(View parent) {
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.course_paybtn_layout, null);
        initPayBtn(contentView);

        mPayPopupWindow = new PopupWindow(
                contentView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        mPayPopupWindow.setFocusable(true);
        mPayPopupWindow.setOutsideTouchable(true);
        mPayPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
        mPayPopupWindow.setAnimationStyle(R.style.PopWindowAnimationShow);
        mPayPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                startPayBtnAnim(135, 0);
            }
        });

        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        contentView.measure(0, 0);
        mPayPopupWindow.showAtLocation(
                parent, Gravity.LEFT | Gravity.TOP,
                location[0] - contentView.getMeasuredWidth() + 16,
                location[1] - contentView.getMeasuredHeight() + 16
        );
    }

    private void initCourseInfo() {
        mCourseTitleView = (TextView) findViewById(R.id.course_title);
        mCourseStudentNumView = (TextView) findViewById(R.id.course_student_num);
        mCourseStarView = (TextView) findViewById(R.id.course_student_star);
        mCoursePriceView = (TextView) findViewById(R.id.course_student_price);

        Course course = mCourseDetailsResult.course;
        mCourseTitleView.setText(course.title);
        if (!"opened".equals(course.showStudentNumType)) {
            mCourseStudentNumView.setVisibility(View.GONE);
        }
        mCourseStudentNumView.setText(course.studentNum);
        mCourseStarView.setText(String.format("%.1f分", course.rating));
        mCoursePriceView.setText(course.price > 0 ? String.format("%.2f", course.price) : "免费");

        Member member = mCourseDetailsResult.member;
        if (member != null) {
            selectLessonFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_learning_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mCourseDetailsResult != null) {
            MenuItem menuItem = menu.getItem(0);
            menuItem.setIcon(mCourseDetailsResult.userFavorited
                    ? R.drawable.course_favorited_icon : R.drawable.course_favorite_icon);

            MenuItem exitItem = menu.findItem(R.id.course_details_menu_exit);
            if (exitItem != null && (mCourseDetailsResult.member == null
                    || mCourseDetailsResult.member.role == Member.Role.teacher)) {
                exitItem.setVisible(false);
            }
        } else {
            menu.findItem(R.id.course_details_menu_more).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void hideCourseInfo() {
        int height = mCourseScrollView.getHeight();
        if (height <= mActionBar.getHeight() || height < mCourseScrollHeight) {
            return;
        }
        mCourseScrollHeight = height;
        ObjectAnimator heightAnimator = ObjectAnimator.ofInt(
                new EdusohoAnimWrap(mCourseScrollView), "height", mCourseScrollHeight, mActionBar.getHeight());
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (Integer) valueAnimator.getAnimatedValue();
                if (mAlpha <= 255 && mAlpha >= 0) {
                    int t = mCourseScrollHeight - height;
                    mAlpha = t > 255 ? 255 : t;
                    setActionBarBackground();
                }
            }
        });

        heightAnimator.setDuration(320);
        //减速DecelerateInterpolator
        heightAnimator.setInterpolator(new DecelerateInterpolator());
        heightAnimator.start();
    }

    private void showCourseInfo() {
        int height = mCourseScrollView.getHeight();
        if (height > mActionBar.getHeight()) {
            return;
        }
        ObjectAnimator heightAnimator = ObjectAnimator.ofInt(
                new EdusohoAnimWrap(mCourseScrollView), "height", mActionBar.getHeight(), mCourseScrollHeight);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (Integer) valueAnimator.getAnimatedValue();
                if (mAlpha <= 255 && mAlpha >= 0) {
                    int t = mCourseScrollHeight - height;
                    mAlpha = t > 255 ? 255 : t;
                    setActionBarBackground();
                }
            }
        });

        heightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewPagerBaseFragment fragment = fragmentAdapter.getItem(
                        mFragmentPager.getCurrentItem());
                EduSohoListView listView = fragment.getListView();
                listView.scrollToPosition(0);
            }
        });

        heightAnimator.setDuration(240);
        heightAnimator.setInterpolator(new AccelerateInterpolator());
        heightAnimator.start();
    }

    private void addScrollListener() {
        final GestureDetector mGestureDetector = new GestureDetector(
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onScroll(
                            MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                        if (Math.abs(distanceY) > Math.abs(distanceX)) {
                            if (distanceY > 0) {
                                hideCourseInfo();
                            } else {
                                ViewPagerBaseFragment fragment = fragmentAdapter.getItem(
                                        mFragmentPager.getCurrentItem());
                                EduSohoListView listView = fragment.getListView();
                                if (!listView.isTop()) {
                                    return super.onScroll(e1, e2, distanceX, distanceY);
                                }
                                showCourseInfo();
                            }
                            return true;
                        }
                        return super.onScroll(e1, e2, distanceX, distanceY);
                    }
                }
        );

        //设置滑动拦截
        mFragmentPager.setInterceptTouchCallback(
                new FixHeightViewPager.TouchCallback() {
                    @Override
                    public boolean onTouchEvent(MotionEvent ev) {
                        return mGestureDetector.onTouchEvent(ev);
                    }
                }
        );
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
        loadCourse(new NormalCallback<String>() {
            @Override
            public void success(String obj) {
                parseRequestData(obj);
                mLoadView.setVisibility(View.GONE);
            }
        });
    }

    private void loadCourse(final NormalCallback<String> callback) {
        RequestUrl url = app.bindUrl(Const.COURSE, true);
        url.setParams(new String[]{
                "courseId", mCourseId + ""
        });

        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                callback.success(object);
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
                }
        );

        if (mCourseDetailsResult == null || mCourseDetailsResult.course == null) {
            longToast("加载课程信息出现错误！请尝试重新打开课程！");
            return;
        }

        invalidateOptionsMenu();
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
        mFragmentPager.setOffscreenPageLimit(fragmentArrayList.length);

        mFragmentPager.setAdapter(fragmentAdapter);
        mTabs.setViewPager(mFragmentPager);

        changeColor(currentColor);
        initCourseInfo();
        //setPagetItem(mFragmentName);
    }

    private void setFragmentPagerHeight(int i) {
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

    private void setPagetItem(String name) {
        for (int i = 0; i < fragmentArrayList.length; i++) {
            if (fragmentArrayList[i].equals(name)) {
                mFragmentPager.setCurrentItem(i, false);
                return;
            }
        }
    }

    public CourseDetailsResult getCourseResult() {
        return mCourseDetailsResult;
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

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private String[] fragments;
        private String[] titles;
        private SparseArray<ViewPagerBaseFragment> mList;

        public MyPagerAdapter(
                FragmentManager fm, String[] fragments, String[] titles) {
            super(fm);
            this.titles = titles;
            this.fragments = fragments;
            mList = new SparseArray<ViewPagerBaseFragment>();
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
        public ViewPagerBaseFragment getItem(final int position) {
            Fragment fragment = mList.get(position);
            if (fragment != null) {
                return (ViewPagerBaseFragment) fragment;
            }
            fragment = app.mEngine.runPluginWithFragment(
                    fragments[position], mActivity, new PluginFragmentCallback() {
                        @Override
                        public void setArguments(Bundle bundle) {
                            bundle.putAll(getBundle(fragments[position]));
                        }
                    }
            );
            mList.put(position, (ViewPagerBaseFragment) fragment);
            return (ViewPagerBaseFragment) fragment;
        }
    }

    private Bundle getBundle(String fragmentName) {
        Course course = mCourseDetailsResult.course;
        Bundle bundle = new Bundle();
        if (fragmentName.equals("CourseTeacherInfoFragment")) {
            bundle.putSerializable(CourseTeacherInfoFragment.IDS, course.teachers);
        } else if (fragmentName.equals("CourseIntroductionFragment")) {
            bundle.putStringArray(CourseIntroductionFragment.TITLES, new String[]{
                    "课程目标", "适应人群", "课程介绍"
            });
            bundle.putStringArray(CourseIntroductionFragment.CONTENTS, new String[]{
                    AppUtil.goalsToStr(course.goals),
                    AppUtil.audiencesToStr(course.audiences),
                    course.about
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.course_details_menu_shard) {
            shardCourse();
        } else if (id == R.id.course_details_menu_favorite) {
            if (app.loginUser == null) {
                LoginActivity.startForResult(mActivity);
                return true;
            }
            if (mCourseDetailsResult == null) {
                return true;
            }
            item.setEnabled(false);
            if (mCourseDetailsResult.userFavorited) {
                unFavoriteCourse(mCourseId, item);
            } else {
                favoriteCourse(mCourseId, item);
            }
        } else if (id == R.id.course_details_menu_coursenotice) {
            app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(Const.COURSE_ID, mCourseId);
                    startIntent.putExtra(FragmentPageActivity.FRAGMENT, "CourseNoticeFragment");
                }
            });
        } else if (id == R.id.course_details_menu_exit) {
            unLearnCourse();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 退出学习
     */
    private void unLearnCourse() {
        ExitCoursePopupDialog.create(mActivity, new ExitCoursePopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button, int position, String selStr) {
                if (button == ExitCoursePopupDialog.CANCEL) {
                    return;
                }

                RequestUrl requestUrl = app.bindUrl(Const.UN_LEARN_COURSE, true);
                requestUrl.setParams(new String[]{
                        Const.COURSE_ID, mCourseId + "",
                        "reason", selStr
                });
                mActivity.ajaxPost(requestUrl, new ResultCallback() {
                    @Override
                    public void callback(String url, String object, AjaxStatus ajaxStatus) {
                        Log.d(null, "exit course->");
                        boolean result = mActivity.parseJsonValue(
                                object, new TypeToken<Boolean>() {
                                }
                        );
                        if (result) {
                            mCourseDetailsResult.member = null;
                            showCourseInfo();
                            mFragmentPager.setCurrentItem(0, false);
                            mPayBtn.setVisibility(View.VISIBLE);
                        } else {
                            mActivity.longToast("退出学习失败");
                        }
                    }
                });
            }
        }).show();
    }

    private void favoriteCourse(int courseId, final MenuItem item) {
        RequestUrl url = app.bindUrl(Const.FAVORITE, true);
        url.setParams(new String[]{
                "courseId", String.valueOf(courseId)
        });
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                item.setEnabled(true);
                Boolean result = mActivity.parseJsonValue(object, new TypeToken<Boolean>() {
                });
                if (result) {
                    mCourseDetailsResult.userFavorited = true;
                    item.setIcon(R.drawable.course_favorited_icon);
                }
            }
        });
    }

    private void unFavoriteCourse(int courseId, final MenuItem item) {
        RequestUrl url = app.bindUrl(Const.UNFAVORITE, true);
        url.setParams(new String[]{
                "courseId", courseId + ""
        });
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                item.setEnabled(true);
                Boolean result = mActivity.parseJsonValue(
                        object, new TypeToken<Boolean>() {
                        }
                );
                if (result) {
                    mCourseDetailsResult.userFavorited = false;
                    item.setIcon(R.drawable.course_favorite_icon);
                }
            }
        });
    }

    /**
     * 分享
     */
    private void shardCourse() {

        Course course = mCourseDetailsResult.course;
        StringBuilder stringBuilder = new StringBuilder(app.schoolHost);
        stringBuilder
                .append(Const.SHARD_COURSE_URL)
                .append("?courseId=")
                .append(course.id);
        ShareUtil shareUtil = new ShareUtil(mContext);
        shareUtil.initShareParams(
                R.drawable.icon,
                course.title,
                stringBuilder.toString(),
                AppUtil.coverCourseAbout(course.about),
                AQUtility.getCacheFile(AQUtility.getCacheDir(mContext), course.largePicture).getAbsolutePath(),
                app.host
        );
        shareUtil.show(new ShareHandler() {
            @Override
            public void handler(String type) {
                //朋友圈
                int wxType = SendMessageToWX.Req.WXSceneTimeline;
                if ("Wechat".equals(type)) {
                    wxType = SendMessageToWX.Req.WXSceneSession;
                }
                shardToMM(mCourseDetailsResult.course, mContext, wxType);
            }
        });
    }

    /**
     * 分享到微信
     *
     * @param course
     * @param context
     * @param type
     * @return
     */
    private boolean shardToMM(Course course, Context context, int type) {
        String APP_ID = getResources().getString(R.string.app_id);
        IWXAPI wxApi;
        wxApi = WXAPIFactory.createWXAPI(context, APP_ID, true);
        wxApi.registerApp(APP_ID);
        WXTextObject wXTextObject = new WXTextObject();
        wXTextObject.text = "分享课程";
        WXWebpageObject wxobj = new WXWebpageObject();
        StringBuilder stringBuilder = new StringBuilder(app.schoolHost);
        stringBuilder
                .append(Const.SHARD_COURSE_URL)
                .append("?courseId=")
                .append(course.id);

        wxobj.webpageUrl = stringBuilder.toString();
        WXMediaMessage wXMediaMessage = new WXMediaMessage();
        wXMediaMessage.mediaObject = wxobj;
        wXMediaMessage.description = AppUtil.coverCourseAbout(course.about);
        wXMediaMessage.title = course.title;
        wXMediaMessage.setThumbImage(app.query.getCachedImage(mCoursePic, 99));

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = type;
        req.transaction = System.currentTimeMillis() + "";
        req.message = wXMediaMessage;
        return wxApi.sendReq(req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAY_COURSE_REQUEST && resultCode == PAY_COURSE_SUCCESS) {
            loadCouseMember(null);
            return;
        }

        if (requestCode == LoginActivity.LOGIN && resultCode == LoginActivity.OK) {
            loadCouseMember(null);
        }
    }
}
