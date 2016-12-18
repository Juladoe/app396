package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.Member;
import com.edusoho.kuozhi.v3.model.bal.Teacher;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.fragment.CourseCatalogFragment;
import com.edusoho.kuozhi.v3.ui.fragment.CourseDetailFragment;
import com.edusoho.kuozhi.v3.ui.fragment.lesson.LessonAudioPlayerFragment;
import com.edusoho.kuozhi.v3.ui.fragment.video.LessonVideoPlayerFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.CourseUtil;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mCourseId = intent.getStringExtra(COURSE_ID);
        if (mCourseId == null || mCourseId.trim().length() == 0) {
            finish();
        }
        mMediaViewHeight = 210;
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initFragment(List<Fragment> fragments) {
        fragments.add(new CourseDetailFragment(mCourseId));
        fragments.add(new CourseCatalogFragment(mCourseId));
    }

    protected void initEvent() {
        super.initEvent();
    }

    protected void initData() {
        if (mCourseId != null) {
            mLoading.show();
            CourseDetailModel.getCourseDetail(mCourseId,
                    new ResponseCallbackListener<CourseDetail>() {
                        @Override
                        public void onSuccess(CourseDetail data) {
                            mCourseDetail = data;
                            refreshView();
                            mLoading.dismiss();
                        }

                        @Override
                        public void onFailure(String code, String message) {
                            mLoading.dismiss();
                            if (message.equals("课程不存在")) {
                                CommonUtil.shortToast(CourseActivity.this, "课程不存在");
                                finish();
                            }
                        }
                    });
        }
    }

    @Override
    protected void refreshView() {
        if (mCourseDetail.getCourse().price == 0) {
            mTvPlay.setText("开始试学");
            mPlayLayout.setBackgroundResource(R.drawable.shape_play_background2);
        } else {
            mTvPlay.setText("开始学习");
            mPlayLayout.setBackgroundResource(R.drawable.shape_play_background2);
        }
        mIsFavorite = mCourseDetail.isUserFavorited();
        if (mIsFavorite) {
            mTvCollect.setText(getResources().getString(R.string.new_font_collected));
        } else {
            mTvCollect.setText(getResources().getString(R.string.new_font_collect));
        }
        ImageLoader.getInstance().displayImage(
                mCourseDetail.getCourse().largePicture,
                mIvMediaBackground);
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
            mIvGrade.setVisibility(View.VISIBLE);
            mIvGrade2.setVisibility(View.VISIBLE);
            mTvInclass.setVisibility(View.VISIBLE);
            initViewPager();
        }
    }

    @Override
    protected void consult() {
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
            mLoading.show();
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
                            mLoading.dismiss();
                            CommonUtil.shortToast(CourseActivity.this, getResources()
                                    .getString(R.string.success_add_course));
                            initData();
                        }

                        @Override
                        public void onAddCourseError(String error) {
                            mLoading.dismiss();
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
                    mTvCollect.setText(getResources().getString(R.string.new_font_collect));
                }
            });
        } else {
            CourseUtil.collectCourse(mCourseId, new CourseUtil.OnCollectSucceeListener() {
                @Override
                public void onCollectSuccee() {
                    mTvCollect.setText(getResources().getString(R.string.new_font_collected));
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
                        , app.host + "/course/" + mCourseDetail.getCourse().id
                        , mCourseDetail.getCourse().title
                        , mCourseDetail.getCourse().about.length() > 20 ?
                        mCourseDetail.getCourse().about.substring(0, 20)
                        : mCourseDetail.getCourse().about
                        , mCourseDetail.getCourse().largePicture);
        new Handler((mActivity.getMainLooper())).post(new Runnable() {
            @Override
            public void run() {
                shareTool.shardCourse();
            }
        });
    }

    @Override
    protected void courseChange() {

    }

    @Override
    protected void courseStart() {
        /**
         * todo 播放课程
         */
        super.courseStart();
        playVideoLesson();
    }

    private void playVideoLesson() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        LessonAudioPlayerFragment fragment = new LessonAudioPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LessonAudioPlayerFragment.COVER, mCourseDetail.getCourse().largePicture);
        bundle.putString(LessonAudioPlayerFragment.PLAY_URI,
                "http://m10.music.126.net/20161218201605/4f307645d714975eb3dd8fad65fd9bab/ymusic/baed/c724/f8bc/32453e10c844d35bc7d58762e668e3de.mp3");
        fragment.setArguments(bundle);
        transaction.replace(R.id.fl_header_container, fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_header_container);
        if (fragment != null && fragment instanceof LessonAudioPlayerFragment) {
            ((LessonAudioPlayerFragment) fragment).destoryService();
        }
        super.onDestroy();
    }
}
