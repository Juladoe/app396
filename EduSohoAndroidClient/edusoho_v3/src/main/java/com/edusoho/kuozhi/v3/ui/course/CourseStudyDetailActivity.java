package com.edusoho.kuozhi.v3.ui.course;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.SectionsPagerAdapter;
import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.handler.CourseStateCallback;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.Member;
import com.edusoho.kuozhi.v3.model.bal.Teacher;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.model.provider.CourseProvider;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.BaseStudyDetailActivity;
import com.edusoho.kuozhi.v3.ui.ImChatActivity;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.NewsCourseActivity;
import com.edusoho.kuozhi.v3.ui.ReviewActivity;
import com.edusoho.kuozhi.v3.ui.ThreadCreateActivity;
import com.edusoho.kuozhi.v3.ui.fragment.CourseCatalogFragment;
import com.edusoho.kuozhi.v3.ui.fragment.CourseDetailFragment;
import com.edusoho.kuozhi.v3.ui.fragment.CourseDiscussFragment;
import com.edusoho.kuozhi.v3.ui.fragment.lesson.LessonAudioPlayerFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by suju on 17/2/7.
 */

public class CourseStudyDetailActivity extends BaseStudyDetailActivity
        implements AppBarLayout.OnOffsetChangedListener, CourseStateCallback {
    public static final String SOURCE = "source";
    public static final String IS_CHILD_COURSE = "child_course";
    public static final String COURSE_ID = "course_id";
    public CourseDetail mCourseDetail;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int mCourseId;
    private boolean mIsFavorite = false;
    private LessonItem mContinueLessonItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCourseId = getIntent().getIntExtra(Const.COURSE_ID, 0);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(),
                getBaseContext(),
                getTitleArray(),
                getFragmentArray(),
                getIntent().getExtras()
        );
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTvAdd.setText(R.string.txt_add_course);
    }

    @Override
    protected void goClass() {
        EdusohoApp.app.mEngine.runNormalPlugin("NewsCourseActivity", EdusohoApp.app.mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(NewsCourseActivity.COURSE_ID, mCourseId);
                startIntent.putExtra(NewsCourseActivity.SHOW_TYPE, NewsCourseActivity.DISCUSS_TYPE);
                startIntent.putExtra(NewsCourseActivity.FROM_NAME, mCourseDetail.getCourse().title);
            }
        });
    }

    @Override
    protected void consult() {
        if (EdusohoApp.app.loginUser == null) {
            CourseUtil.notLogin();
            return;
        }
        Teacher[] teachers = mCourseDetail.getCourse().teachers;
        final Teacher teacher;
        if (teachers.length > 0) {
            teacher = teachers[0];
        } else {
            CommonUtil.shortToast(this, "课程目前没有老师");
            return;
        }
        EdusohoApp.app.mEngine.runNormalPlugin("ImChatActivity", EdusohoApp.app.mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ImChatActivity.FROM_NAME, teacher.nickname);
                startIntent.putExtra(ImChatActivity.FROM_ID, teacher.id);
                startIntent.putExtra(ImChatActivity.HEAD_IMAGE_URL, teacher.avatar);
            }
        });
    }

    @Override
    protected void add() {
        if (mCourseId != 0) {
            if (!"1".equals(mCourseDetail.getCourse().buyable)) {
                CommonUtil.shortToast(CourseStudyDetailActivity.this, getResources()
                        .getString(R.string.add_error_close));
                return;
            }
            showProcessDialog();
            if (EdusohoApp.app.loginUser != null && EdusohoApp.app.loginUser.vip != null
                    && EdusohoApp.app.loginUser.vip.levelId >= mCourseDetail.getCourse().vipLevelId
                    && mCourseDetail.getCourse().vipLevelId != 0) {
                CourseUtil.addCourseVip(mCourseId, new CourseUtil.OnAddCourseListener() {
                    @Override
                    public void onAddCourseSuccess(String response) {
                        hideProcesDialog();
                        CommonUtil.shortToast(CourseStudyDetailActivity.this, getResources()
                                .getString(R.string.success_add_course));
                        initData();
                    }

                    @Override
                    public void onAddCourseError(String response) {
                        hideProcesDialog();
                    }
                });
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
                            CommonUtil.shortToast(CourseStudyDetailActivity.this, getResources()
                                    .getString(R.string.success_add_course));
                            initData();
                        }

                        @Override
                        public void onAddCourseError(String error) {
                            hideProcesDialog();
                        }
                    });
        }
    }

    @Override
    protected void collect() {
        if (mIsFavorite) {
            CourseUtil.uncollectCourse(mCourseId, new CourseUtil.OnCollectSuccessListener() {
                @Override
                public void onCollectSuccess() {
                    mIsFavorite = false;
                    mTvCollect.setText(getResources().getString(R.string.new_font_collect));
                    mTvCollect.setTextColor(getResources().getColor(R.color.secondary_font_color));
                    mTvCollectTxt.setTextColor(getResources().getColor(R.color.secondary_font_color));
                }
            });
        } else {
            CourseUtil.collectCourse(mCourseId, new CourseUtil.OnCollectSuccessListener() {
                @Override
                public void onCollectSuccess() {
                    mIsFavorite = true;
                    mTvCollect.setText(getResources().getString(R.string.new_font_collected));
                    mTvCollect.setTextColor(getResources().getColor(R.color.primary_color));
                    mTvCollectTxt.setTextColor(getResources().getColor(R.color.primary_color));
                }
            });
        }
    }

    @Override
    protected void share() {
        if (mCourseDetail == null) {
            return;
        }
        final ShareTool shareTool =
                new ShareTool(this
                        , EdusohoApp.app.host + "/course/" + mCourseDetail.getCourse().id
                        , mCourseDetail.getCourse().title
                        , mCourseDetail.getCourse().about.length() > 20 ?
                        mCourseDetail.getCourse().about.substring(0, 20)
                        : mCourseDetail.getCourse().about
                        , mCourseDetail.getCourse().middlePicture);
        new Handler((EdusohoApp.app.mContext.getMainLooper())).post(new Runnable() {
            @Override
            public void run() {
                shareTool.shardCourse();
            }
        });
    }

    @Override
    protected void initData() {
        if (mCourseId == 0) {
            CommonUtil.shortToast(getBaseContext(), "课程不存在");
            finish();
            return;
        }
        CourseDetailModel.getCourseDetail(mCourseId,
                new ResponseCallbackListener<CourseDetail>() {
                    @Override
                    public void onSuccess(CourseDetail data) {
                        mCourseDetail = data;
                        if (mCourseDetail.getMember() == null) {
                            ((CourseCatalogFragment) mSectionsPagerAdapter.getItem(1)).reFreshView(false);
                            ((CourseDiscussFragment) mSectionsPagerAdapter.getItem(2)).reFreshView(false);
                            setLoadStatus(View.GONE);
                        } else {
                            ((CourseCatalogFragment) mSectionsPagerAdapter.getItem(1)).reFreshView(true);
                            ((CourseDiscussFragment) mSectionsPagerAdapter.getItem(2)).reFreshView(true);
                            tabPage(300);
                        }
                        mTitle = mCourseDetail.getCourse().title;
                        refreshView();
                        if (data != null && data.getCourse() != null) {
                            saveCourseToCache(data.getCourse());
                        }
                    }

                    @Override
                    public void onFailure(String code, String message) {
                        if ("课程不存在".equals(message)) {
                            CommonUtil.shortToast(CourseStudyDetailActivity.this, "课程不存在");
                            finish();
                        }
                    }
                });
    }

    private void saveCourseToCache(Course course) {
        course.setSourceName(getIntent().getStringExtra(SOURCE));
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(getBaseContext());
        sqliteUtil.saveLocalCache(
                Const.CACHE_COURSE_TYPE,
                String.format("course-%d", course.id),
                new Gson().toJson(course)
        );
    }

    @Override
    protected void refreshView() {
        mIsFavorite = mCourseDetail.isUserFavorited();
        if (mIsFavorite) {
            mTvCollect.setText(getResources().getString(R.string.new_font_collected));
            mTvCollect.setTextColor(getResources().getColor(R.color.primary_color));
            mTvCollectTxt.setTextColor(getResources().getColor(R.color.primary_color));
        } else {
            mTvCollect.setText(getResources().getString(R.string.new_font_collect));
            mTvCollect.setTextColor(getResources().getColor(R.color.secondary_font_color));
            mTvCollectTxt.setTextColor(getResources().getColor(R.color.secondary_font_color));
        }
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_course)
                .showImageOnFail(R.drawable.default_course)
                .showImageOnLoading(R.drawable.default_course)
                .build();
        ImageLoader.getInstance().displayImage(
                mCourseDetail.getCourse().largePicture,
                mIvBackGraound, imageOptions);
        Member member = mCourseDetail.getMember();
        if (member == null) {
            mIsMemder = false;
            if (getIntent().getBooleanExtra(CourseStudyDetailActivity.IS_CHILD_COURSE, false)) {
                mBottomLayout.setVisibility(View.GONE);
            } else {
                mAddLayout.setVisibility(View.VISIBLE);
                mBottomLayout.setVisibility(View.VISIBLE);
            }
            mTvInclass.setVisibility(View.GONE);
            initViewPager();
            mIvGrade.setVisibility(View.GONE);
        } else {
            mIsMemder = true;
            mAddLayout.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.GONE);
            initViewPager();
            mIvGrade.setVisibility(View.VISIBLE);
        }
        if (EdusohoApp.app.loginUser != null && EdusohoApp.app.loginUser.vip != null &&
                EdusohoApp.app.loginUser.vip.levelId >= mCourseDetail.getCourse().vipLevelId
                && mCourseDetail.getCourse().vipLevelId != 0) {
            mTvAdd.setText(R.string.txt_vip_free);
        } else {
            mTvAdd.setText(R.string.txt_add_course);
        }
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
    public void finish() {
        super.finish();
        removePlayFragment();
        EdusohoApp.app.stopPlayCacheServer();
    }


    private void removePlayFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_header_container);
        if (fragment == null) {
            return;
        }
        if (fragment instanceof LessonAudioPlayerFragment) {
            ((LessonAudioPlayerFragment) fragment).destoryService();
        }

        transaction.remove(fragment).commitAllowingStateLoss();
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
        int toolbarHeight = AppUtil.dp2px(getBaseContext(), 260);
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
                "CourseDetailFragment", "CourseCatalogFragment", "CourseDiscussFragment"
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LessonActivity.REQUEST_LEARN) {
            coursePause();
        }
        if (requestCode == ReviewActivity.REVIEW_RESULT) {
            Fragment fragment = mSectionsPagerAdapter.getItem(0);
            if (fragment != null && fragment instanceof CourseDetailFragment) {
                ((CourseDetailFragment) fragment).refreshReview();
            }
        }
    }

    private void showCourseExpireDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EdusohoApp.app.mContext);
        builder.setTitle("提醒")
                .setMessage("课程已过期，是否重新加入?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unLearnCourse();
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void unLearnCourse() {
        new CourseProvider(getBaseContext()).unLearn(mCourseId)
                .success(new NormalCallback<String>() {
                    @Override
                    public void success(String response) {
                        if (response.equals("true")) {
                            ((CourseCatalogFragment) mSectionsPagerAdapter.getItem(1)).reFreshView(false);
                            ((CourseDiscussFragment) mSectionsPagerAdapter.getItem(2)).reFreshView(false);
                            mCourseDetail.setMember(null);
                            mIsMemder = false;
                            mAddLayout.setVisibility(View.VISIBLE);
                            mTvInclass.setVisibility(View.GONE);
                            initViewPager();
                            mViewPager.setCurrentItem(0);
                            ((CourseCatalogFragment) mSectionsPagerAdapter.getItem(1)).reFreshView(false);
                            ((CourseDiscussFragment) mSectionsPagerAdapter.getItem(2)).reFreshView(false);
                        } else {
                            CommonUtil.shortToast(EdusohoApp.app.mContext, "退出失败");
                        }
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                CommonUtil.shortToast(EdusohoApp.app.mContext, "退出失败");
            }
        });
    }

    @Override
    public boolean isExpired() {
        return mCourseDetail != null && validCourseIsExpird(mCourseDetail.getMember());
    }

    private boolean validCourseIsExpird(Member courseMember) {
        if (courseMember == null) {
            return false;
        }
        return courseMember.deadline < 0;
    }

    @Override
    protected void showThreadCreateView(String type) {
        if (mCourseDetail != null && validCourseIsExpird(mCourseDetail.getMember())) {
            showCourseExpireDlg();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(ThreadCreateActivity.TARGET_ID, mCourseId);
        bundle.putString(ThreadCreateActivity.TARGET_TYPE, "");
        bundle.putString(ThreadCreateActivity.TYPE, "question".equals(type) ? "question" : "discussion");
        bundle.putString(ThreadCreateActivity.THREAD_TYPE, "course");
        EdusohoApp.app.mEngine.runNormalPluginWithBundle("ThreadCreateActivity", this, bundle);
    }

    @Override
    public void handlerCourseExpired() {

    }
}
