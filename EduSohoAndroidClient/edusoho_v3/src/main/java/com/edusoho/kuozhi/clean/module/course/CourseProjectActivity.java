package com.edusoho.kuozhi.clean.module.course;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.utils.CommonConstant;
import com.edusoho.kuozhi.clean.widget.ESIconTextButton;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;

/**
 * Created by JesseHuang on 2017/3/22.
 */

public class CourseProjectActivity extends AppCompatActivity implements CourseProjectContract.View {

    private static final String COURSE_PROJECT_ID = "CourseProjectId";

    private String mCourseProjectId;
    private CourseProjectContract.Presenter mPresenter;
    private Toolbar mToolbar;
    private ImageView mCourseCoverImageView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private View mBottomView;
    private ESIconTextButton mConsultESIconTextButton;
    private ESIconTextButton mFavoriteESIconTextButton;
    private TextView mLearnTextView;

    public static CourseProjectActivity newInstance(String courseProjectId) {
        CourseProjectActivity activity = new CourseProjectActivity();
        Bundle bundle = new Bundle();
        bundle.putString(COURSE_PROJECT_ID, courseProjectId);
        return activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        if (getIntent() != null) {
            mCourseProjectId = getIntent().getStringExtra(COURSE_PROJECT_ID);
        }
        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCourseCoverImageView = (ImageView) findViewById(R.id.iv_course_cover);
        mTabLayout = (TabLayout) findViewById(R.id.tl_task);
        mViewPager = (ViewPager) findViewById(R.id.vp_content);
        mBottomView = findViewById(R.id.tl_bottom);
        mConsultESIconTextButton = (ESIconTextButton) findViewById(R.id.tb_consult);
        mFavoriteESIconTextButton = (ESIconTextButton) findViewById(R.id.tb_favorite);
        mLearnTextView = (TextView) findViewById(R.id.tv_learn);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        RetrofitService.init(EdusohoApp.app.host);

        mPresenter = new CourseProjectPresenter(mCourseProjectId, this);
        mPresenter.subscribe();
    }

    @Override
    public void setPresenter(CourseProjectContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showTasksCover(String imageUrl) {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void showFragments(CourseProjectEnum[] courseProjectModules, CourseProject courseProject) {
        CourseProjectViewPagerAdapter adapter = new CourseProjectViewPagerAdapter(getSupportFragmentManager(), courseProjectModules, courseProject);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private class CourseProjectViewPagerAdapter extends FragmentPagerAdapter {

        private CourseProjectEnum[] mCourseProjectModules;
        private CourseProject mCourseProject;

        public CourseProjectViewPagerAdapter(FragmentManager fm, CourseProjectEnum[] courseProjects, CourseProject courseProject) {
            super(fm);
            mCourseProjectModules = courseProjects;
            mCourseProject = courseProject;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = Fragment.instantiate(getApplicationContext(), mCourseProjectModules[position].getModuleName());
            Bundle bundle = new Bundle();
            bundle.putSerializable(((CourseProjectFragmentListener) fragment).getBundleKey(), mCourseProject);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return mCourseProjectModules.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mCourseProjectModules[position].getModuleTitle();
        }
    }
}
