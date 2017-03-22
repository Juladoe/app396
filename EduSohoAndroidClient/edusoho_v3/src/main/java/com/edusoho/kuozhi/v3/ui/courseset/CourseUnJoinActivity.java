package com.edusoho.kuozhi.v3.ui.courseset;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.SectionsPagerAdapter;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.Teacher;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.ImChatActivity;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.util.SchoolUtil;
import com.edusoho.kuozhi.v3.view.ScrollableAppBarLayout;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import extensions.PagerSlidingTabStrip;

/**
 * Created by DF on 2017/3/21.
 */

public class CourseUnJoinActivity extends AppCompatActivity
        implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

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
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private LoadDialog mProcessDialog;

    private int mCourseId;
    private boolean mIsFavorite = false;
    private LessonItem mContinueLessonItem;
    private CourseDetail mCourseDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_unjoin);
        getWindow().setBackgroundDrawable(null);
        ActivityUtil.setStatusBarFitsByColor(this, R.color.transparent);

        mCourseId = getIntent().getIntExtra(Const.COURSE_ID, 0);
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
        initData();
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
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        setSupportActionBar(mToolbar);
        mTabLayout.setIndicatorColor(R.color.primary_color);
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(),
                getBaseContext(),
                getTitleArray(),
                getFragmentArray(),
                getIntent().getExtras()
        );
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setViewPager(mViewPager);
    }

    private void initEvent() {
        mBackView.setOnClickListener(this);
        mShareView.setOnClickListener(this);
        mConsult.setOnClickListener(this);
        mCollect.setOnClickListener(this);
        mTvAdd.setOnClickListener(this);
    }

    private void initData() {
        if (mCourseId == 0) {
            CommonUtil.shortToast(getBaseContext(), getResources().getString(R.string.lesson_unexit));
            finish();
            return;
        }
        CourseDetailModel.getCourseDetail(mCourseId,
                new ResponseCallbackListener<CourseDetail>() {
                    @Override
                    public void onSuccess(CourseDetail data) {
                        mCourseDetail = data;
                        mAddLayout.setVisibility(data.getCourse().parentId != 0 ? View.GONE : View.VISIBLE);
                        reLoadView();
                    }

                    @Override
                    public void onFailure(String code, String message) {
                        if (message.contains("课程不存在") || message.contains("课程未发布")) {
                            CommonUtil.shortToast(CourseUnJoinActivity.this, message);
                            finish();
                        }
                    }
                });
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
        String img = mCourseDetail.getCourse().largePicture.substring(mCourseDetail.getCourse()
                .largePicture.indexOf("file"));
        String url = SchoolUtil.getDefaultSchool(getBaseContext()).host + "/" + img;
        ImageLoader.getInstance().displayImage(url, mIvBackGraound, imageOptions);
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
            share();
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
//        if (mViewPager.getCurrentItem() == 2) {
//            if (i == 0) {
//                if (((AppBarLayout.LayoutParams) mToolBarLayout.getLayoutParams()).getScrollFlags() == 0) {
//                    ((WidgtState) mSectionsPagerAdapter.getItem(2)).setTopViewVisibility(false);
//                } else {
//                    ((WidgtState) mSectionsPagerAdapter.getItem(2)).setTopViewVisibility(true);
//                }
//            } else {
//                ((WidgtState) mSectionsPagerAdapter.getItem(2)).setTopViewVisibility(false);
//            }
//        }
        int maxHeight = getResources().getDimensionPixelOffset(R.dimen.action_bar_height);
        int toolbarHeight = AppUtil.dp2px(getBaseContext(), 210);
        if (toolbarHeight + i > maxHeight * 2) {
            changeToolbarStyle(false);
            return;
        }
        changeToolbarStyle(true);
    }

    private String[] getFragmentArray() {
        return new String[]{
                "CourseIntroduceFragment", "StudyPlayFragment", "CourseEvaluateFragment"
        };
    }

    private String[] getTitleArray() {
        return new String[]{
                "简介", "计划", "评价"
        };
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
        if (this instanceof CourseUnJoinActivity.WidgtState) {
            ((CourseUnJoinActivity.WidgtState) this).setTopViewVisibility(isTop);
        }
    }

    protected void setToolbarLayoutBackground(int color) {
        mToolBarLayout.setContentScrimColor(color);
    }

    public interface WidgtState {
        void setTopViewVisibility(boolean isTop);
    }

    private void share() {
        MobclickAgent.onEvent(this, "courseDetailsPage_share");
        if (mCourseDetail == null) {
            return;
        }
        Course course = mCourseDetail.getCourse();
        final ShareTool shareTool =
                new ShareTool(this
                        , ((EdusohoApp) getApplication()).host + "/course/" + course.id
                        , course.title
                        , course.about.length() > 20 ? course.about.substring(0, 20) : course.about
                        , course.middlePicture);
        new Handler((getApplication().getMainLooper())).post(new Runnable() {
            @Override
            public void run() {
                shareTool.shardCourse();
            }
        });
    }

    private void collect() {
        MobclickAgent.onEvent(this, "courseDetailsPage_collection");
        if (mIsFavorite) {
            CourseUtil.uncollectCourse(mCourseId, new CourseUtil.OnCollectSuccessListener() {
                @Override
                public void onCollectSuccess() {
                    mIsFavorite = false;
                    mTvCollect.setText(getResources().getString(R.string.new_font_collect));
                    mTvCollect.setTextColor(ContextCompat.getColor(CourseUnJoinActivity.this, R.color.secondary_font_color));
                    mTvCollectTxt.setTextColor(ContextCompat.getColor(CourseUnJoinActivity.this, R.color.secondary_font_color));
                }
            });
        } else {
            CourseUtil.collectCourse(mCourseId, new CourseUtil.OnCollectSuccessListener() {
                @Override
                public void onCollectSuccess() {
                    mIsFavorite = true;
                    mTvCollect.setText(getResources().getString(R.string.new_font_collected));
                    mTvCollect.setTextColor(ContextCompat.getColor(CourseUnJoinActivity.this, R.color.primary_color));
                    mTvCollectTxt.setTextColor(ContextCompat.getColor(CourseUnJoinActivity.this, R.color.primary_color));
                }
            });
        }
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
        if (mCourseId != 0) {
            if (!"1".equals(mCourseDetail.getCourse().buyable)) {
                CommonUtil.shortToast(CourseUnJoinActivity.this, getResources()
                        .getString(R.string.add_error_close));
                return;
            }
            showProcessDialog();
            if (((EdusohoApp) getApplication()).loginUser != null && ((EdusohoApp) getApplication()).loginUser.vip != null
                    && ((EdusohoApp) getApplication()).loginUser.vip.levelId >= mCourseDetail.getCourse().vipLevelId
                    && mCourseDetail.getCourse().vipLevelId != 0) {
                // TODO: 2017/3/21
//                CourseUtil.addCourseVip(mCourseId, new CourseUtil.OnAddCourseListener() {
//                    @Override
//                    public void onAddCourseSuccess(String response) {
//                        hideProcesDialog();
//                        CommonUtil.shortToast(CourseStudyDetailActivity.this, getResources()
//                                .getString(R.string.success_add_course));
//                        initData();
//                    }

//                    @Override
//                    public void onAddCourseError(String response) {
//                        hideProcesDialog();
//                    }
//                });
                return;
            }
            CourseUtil.addCourse(new CourseUtil.CourseParamsBuilder()
                            .setCouponCode("")
                            .setPayment("")
                            .setPayPassword("")
                            .setTargetId(String.valueOf(mCourseDetail.getCourse().id))
                            .setTargetType("course")
                            .setTotalPrice(String.valueOf(mCourseDetail.getCourse().price))
                    , new CourseUtil.OnAddCourseListener() {
                        @Override
                        public void onAddCourseSuccess(String response) {
                            hideProcesDialog();
                            CommonUtil.shortToast(CourseUnJoinActivity.this, getResources()
                                    .getString(R.string.success_add_course));
                            initData();
                        }

                        @Override
                        public void onAddCourseError(String error) {
                            hideProcesDialog();
                        }
                    });
//            mIsJump = true;
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
}
