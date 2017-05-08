package com.edusoho.kuozhi.clean.module.courseset;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.courseset.dialog.courses.SelectProjectDialog;
import com.edusoho.kuozhi.clean.module.order.confirm.ConfirmOrderActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.ImChatActivity;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.SchoolUtil;
import com.edusoho.kuozhi.v3.view.ScrollableAppBarLayout;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import extensions.PagerSlidingTabStrip;

/**
 * Created by DF on 2017/3/21.
 * 课程activity
 */

public class CourseUnLearnActivity extends BaseFinishActivity<CourseUnLearnContract.Presenter>
        implements CourseUnLearnContract.View, View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    public static final String COURSE_SET_ID = "course_set_id";

    private View mLoadView;
    private PagerSlidingTabStrip mTabLayout;
    private ImageView mIvBackGraound;
    private TextView mTvCollectTxt;
    private ViewGroup mConsult;
    private ViewGroup mCollect;
    private TextView mBackView;
    private TextView mTvCollect;
    private TextView mTvAdd;
    private ScrollableAppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mToolBarLayout;
    private TextView mShareView;
    private LoadDialog mProcessDialog;
    private ViewPager mViewPager;
    private ViewGroup mDiscountLayout;
    private TextView mDiscountName;
    private TextView mDiscountTime;
    private SelectProjectDialog mSelectDialog;

    private int mCourseSetId;
    private long mEndTime;
    private boolean mIsFavorite = false;
    private Timer mTimer;
    private CourseSet mCourseSet;
    private CourseUnLearnContract.Presenter mPresenter;

    public static void launch(Context context, int courseSetId) {
        Intent intent = new Intent(context, CourseUnLearnActivity.class);
        intent.putExtra(COURSE_SET_ID, courseSetId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_unlearn);
        getWindow().setBackgroundDrawable(null);
        ActivityUtil.setStatusBarFitsByColor(this, R.color.transparent);

        mCourseSetId = getIntent().getIntExtra(COURSE_SET_ID, 0);
        isJoin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void isJoin() {
        initView();
        mPresenter = new CourseUnLearnPresenter(mCourseSetId, this);
        mPresenter.subscribe();
    }

    private void initView() {
        mLoadView = findViewById(R.id.ll_frame_load);
        mBackView = (TextView) findViewById(R.id.iv_back);
        mIvBackGraound = (ImageView) findViewById(R.id.iv_background);
        mConsult = (ViewGroup) findViewById(R.id.consult_layout);
        mCollect = (ViewGroup) findViewById(R.id.collect_layout);
        mTvCollect = (TextView) findViewById(R.id.tv_collect);
        mTvCollectTxt = (TextView) findViewById(R.id.tv_collect_txt);
        mTvAdd = (TextView) findViewById(R.id.tv_add);
        mAppBarLayout = (ScrollableAppBarLayout) findViewById(R.id.app_bar);
        mToolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mShareView = (TextView) findViewById(R.id.iv_share);
        mDiscountLayout = (ViewGroup) findViewById(R.id.ll_limit_activities);
        mDiscountName = (TextView) findViewById(R.id.discount_activity_name);
        mDiscountTime = (TextView) findViewById(R.id.discount_activity_time);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        setSupportActionBar(mToolbar);
        mTabLayout.setIndicatorColor(R.color.primary_color);
    }

    private void initEvent() {
        mBackView.setOnClickListener(this);
        mShareView.setOnClickListener(this);
        mConsult.setOnClickListener(this);
        mCollect.setOnClickListener(this);
        mTvAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.iv_share) {
            share();
        } else if (id == R.id.collect_layout) {
            collect();
        } else if (id == R.id.consult_layout) {
            consult();
        } else if (id == R.id.tv_add) {
            MobclickAgent.onEvent(this, "courseDetailsPage_joinTheCourse");
            mPresenter.joinStudy();
        }
    }

    private void share() {
        if (mCourseSet == null) {
            return;
        }
        final ShareTool shareTool =
                new ShareTool(this
                        , SchoolUtil.getDefaultSchool(getBaseContext()).host + "/course/" + mCourseSet.id
                        , mCourseSet.title
                        , mCourseSet.summary.length() > 20 ? mCourseSet.summary.substring(0, 20) : mCourseSet.summary
                        , mCourseSet.cover.middle);
        new Handler((getApplication().getMainLooper())).post(new Runnable() {
            @Override
            public void run() {
                shareTool.shardCourse();
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int maxHeight = AppUtil.dp2px(this, 44);
        int toolbarHeight = AppUtil.dp2px(getBaseContext(), 210);
        if (toolbarHeight + i > maxHeight * 2) {
            changeToolbarStyle(false);
            return;
        }
        changeToolbarStyle(true);
    }

    private void changeToolbarStyle(boolean isTop) {
        if (isTop) {
            setToolbarLayoutBackground(ContextCompat.getColor(this, R.color.textIcons));
            mShareView.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));
            mBackView.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));
        } else {
            setToolbarLayoutBackground(ContextCompat.getColor(this, R.color.transparent));
            mShareView.setTextColor(ContextCompat.getColor(this, R.color.textIcons));
            mBackView.setTextColor(ContextCompat.getColor(this, R.color.textIcons));
        }
    }

    protected void setToolbarLayoutBackground(int color) {
        mToolBarLayout.setContentScrimColor(color);
    }

    @Override
    public void setCourseSet(CourseSet courseSet) {
        mCourseSet = courseSet;
    }

    @Override
    public void showFragments(String[] titleArray, String[] fragmentArray) {
        CourseUnJoinPagerAdapter courseUnJoinPagerAdapter = new CourseUnJoinPagerAdapter(
                getSupportFragmentManager(), titleArray, fragmentArray, getIntent().getExtras());
        mViewPager.setAdapter(courseUnJoinPagerAdapter);
        mTabLayout.setViewPager(mViewPager);
        initEvent();
    }

    @Override
    public void newFinish() {
        finish();
    }

    private void collect() {
        MobclickAgent.onEvent(this, "courseDetailsPage_collection");
        if (mIsFavorite) {
            mPresenter.cancelFavoriteCourseSet();
        } else {
            mPresenter.favoriteCourseSet();
        }
    }

    @Override
    public void showFavoriteCourseSet(boolean isFavorite) {
        mIsFavorite = isFavorite;
        if (isFavorite) {
            mTvCollect.setText(getResources().getString(R.string.new_font_collected));
            mTvCollect.setTextColor(ContextCompat.getColor(CourseUnLearnActivity.this, R.color.primary_color));
            mTvCollectTxt.setTextColor(ContextCompat.getColor(CourseUnLearnActivity.this, R.color.primary_color));
            showToast(R.string.favorite_success);
        } else {
            mTvCollect.setText(getResources().getString(R.string.new_font_collect));
            mTvCollect.setTextColor(ContextCompat.getColor(CourseUnLearnActivity.this, R.color.secondary_font_color));
            mTvCollectTxt.setTextColor(ContextCompat.getColor(CourseUnLearnActivity.this, R.color.secondary_font_color));
            showToast(R.string.cancel_favorite);
        }
    }

    @Override
    public void showBackGround(String img) {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_course)
                .showImageOnFail(R.drawable.default_course)
                .showImageOnLoading(R.drawable.default_course)
                .build();
        ImageLoader.getInstance().displayImage(img, mIvBackGraound, imageOptions);
    }

    @Override
    public void showDiscountInfo(String name, long time) {
        mEndTime = time;
        mDiscountLayout.setVisibility(View.VISIBLE);
        mDiscountName.setText(String.format("【%s】", name));
        mTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mEndTime > 0) {
                            mDiscountTime.setText(getTime(mEndTime));
                        } else {
                            mDiscountLayout.setVisibility(View.GONE);
                        }
                        mEndTime--;
                    }
                });
            }
        };
        mTimer.schedule(timerTask, 0, 1000);
    }

    private String getTime(long time) {
        String sTime;
        long day = time / (60 * 60 * 24);
        long hour = (time - 60 * 60 * 24 * day) / 3600;
        long minute = (time - 60 * 60 * 24 * day - 3600 * hour) / 60;
        long second = time - 60 * 60 * 24 * day - 3600 * hour - 60 * minute;
        StringBuilder sb = new StringBuilder();
        sTime = day > 0 ? (day > 9 ? day + "" : "0" + day) : "00";
        sb.append(String.format(getString(R.string.remain), sTime));
        sTime = hour > 0 ? (hour > 9 ? hour + "" : "0" + hour) : "00";
        sb.append(String.format(getString(R.string.hour), sTime));
        sTime = minute > 0 ? (minute > 9 ? minute + "" : "0" + minute) : "00";
        sb.append(String.format(getString(R.string.minute), sTime));
        sTime = second > 0 ? (second > 9 ? second + "" : "0" + second) : "00";
        sb.append(String.format(getString(R.string.second), sTime));
        return sb.toString();
    }

    private void consult() {
        MobclickAgent.onEvent(this, "courseDetailsPage_consultation");
        mPresenter.consultTeacher();
    }

    @Override
    public void showFavorite(boolean isFavorite) {
        mIsFavorite = isFavorite;
        if (mIsFavorite) {
            mTvCollect.setText(getResources().getString(R.string.new_font_collected));
            mTvCollect.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            mTvCollectTxt.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
        } else {
            mTvCollect.setText(getResources().getString(R.string.new_font_collect));
            mTvCollect.setTextColor(ContextCompat.getColor(this, R.color.secondary_font_color));
            mTvCollectTxt.setTextColor(ContextCompat.getColor(this, R.color.secondary_font_color));
        }
    }

    @Override
    public void showProcessDialog(boolean isShow) {
        if (isShow) {
            showProcessDialog();
        } else {
            hideProcessDialog();
        }
    }

    @Override
    public void setDialogData(List<CourseProject> list) {
        if (mSelectDialog != null) {
            mSelectDialog.setData(list, null);
        }
    }

    @Override
    public void showLoadView(boolean isShow) {
        mLoadView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showPlanDialog(List<CourseProject> list, List<VipInfo> vipInfo, CourseSet courseSet) {
        if (mSelectDialog == null) {
            mSelectDialog = new SelectProjectDialog();
            mSelectDialog.setData(list, vipInfo);
        }
        mSelectDialog.show(getSupportFragmentManager(), "SelectProjectDialog");
    }

    protected void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(this);
        }
        mProcessDialog.show();
    }

    protected void hideProcessDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }

    @Override
    public void goToConfirmOrderActivity(CourseProject courseProject) {
        if (mCourseSet != null && courseProject != null) {
            ConfirmOrderActivity.launch(this, courseProject.courseSet.id, courseProject.id);
        }
    }

    @Override
    public void goToCourseProjectActivity(int courseProjectId) {
        CourseProjectActivity.launch(this, courseProjectId);
    }

    @Override
    public void goToImChatActivity(final Teacher teacher) {
        CoreEngine.create(getBaseContext()).runNormalPlugin("ImChatActivity", ((EdusohoApp) getApplication()).mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ImChatActivity.FROM_NAME, teacher.nickname);
                startIntent.putExtra(ImChatActivity.FROM_ID, teacher.id);
                startIntent.putExtra(ImChatActivity.HEAD_IMAGE_URL, teacher.avatar.middle);
            }
        });
    }

    @Override
    public void goToLoginActivity() {
        CoreEngine.create(getBaseContext()).runNormalPluginForResult("LoginActivity", this
                , 0, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LoginActivity.OK) {
            showProcessDialog();
            mPresenter.isJoinCourseSet();
        }
    }

    private class CourseUnJoinPagerAdapter extends FragmentPagerAdapter {

        private String[] mTitleArray;
        private String[] mFragmentArray;
        private Bundle mBundle;

        private CourseUnJoinPagerAdapter(FragmentManager fm, String[] titleArray, String[] fragmentArray, Bundle bundle) {
            super(fm);
            this.mBundle = bundle;
            this.mTitleArray = titleArray;
            this.mFragmentArray = fragmentArray;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = Fragment.instantiate(CourseUnLearnActivity.this, mFragmentArray[position]);
            fragment.setArguments(mBundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return mTitleArray.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleArray[position];
        }
    }
}
