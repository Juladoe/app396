package com.edusoho.kuozhi.clean.module.course;

import android.content.Context;
import android.content.Intent;
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
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.Member;
import com.edusoho.kuozhi.clean.module.course.progress.DialogProgress;
import com.edusoho.kuozhi.clean.widget.ESIconTextButton;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESProgressBar;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.ImChatActivity;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 2017/3/22.
 */

public class CourseProjectActivity extends AppCompatActivity implements CourseProjectContract.View {

    private static final String COURSE_PROJECT_ID = "CourseProjectId";

    private int mCourseProjectId;
    private CourseProjectContract.Presenter mPresenter;
    private CourseProjectViewPagerAdapter mAdapter;
    private Toolbar mToolbar;
    private ImageView mCourseCover;
    private View mProgressLayout;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private View mBottomView;
    private ESIconTextButton mConsult;
    private TextView mLearnTextView;
    private ESProgressBar mProgressBar;
    private ESIconView mBack;
    private ESIconView mShare;
    private ESIconView mCache;
    private ESIconView mProgressInfo;

    public static void launch(Context context, int courseProjectId) {
        Intent intent = new Intent(context, CourseProjectActivity.class);
        intent.putExtra(COURSE_PROJECT_ID, courseProjectId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_project);
        if (getIntent() != null) {
            mCourseProjectId = getIntent().getIntExtra(COURSE_PROJECT_ID, 0);
        }
        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCourseCover = (ImageView) findViewById(R.id.iv_course_cover);
        mProgressLayout = findViewById(R.id.layout_progress);
        mProgressBar = (ESProgressBar) findViewById(R.id.pb_learn_progress);
        mTabLayout = (TabLayout) findViewById(R.id.tl_task);
        mViewPager = (ViewPager) findViewById(R.id.vp_content);
        mBottomView = findViewById(R.id.tl_bottom);
        mConsult = (ESIconTextButton) findViewById(R.id.tb_consult);
        mConsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.consult();
            }
        });
        mLearnTextView = (TextView) findViewById(R.id.tv_learn);
        mProgressInfo = (ESIconView) findViewById(R.id.icon_progress_info);
        mProgressInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.showCourseProgressInfo();
            }
        });

        mLearnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.joinCourseProject(mCourseProjectId);
            }
        });

        mBack = (ESIconView) findViewById(R.id.iv_back);
        mShare = (ESIconView) findViewById(R.id.icon_share);
        mCache = (ESIconView) findViewById(R.id.icon_cache);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        ActivityUtil.setStatusBarFitsByColor(this, R.color.transparent);

        mPresenter = new CourseProjectPresenter(mCourseProjectId, this);
        mPresenter.subscribe();
    }

    @Override
    public void showCover(String imageUrl) {
        ImageLoader.getInstance().displayImage(imageUrl, mCourseCover, EdusohoApp.app.mOptions);
    }

    @Override
    public void showFragments(List<CourseProjectEnum> courseProjectModules, CourseProject courseProject) {
        mAdapter = new CourseProjectViewPagerAdapter(getSupportFragmentManager(), courseProjectModules, courseProject);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(courseProjectModules.size());
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void showBottomLayout(boolean visible) {
        mBottomView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void launchImChatWithTeacher(final CourseProject.Teacher teacher) {
        CoreEngine.create(getBaseContext()).runNormalPlugin("ImChatActivity", getApplicationContext(), new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ImChatActivity.FROM_NAME, teacher.nickname);
                startIntent.putExtra(ImChatActivity.FROM_ID, teacher.id);
                startIntent.putExtra(ImChatActivity.HEAD_IMAGE_URL, teacher.avatar);
            }
        });
    }

    @Override
    public void showCacheButton(boolean visible) {
        mCache.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showShareButton(boolean visible) {
        mShare.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void initJoinCourseLayout() {
        mTabLayout.setVisibility(View.GONE);
        mAdapter.removeFragment(CourseProjectEnum.RATE.getPosition());
        mAdapter.removeFragment(CourseProjectEnum.INFO.getPosition());
        showCacheButton(true);
        showShareButton(false);
        showBottomLayout(false);
    }

    @Override
    public void initLearnedLayout() {
        mTabLayout.setVisibility(View.GONE);
        showCacheButton(true);
        showShareButton(false);
        showBottomLayout(false);
    }

    @Override
    public void setProgressBar(int progress) {
        mProgressLayout.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(progress);
    }

    @Override
    public void launchDialogProgress(CourseLearningProgress progress, Member member) {
        DialogProgress.newInstance(progress, member).show(getSupportFragmentManager(), "DialogProgress");
    }

    private class CourseProjectViewPagerAdapter extends FragmentPagerAdapter {

        private List<CourseProjectEnum> mCourseProjectModules;
        private CourseProject mCourseProject;
        private List<Fragment> mFragments;
        private long baseId = 0;

        public CourseProjectViewPagerAdapter(FragmentManager fm, List<CourseProjectEnum> courseProjects, CourseProject courseProject) {
            super(fm);
            mFragments = new ArrayList<>();
            mCourseProjectModules = courseProjects;
            mCourseProject = courseProject;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = Fragment.instantiate(CourseProjectActivity.this, mCourseProjectModules.get(position).getModuleName());
            Bundle bundle = new Bundle();
            bundle.putSerializable(((CourseProjectFragmentListener) fragment).getBundleKey(), mCourseProject);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
            return fragment;
        }

        public void removeFragment(int position) {
            mCourseProjectModules.remove(position);
            notifyChangeInPosition(position);
            notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mCourseProjectModules.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mCourseProjectModules.get(position).getModuleTitle();
        }
    }
}
