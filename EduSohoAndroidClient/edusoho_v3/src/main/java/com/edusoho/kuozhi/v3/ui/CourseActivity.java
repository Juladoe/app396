package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.Member;
import com.edusoho.kuozhi.v3.model.bal.Teacher;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.fragment.CourseCatalogFragment;
import com.edusoho.kuozhi.v3.ui.fragment.lesson.LessonAudioPlayerFragment;
import com.edusoho.kuozhi.v3.ui.fragment.video.LessonVideoPlayerFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Zhang on 2016/12/8.
 */
public class CourseActivity extends DetailActivity implements View.OnClickListener {
    public static final String COURSE_ID = "course_id";
    private String mCourseId;
    private boolean mIsFavorite = false;
    private CourseDetail mCourseDetail;
    private LessonItem mContinueLessonItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mCourseId = intent.getStringExtra(COURSE_ID);
        if (mCourseId == null || mCourseId.trim().length() == 0) {
            finish();
            return;
        }
        mMediaViewHeight = 210;
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mTvAdd.setText(R.string.txt_add_course);
    }

    @Override
    protected void initFragment(List<Fragment> fragments) {
        Fragment detailfragment = app.mEngine.runPluginWithFragment("CourseDetailFragment", this, new PluginFragmentCallback() {
            @Override
            public void setArguments(Bundle bundle) {
                bundle.putString("id", mCourseId);
            }
        });
        fragments.add(detailfragment);
        Fragment catafragment = app.mEngine.runPluginWithFragment("CourseCatalogFragment", this, new PluginFragmentCallback() {
            @Override
            public void setArguments(Bundle bundle) {
                bundle.putString("id", mCourseId);
            }
        });
        fragments.add(catafragment);
    }

    protected void initEvent() {
        super.initEvent();
    }

    protected void initData() {
        if (TextUtils.isEmpty(mCourseId)) {
            CommonUtil.shortToast(CourseActivity.this, "课程不存在");
            finish();
            return;
        }
        setLoadStatus(View.VISIBLE);
        CourseDetailModel.getCourseDetail(mCourseId,
                new ResponseCallbackListener<CourseDetail>() {
                    @Override
                    public void onSuccess(CourseDetail data) {
                        mCourseDetail = data;
                        if (mFragments.size() >= 2 && mFragments.get(1) != null
                                && mFragments.get(1) instanceof CourseCatalogFragment) {
                            if (mCourseDetail.getMember() == null) {
                                ((CourseCatalogFragment) mFragments.get(1)).reFreshView(false);
                            } else {
                                ((CourseCatalogFragment) mFragments.get(1)).reFreshView(true);
                                tabPage(300);
                            }
                        }
                        refreshView();
                        setLoadStatus(View.GONE);
                    }

                    @Override
                    public void onFailure(String code, String message) {
                        setLoadStatus(View.GONE);
                        if ("课程不存在".equals(message)) {
                            CommonUtil.shortToast(CourseActivity.this, "课程不存在");
                            finish();
                        }
                    }
                });
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
                mIvMediaBackground, imageOptions);
        Member member = mCourseDetail.getMember();
        if (member == null) {
            mIsMemder = false;
            mBottomLayout.setVisibility(View.VISIBLE);
            mIvGrade.setVisibility(View.GONE);
            mIvGrade2.setVisibility(View.GONE);
            mTvInclass.setVisibility(View.GONE);
            initViewPager();
        } else {
            mIsMemder = true;
            mBottomLayout.setVisibility(View.GONE);
            mTvInclass.setVisibility(View.VISIBLE);
            initViewPager();
        }
        if (app.loginUser != null && app.loginUser.vip != null &&
                app.loginUser.vip.levelId >= mCourseDetail.getCourse().vipLevelId
                && mCourseDetail.getCourse().vipLevelId != 0) {
            mTvAdd.setText(R.string.txt_vip_free);
        } else {
            mTvAdd.setText(R.string.txt_add_course);
        }
    }

    @Override
    protected void goClass() {
        app.mEngine.runNormalPlugin("NewsCourseActivity", mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(NewsCourseActivity.COURSE_ID, Integer.parseInt(mCourseId));
                startIntent.putExtra(NewsCourseActivity.SHOW_TYPE, NewsCourseActivity.DISCUSS_TYPE);
                startIntent.putExtra(NewsCourseActivity.FROM_NAME, mCourseDetail.getCourse().title);
            }
        });
    }

    @Override
    protected void consult() {
        if (app.loginUser == null) {
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
        app.mEngine.runNormalPlugin("ImChatActivity", mContext, new PluginRunCallback() {
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
        if (mCourseId != null) {
            showProcessDialog();
            CourseUtil.addCourse(new CourseUtil.CourseParamsBuilder()
                            .setCouponCode("")
                            .setPayment("")
                            .setPayPassword("")
                            .setTargetId(String.valueOf(mCourseDetail.getCourse().id))
                            .setTargetType("course")
                            .setTotalPrice(String.valueOf(mCourseDetail.getCourse().price))
                    , new CourseUtil.OnAddCourseListener() {
                        @Override
                        public void onAddCourseSuccee(String response) {
                            hideProcesDialog();
                            CommonUtil.shortToast(CourseActivity.this, getResources()
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
            CourseUtil.uncollectCourse(mCourseId, new CourseUtil.OnCollectSucceeListener() {
                @Override
                public void onCollectSuccee() {
                    mIsFavorite = false;
                    mTvCollect.setText(getResources().getString(R.string.new_font_collect));
                    mTvCollect.setTextColor(getResources().getColor(R.color.secondary_font_color));
                    mTvCollectTxt.setTextColor(getResources().getColor(R.color.secondary_font_color));
                }
            });
        } else {
            CourseUtil.collectCourse(mCourseId, new CourseUtil.OnCollectSucceeListener() {
                @Override
                public void onCollectSuccee() {
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
        if (mIsPlay) {
            if (mContinueLessonItem == null) {
                return;
            }
            String shareUrl = String.format("%s/course/%s/learn#lesson/%d/", app.host, mCourseId, mContinueLessonItem.id);
            final ShareTool shareTool =
                    new ShareTool(this
                            , shareUrl
                            , mCourseDetail.getCourse().title
                            , mContinueLessonItem.title
                            , mCourseDetail.getCourse().middlePicture);
            new Handler((mActivity.getMainLooper())).post(new Runnable() {
                @Override
                public void run() {
                    shareTool.shardCourse();
                }
            });
        } else {
            final ShareTool shareTool =
                    new ShareTool(this
                            , app.host + "/course/" + mCourseDetail.getCourse().id
                            , mCourseDetail.getCourse().title
                            , mCourseDetail.getCourse().about.length() > 20 ?
                            mCourseDetail.getCourse().about.substring(0, 20)
                            : mCourseDetail.getCourse().about
                            , mCourseDetail.getCourse().middlePicture);
            new Handler((mActivity.getMainLooper())).post(new Runnable() {
                @Override
                public void run() {
                    shareTool.shardCourse();
                }
            });
        }
    }

    @Override
    protected void courseChange(LessonItem lessonItem) {
        mContinueLessonItem = lessonItem;
        coursePause();
        courseStart();
    }

    @Override
    protected void courseHastrial(String state, LessonItem lessonItem) {
        mContinueLessonItem = lessonItem;
        mPlayLastLayout.setVisibility(View.GONE);
        mPlayButtonLayout.setVisibility(View.VISIBLE);
        switch (state) {
            case Const.COURSE_CHANGE_STATE_NONE:
                mPlayLayout.setEnabled(true);
                if (mCourseDetail == null || mCourseDetail.getMember() == null) {
                    mTvPlay.setText(R.string.txt_study_try);
                    mTvPlay2.setText(R.string.txt_study_try);
                    mPlayLayout.setBackgroundResource(R.drawable.shape_play_background2);
                } else {
                    mTvPlay.setText(R.string.txt_study_start);
                    mTvPlay2.setText(R.string.txt_study_start);
                    mPlayLayout.setBackgroundResource(R.drawable.shape_play_background);
                }
                break;
            case Const.COURSE_CHANGE_STATE_STARTED:
                mTvPlay.setText(R.string.txt_study_continue);
                mTvPlay2.setText(R.string.txt_study_continue);
                mPlayLayout.setBackgroundResource(R.drawable.shape_play_background);
                mPlayLayout.setEnabled(true);
                mPlayLastLayout.setVisibility(View.VISIBLE);
                mTvLastTitle.setText(String.valueOf(lessonItem == null ? null : lessonItem.title));
                break;
            case Const.COURSE_CHANGE_STATE_FINISH:
                mTvPlay.setText(R.string.txt_study_finish);
                mTvPlay2.setText(R.string.txt_study_finish);
                mPlayLayout.setBackgroundResource(R.drawable.shape_play_background);
                mPlayLayout.setEnabled(false);
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    protected void onFragmentsFocusChange(View rootView, boolean hasFocus) {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (int i = 0; i < fragmentList.size(); i++) {
            Fragment fragment = fragmentList.get(i);
            if (fragment instanceof View.OnFocusChangeListener) {
                ((View.OnFocusChangeListener) fragment).onFocusChange(null, hasFocus);
            }
        }
    }

    @Override
    protected void coursePause() {
        super.coursePause();
        removePlayFragment();
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

    @Override
    protected void courseStart() {
        if (mContinueLessonItem == null) {
            return;
        }
        super.courseStart();
        String type = mContinueLessonItem.type;
        if ("self".equals(mContinueLessonItem.mediaSource)) {
            switch (type) {
                case "audio":
                    playAudioLesson(mContinueLessonItem);
                    return;
                case "video":
                    playVideoLesson(mContinueLessonItem);
                    return;
            }
        }

        Fragment fragment = mFragments.get(1);
        if (fragment != null && fragment instanceof CourseCatalogFragment) {
            if (mContinueLessonItem == null) {
                return;
            }
            ((CourseCatalogFragment) fragment).startLessonActivity(mContinueLessonItem.id, mContinueLessonItem.courseId);
        }
    }

    private void playVideoLesson(LessonItem lessonItem) {
        Uri uri = Uri.parse(lessonItem.mediaUri);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        LessonVideoPlayerFragment fragment = new LessonVideoPlayerFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(Const.COURSE_ID, AppUtil.parseInt(mCourseId));
        bundle.putInt(Const.LESSON_ID, lessonItem.id);
        bundle.putString(LessonVideoPlayerFragment.PLAY_URI,
                String.format("%s://%s%s", uri.getScheme(), uri.getHost(), uri.getPath()));
        fragment.setArguments(bundle);
        transaction.replace(R.id.fl_header_container, fragment);
        transaction.commitAllowingStateLoss();
    }

    private void playAudioLesson(LessonItem lessonItem) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        LessonAudioPlayerFragment fragment = new LessonAudioPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LessonAudioPlayerFragment.COVER, mCourseDetail.getCourse().largePicture);
        bundle.putString(LessonAudioPlayerFragment.PLAY_URI, lessonItem.mediaUri);
        bundle.putInt(Const.COURSE_ID, AppUtil.parseInt(mCourseId));
        bundle.putInt(Const.LESSON_ID, lessonItem.id);
        fragment.setArguments(bundle);
        transaction.replace(R.id.fl_header_container, fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void finish() {
        super.finish();
        removePlayFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LessonActivity.REQUEST_LEARN) {
            coursePause();
        }
    }

    @Override
    public MenuPop getMenu() {
        if (mContinueLessonItem != null
                && mCourseDetail.getMember() == null
                && 1 == mContinueLessonItem.free) {
            return null;
        }
        return super.getMenu();
    }
}