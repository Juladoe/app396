package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
import com.edusoho.kuozhi.v3.entity.lesson.CourseCatalogue;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.Member;
import com.edusoho.kuozhi.v3.model.bal.Teacher;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.fragment.CourseCatalogFragment;
import com.edusoho.kuozhi.v3.ui.fragment.CourseDetailFragment;
import com.edusoho.kuozhi.v3.ui.fragment.lesson.LessonAudioPlayerFragment;
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
        if (mCourseId != null) {
            mLoading.show();
            CourseDetailModel.getCourseDetail(mCourseId,
                    new ResponseCallbackListener<CourseDetail>() {
                        @Override
                        public void onSuccess(CourseDetail data) {
                            mCourseDetail = data;
                            if (mFragments.size() >= 2 && mFragments.get(1) != null
                                    && mFragments.get(1) instanceof CourseCatalogFragment) {
                                if (mCourseDetail.getMember() == null) {
                                    ((CourseCatalogFragment) mFragments.get(1)).reFreshView(false);
                                }else{
                                    ((CourseCatalogFragment) mFragments.get(1)).reFreshView(true);
                                }
                            }
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
//            mIvGrade.setVisibility(View.VISIBLE);
//            mIvGrade2.setVisibility(View.VISIBLE);
            mTvInclass.setVisibility(View.VISIBLE);
            initViewPager();
        }
    }

    @Override
    protected void goClass() {
        app.mEngine.runNormalPlugin("NewsCourseActivity", mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(NewsCourseActivity.COURSE_ID, mCourseId);
                startIntent.putExtra(NewsCourseActivity.FROM_NAME, mCourseDetail.getCourse().title);
            }
        });
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
    protected void courseChange(CourseCatalogue.LessonsBean lesson) {

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
                "http://yinyueshiting.baidu.com/data2/music/64011738/2771611482105661128.mp3?xcode=6dc9fc7b26d1ff315fa4084c7da1aa86");
        fragment.setArguments(bundle);
        transaction.replace(R.id.fl_header_container, fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void finish() {
        super.finish();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_header_container);
        if (fragment != null && fragment instanceof LessonAudioPlayerFragment) {
            ((LessonAudioPlayerFragment) fragment).destoryService();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.hour_rlayout) {
            Fragment fragment = mFragments.get(1);
            if (fragment != null && fragment instanceof CourseCatalogFragment) {
                ((CourseCatalogFragment) fragment).reFreshView(true);
            }
        }
    }
}
