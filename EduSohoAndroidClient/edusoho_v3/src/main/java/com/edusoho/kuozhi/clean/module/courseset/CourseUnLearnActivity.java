package com.edusoho.kuozhi.clean.module.courseset;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.Teacher;
import com.edusoho.kuozhi.v3.ui.ImChatActivity;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.view.ScrollableAppBarLayout;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import extensions.PagerSlidingTabStrip;

/**
 * Created by DF on 2017/3/21.
 */

public class CourseUnLearnActivity extends AppCompatActivity
        implements CourseUnLearnContract.View, View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    //CourseUnjoinView ;
    private View mLoadView;
    private PagerSlidingTabStrip mTabLayout;
    private ImageView mIvBackGraound;
    private TextView mTvCollectTxt;
    private ViewGroup mAddLayout;
    private ViewGroup mConsult;
    private ViewGroup mCollect;
    private TextView mBackView;
    private TextView mTvCollect;
    private TextView mTvAdd;
    private ScrollableAppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mToolBarLayout;
    private TextView mShareView;
    private LoadDialog mProcessDialog;

    private int mCourseId = 1;
    private boolean mIsFavorite = false;
    private CourseDetail mCourseDetail;
    private ViewPager mViewPager;
    private CourseUnLearnContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_unlearn);
        getWindow().setBackgroundDrawable(null);
        ActivityUtil.setStatusBarFitsByColor(this, R.color.transparent);

//        mCourseId = getIntent().getIntExtra(Const.COURSE_ID, 0);
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

    private void isJoin() {
        // TODO: 2017/3/21 判断是否已经加入计划
        initView();
        initEvent();
    }

    private void initView() {
        mLoadView = findViewById(R.id.ll_frame_load);
        mBackView = (TextView) findViewById(R.id.iv_back);
        mIvBackGraound = (ImageView) findViewById(R.id.iv_background);
        mAddLayout = (ViewGroup) findViewById(R.id.bottom_add_layout);
        mConsult = (ViewGroup) findViewById(R.id.consult_layout);
        mCollect = (ViewGroup) findViewById(R.id.collect_layout);
        mTvCollect = (TextView) findViewById(R.id.tv_collect);
        mTvCollectTxt = (TextView) findViewById(R.id.tv_collect_txt);
        mTvAdd = (TextView) findViewById(R.id.tv_add);
        mAppBarLayout = (ScrollableAppBarLayout) findViewById(R.id.app_bar);
        mToolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mShareView = (TextView) findViewById(R.id.iv_share);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        setSupportActionBar(mToolbar);
        mTabLayout.setIndicatorColor(R.color.primary_color);

        RetrofitService.init(EdusohoApp.app.host);

        mPresenter = new CourseUnLearnPresenter(mCourseId + "", this);
        mPresenter.subscribe();
    }

    @Override
    public void showFragments(String[] titleArray, String[] fragmentArray) {
        CourseUnJoinPagerAdapter courseUnJoinPagerAdapter = new CourseUnJoinPagerAdapter(
                                getSupportFragmentManager(), titleArray, fragmentArray, getIntent().getExtras());
        mViewPager.setAdapter(courseUnJoinPagerAdapter);
        mTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void newFinish(boolean isShow) {
        if (isShow) {
            CommonUtil.shortToast(getBaseContext(), getResources().getString(R.string.lesson_unexit));
        }
        finish();
    }

    private void initEvent() {
        mBackView.setOnClickListener(this);
        mShareView.setOnClickListener(this);
        mConsult.setOnClickListener(this);
        mCollect.setOnClickListener(this);
        mTvAdd.setOnClickListener(this);
    }

    private void reLoadView() {
        mIsFavorite = mCourseDetail.isUserFavorited();
        if (mIsFavorite) {
            mTvCollect.setText(getResources().getString(R.string.new_font_collected));
            mTvCollect.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
            mTvCollectTxt.setTextColor(ContextCompat.getColor(this, R.color.primary_color));
        } else {
            mTvCollect.setText(getResources().getString(R.string.new_font_collect));
            mTvCollect.setTextColor(ContextCompat.getColor(this, R.color.secondary_font_color));
            mTvCollectTxt.setTextColor(ContextCompat.getColor(this, R.color.secondary_font_color));
        }
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_course)
                .showImageOnFail(R.drawable.default_course)
                .showImageOnLoading(R.drawable.default_course)
                .build();
        if (((EdusohoApp) getApplication()).loginUser != null && ((EdusohoApp) getApplication()).loginUser.vip != null &&
                ((EdusohoApp) getApplication()).loginUser.vip.levelId >= mCourseDetail.getCourse().vipLevelId
                && mCourseDetail.getCourse().vipLevelId != 0) {
            mTvAdd.setText(R.string.txt_vip_free);
        } else {
            mTvAdd.setText(R.string.txt_add_course);
        }
        mLoadView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.iv_share) {
            mPresenter.share();
        } else if (id == R.id.collect_layout) {
            collect();
        } else if (id == R.id.consult_layout) {
            consult();
        } else if (id == R.id.tv_add) {
            add();
        }
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

    private void collect() {
        MobclickAgent.onEvent(this, "courseDetailsPage_collection");
        if (mIsFavorite) {
            CourseUtil.uncollectCourse(mCourseId, new CourseUtil.OnCollectSuccessListener() {
                @Override
                public void onCollectSuccess() {
                    mIsFavorite = false;
                    mTvCollect.setText(getResources().getString(R.string.new_font_collect));
                    mTvCollect.setTextColor(ContextCompat.getColor(CourseUnLearnActivity.this, R.color.secondary_font_color));
                    mTvCollectTxt.setTextColor(ContextCompat.getColor(CourseUnLearnActivity.this, R.color.secondary_font_color));
                }
            });
        } else {
            CourseUtil.collectCourse(mCourseId, new CourseUtil.OnCollectSuccessListener() {
                @Override
                public void onCollectSuccess() {
                    mIsFavorite = true;
                    mTvCollect.setText(getResources().getString(R.string.new_font_collected));
                    mTvCollect.setTextColor(ContextCompat.getColor(CourseUnLearnActivity.this, R.color.primary_color));
                    mTvCollectTxt.setTextColor(ContextCompat.getColor(CourseUnLearnActivity.this, R.color.primary_color));
                }
            });
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

    private void consult() {
        MobclickAgent.onEvent(this, "courseDetailsPage_consultation");
        if (((EdusohoApp) getApplication()).loginUser == null) {
            CourseUtil.notLogin();
            return;
        }
        Teacher[] teachers = mCourseDetail.getCourse().teachers;
        final Teacher teacher;
        if (teachers.length > 0) {
            teacher = teachers[0];
        } else {
            CommonUtil.shortToast(this, getResources().getString(R.string.lesson_no_teacher));
            return;
        }
        CoreEngine.create(getBaseContext()).runNormalPlugin("ImChatActivity", ((EdusohoApp) getApplication()).mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ImChatActivity.FROM_NAME, teacher.nickname);
                startIntent.putExtra(ImChatActivity.FROM_ID, teacher.id);
                startIntent.putExtra(ImChatActivity.HEAD_IMAGE_URL, teacher.avatar);
            }
        });
    }

    protected void add() {
        MobclickAgent.onEvent(this, "courseDetailsPage_joinTheCourse");
        if (!"0".equals(mCourseId)) {
            mPresenter.joinStudy(this);
        }
    }

    @Override
    public void showProcessDialog(boolean isShow) {
        if (isShow) {
            showProcessDialog();
        } else {
            hideProcesDialog();
        }
    }

    protected void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(this);
        }
        mProcessDialog.show();
    }

    protected void hideProcesDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }


    private class CourseUnJoinPagerAdapter extends FragmentPagerAdapter {

        private String[] mTitleArray;
        private String[] mFragmentArray;
        private Bundle mBundle;

        public CourseUnJoinPagerAdapter(FragmentManager fm, String[] titleArray, String[] fragmentArray, Bundle bundle) {
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
