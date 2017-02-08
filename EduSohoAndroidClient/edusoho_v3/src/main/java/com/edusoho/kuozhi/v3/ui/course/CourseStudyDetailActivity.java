package com.edusoho.kuozhi.v3.ui.course;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.SectionsPagerAdapter;
import com.edusoho.kuozhi.v3.handler.CourseStateCallback;
import com.edusoho.kuozhi.v3.util.AppUtil;

/**
 * Created by suju on 17/2/7.
 */

public class CourseStudyDetailActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener, CourseStateCallback {

    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private TextView mShareView;
    private AppBarLayout mAppBarLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_study_layout);
        initView();
    }

    private void initView() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(),
                getBaseContext(),
                getTitleArray(),
                getFragmentArray(),
                getIntent().getExtras()
        );

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mShareView = (TextView) findViewById(R.id.iv_course_study_share);
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

    private void changeToolbarStyle(boolean isTop) {
        if (isTop) {
            mShareView.setTextColor(getResources().getColor(R.color.textPrimary));
            mToolbar.setNavigationIcon(R.drawable.action_icon_back);
        } else {
            mShareView.setTextColor(getResources().getColor(R.color.textIcons));
            mToolbar.setNavigationIcon(R.drawable.action_bar_back);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int maxHeight = getResources().getDimensionPixelOffset(R.dimen.action_bar_height);
        int toolbarHeight = AppUtil.dp2px(getBaseContext(), 210);
        if (toolbarHeight + i > maxHeight * 2) {
            changeToolbarStyle(false);
            return;
        }
        changeToolbarStyle(true);
    }

    protected String[] getTitleArray() {
        return new String [] {
                "课程", "目录", "问答"
        };
    }

    protected String[] getFragmentArray() {
        return new String [] {
                "CourseDetailFragment", "CourseCatalogFragment", "CourseDetailFragment"
        };
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public void handlerCourseExpired() {

    }
}
