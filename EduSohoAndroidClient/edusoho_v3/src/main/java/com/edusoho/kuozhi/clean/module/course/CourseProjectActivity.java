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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.widget.ESIconTextButton;
import com.edusoho.kuozhi.v3.core.CoreEngine;

/**
 * Created by JesseHuang on 2017/3/22.
 */

public class CourseProjectActivity extends AppCompatActivity implements CourseProjectContract.View {

    private CourseProjectContract.Presenter mPresenter;
    private Toolbar mToolbar;
    private ImageView mCourseCoverImageView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private View mBottomView;
    private ESIconTextButton mConsultESIconTextButton;
    private ESIconTextButton mFavoriteESIconTextButton;
    private TextView mLearnTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
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
        mPresenter = new CourseProjectPresenter(1, this);
        mPresenter.initFragments();
    }

    @Override
    public void showTasksCover(String imageUrl) {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setPresenter(CourseProjectContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showFragments(CourseProjectEnum[] courseProjects) {
        CourseProjectViewPagerAdapter adapter = new CourseProjectViewPagerAdapter(getSupportFragmentManager(), courseProjects);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private class CourseProjectViewPagerAdapter extends FragmentPagerAdapter {

        private CourseProjectEnum[] mCourseProjects;

        public CourseProjectViewPagerAdapter(FragmentManager fm, CourseProjectEnum[] courseProjects) {
            super(fm);
            mCourseProjects = courseProjects;
        }

        @Override
        public Fragment getItem(int position) {
            return CoreEngine.create(getApplicationContext()).runPluginWithFragment(mCourseProjects[position].getModuleName(), CourseProjectActivity.this, null);
        }

        @Override
        public int getCount() {
            return mCourseProjects.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mCourseProjects[position].getModuleTitle();
        }
    }
}
