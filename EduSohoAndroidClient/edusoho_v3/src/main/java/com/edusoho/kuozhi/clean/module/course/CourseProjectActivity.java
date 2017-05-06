package com.edusoho.kuozhi.clean.module.course;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskResultEnum;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.module.course.task.catalog.TaskIconEnum;
import com.edusoho.kuozhi.clean.module.order.confirm.ConfirmOrderActivity;
import com.edusoho.kuozhi.clean.utils.AppUtils;
import com.edusoho.kuozhi.clean.widget.ESIconTextButton;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESProgressBar;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.ImChatActivity;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.LessonDownloadingActivity;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.ui.fragment.lesson.LessonAudioPlayerFragment;
import com.edusoho.kuozhi.v3.ui.fragment.video.LessonVideoPlayerFragment;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by JesseHuang on 2017/3/22.
 */

public class CourseProjectActivity extends BaseActivity<CourseProjectContract.Presenter> implements
        CourseProjectContract.View {

    private static final String COURSE_PROJECT_ID = "CourseProjectId";
    private static final String FRAGMENT_VIDEO_TAG = "video";
    private static final String FRAGMENT_AUDIO_TAG = "audio";
    private static final String HOMEWORK_CLASSNAME = "com.edusoho.kuozhi.homework.HomeworkSummaryActivity";
    private static final String EXERCISE_CLASSNAME = "com.edusoho.kuozhi.homework.ExerciseSummaryActivity";

    private int mCourseProjectId;
    private String mCourseCoverImageUrl;
    private CourseProjectContract.Presenter mPresenter;
    private CourseProjectViewPagerAdapter mAdapter;
    private Toolbar mToolbar;
    private ImageView mCourseCover;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private View mBottomView;
    private ESIconTextButton mConsult;
    private TextView mLearnTextView;
    private ESProgressBar mProgressBar;
    private ESIconView mBack;
    private ESIconView mShare;
    private ESIconView mCache;
    private View mPlayLayout;
    private TextView mLatestLearnedTitle;
    private TextView mLatestTaskTitle;
    private TextView mImmediateLearn;
    private TextView mFinishTask;
    private FrameLayout mTaskPlayContainer;

    private Map<String, Fragment> mFragments;

    private CourseProjectPresenter.ShowActionHelper mShowDialogHelper;

    private AlertDialog mCourseExpiredDialog;
    private AlertDialog mCourseMemberExpiredDialog;

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

    @Override
    protected void onDestroy() {
        clearTaskFragment();
        super.onDestroy();
    }

    private void init() {
        mTaskPlayContainer = (FrameLayout) findViewById(R.id.task_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCourseCover = (ImageView) findViewById(R.id.iv_course_cover);
        mCourseCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.exitCourse();
            }
        });
        mProgressBar = (ESProgressBar) findViewById(R.id.pb_learn_progress);
        mTabLayout = (TabLayout) findViewById(R.id.tl_task);
        mViewPager = (ViewPager) findViewById(R.id.vp_content);
        mBottomView = findViewById(R.id.tl_bottom);
        mConsult = (ESIconTextButton) findViewById(R.id.tb_consult);
        mConsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShowDialogHelper.getErrorType() == CourseProjectPresenter.ShowActionHelper.TYPE_NOT_LOGIN) {
                    mShowDialogHelper.doAction();
                } else {
                    mPresenter.consult();
                }
            }
        });
        mLearnTextView = (TextView) findViewById(R.id.tv_learn);

        mLearnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowDialogHelper != null) {
                    mShowDialogHelper.doAction();
                } else {
                    mPresenter.joinCourseProject();
                }
            }
        });

        mBack = (ESIconView) findViewById(R.id.iv_back);
        mShare = (ESIconView) findViewById(R.id.icon_share);
        mCache = (ESIconView) findViewById(R.id.icon_cache);
        mCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowDialogHelper != null) {
                    mShowDialogHelper.doAction();
                } else {
                    if (AppUtils.getRomAvailableSize(getApplicationContext()).contains("M")) {
                        if (Float.parseFloat(AppUtils.getRomAvailableSize(getApplicationContext())
                                .replaceAll("[a-zA-Z]", "").trim()) < 100) {
                            showToast(R.string.cache_hint);
                            return;
                        }
                    }
                    startActivity(new Intent(CourseProjectActivity.this, LessonDownloadingActivity.class)
                            .putExtra(Const.COURSE_ID, mCourseProjectId));
                }
            }
        });
        mPlayLayout = findViewById(R.id.rl_play_layout);
        mLatestLearnedTitle = (TextView) findViewById(R.id.tv_latest_learned_title);
        mLatestTaskTitle = (TextView) findViewById(R.id.tv_latest_task_title);
        mImmediateLearn = (TextView) findViewById(R.id.tv_immediate_learn);
        mImmediateLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowDialogHelper != null) {
                    mShowDialogHelper.doAction();
                } else {
                    // TODO: 2017/4/28 继续学习&试学
                }
            }
        });

        mFinishTask = (TextView) findViewById(R.id.tv_finish_task);
        mFinishTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseTask task = (CourseTask) v.getTag();
                if (task.result != null && !TaskResultEnum.FINISH.toString().equals(task.result.status)) {
                    mPresenter.finishTask(task);
                }
            }
        });

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
        mCourseExpiredDialog = initCourseExpiredAlertDialog();
        mCourseMemberExpiredDialog = initCourseMemberExpiredAlertDialog();
        mPresenter = new CourseProjectPresenter(mCourseProjectId, this);
        mPresenter.subscribe();
    }

    @Override
    public void initTrailTask(CourseTask trialTask) {
        setPlayLayoutVisible(true);
        mLatestLearnedTitle.setVisibility(View.GONE);
        mLatestTaskTitle.setText(trialTask.title);
        mImmediateLearn.setText(R.string.start_learn_trial_task);
    }

    @Override
    public void initNextTask(CourseTask nextTask) {
        setPlayLayoutVisible(true);
        mLatestLearnedTitle.setVisibility(View.VISIBLE);
        mLatestTaskTitle.setText(String.format("%s %s", nextTask.toTaskItemSequence(), nextTask.title));
        mImmediateLearn.setText(R.string.start_learn_next_task);
    }

    @Override
    public void setPlayLayoutVisible(boolean visible) {
        mPlayLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showCover(String imageUrl) {
        mCourseCoverImageUrl = imageUrl;
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
    public void launchImChatWithTeacher(final Teacher teacher) {
        CoreEngine.create(getBaseContext()).runNormalPlugin("ImChatActivity", getApplicationContext(), new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ImChatActivity.FROM_NAME, teacher.nickname);
                startIntent.putExtra(ImChatActivity.FROM_ID, teacher.id);
                startIntent.putExtra(ImChatActivity.HEAD_IMAGE_URL, teacher.avatar.middle);
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

    /**
     * 点击加入界面显示
     *
     * @param mode 是否解锁计划
     */
    @Override
    public void initJoinCourseLayout(CourseProject.LearnMode mode) {
        mTabLayout.setVisibility(View.GONE);
        mAdapter.destroyItem(CourseProjectEnum.RATE);
        mAdapter.destroyItem(CourseProjectEnum.INFO);
        showCacheButton(mode == CourseProject.LearnMode.FREEMODE);
        showShareButton(false);
        showBottomLayout(false);
    }

    /**
     * 退出课程
     */
    @Override
    public void exitCourseLayout() {
        mTabLayout.setVisibility(View.VISIBLE);
        mAdapter.clear();
        mAdapter.addFragment(CourseProjectEnum.INFO);
        mAdapter.addFragment(CourseProjectEnum.TASKS);
        mAdapter.addFragment(CourseProjectEnum.RATE);
        mAdapter.notifyDataSetChanged();
        showCacheButton(false);
        showShareButton(true);
        showBottomLayout(true);
    }

    /**
     * 进入页面显示：已加入
     *
     * @param mode 是否解锁计划
     */
    @Override
    public void initLearnLayout(CourseProject.LearnMode mode) {
        mTabLayout.setVisibility(View.GONE);
        showCacheButton(mode == CourseProject.LearnMode.FREEMODE);
        showShareButton(false);
        showBottomLayout(false);
    }

    /**
     * 未加入底部Layout处理
     *
     * @param statusEnum
     */
    @Override
    public void setJoinButton(JoinButtonStatusEnum statusEnum) {
        switch (statusEnum) {
            case NORMAL:
                mLearnTextView.setText(R.string.learn_course_project);
                mLearnTextView.setBackgroundResource(R.color.primary_color);
                break;
            case VIP_FREE:
                mLearnTextView.setText(R.string.learn_course_project_free_to_learn);
                mLearnTextView.setBackgroundResource(R.color.primary_color);
                break;
            case COURSE_EXPIRED:
                mLearnTextView.setText(R.string.course_closed);
                mLearnTextView.setBackgroundResource(R.color.secondary2_font_color);
                break;

        }
    }

    @Override
    public void launchConfirmOrderActivity(int courseSetId, int courseId) {
        ConfirmOrderActivity.launch(this, courseSetId, courseId);
    }

    public void showExitDialog(int msgRes, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setMessage(msgRes)
                .setPositiveButton(R.string.course_exit_confirm, onClickListener)
                .setNegativeButton(R.string.course_exit_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private AlertDialog initCourseExpiredAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setMessage(R.string.course_expired_dialog)
                .setPositiveButton(R.string.course_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.exitCourse();
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false);
        return builder.create();
    }

    private AlertDialog initCourseMemberExpiredAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setMessage(R.string.course_exit)
                .setPositiveButton(R.string.course_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.exitCourse();
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false);
        return builder.create();
    }

    public boolean isJoin() {
        return mPresenter.isJoin();
    }

    @Override
    public void onReceiveMessage(MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.LEARN_TASK) {
            CourseTask task = (CourseTask) messageEvent.getMessageBody();
            mFinishTask.setTag(task);
            switch (messageEvent.getType()) {
                case MessageEvent.LEARN_TASK:
                    if (mShowDialogHelper != null) {
                        mShowDialogHelper.doAction();
                    } else {
                        learnTask(task);
                    }
                    break;
            }
        }
    }

    private void learnTask(CourseTask task) {
        setPlayLayoutVisible(false);
        mFinishTask.setVisibility(View.GONE);
        clearTaskFragment();
        TaskIconEnum taskType = TaskIconEnum.fromString(task.type);
        switch (taskType) {
            case LIVE:
                // TODO: 2017/4/28 course2.0以前代码
                final String url = String.format(EdusohoApp.app.host + Const.WEB_LESSON, mCourseProjectId, task.id);
                CoreEngine.create(getApplicationContext()).runNormalPlugin("WebViewActivity", getApplicationContext(), new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
                break;
            case VIDEO:
                playVideo(task);
                if (task.result != null) {
                    setTaskFinishButtonBackground(TaskResultEnum.FINISH.toString().equals(task.result.status));
                } else {
                    setTaskFinishButtonBackground(false);
                }
                break;
            case AUDIO:
                playAudio(task);
                if (task.result != null) {
                    setTaskFinishButtonBackground(TaskResultEnum.FINISH.toString().equals(task.result.status));
                } else {
                    setTaskFinishButtonBackground(false);
                }
                break;
            case TEXT:
            case DOC:
            case PPT:
            case TESTPAPER:
                Bundle bundle = new Bundle();
                bundle.putInt(Const.LESSON_ID, task.id);
                bundle.putInt(Const.COURSE_ID, mCourseProjectId);
                bundle.putInt(LessonActivity.MEMBER_STATE
                        , mPresenter.getCourseMember() != null ? CourseMember.MEMBER : CourseMember.NONE);
                CoreEngine.create(getApplicationContext()).runNormalPluginWithBundleForResult(
                        "LessonActivity", this, bundle, LessonActivity.REQUEST_LEARN);
                break;
            case HOMEWORK:
                startActivity(new Intent().setClassName(getPackageName(), HOMEWORK_CLASSNAME).putExtra(Const.LESSON_ID, task.id));
                break;
            case EXERCISE:
                startActivity(new Intent().setClassName(getPackageName(), EXERCISE_CLASSNAME).putExtra(Const.LESSON_ID, task.id));
                break;
        }
    }

    private void playVideo(CourseTask task) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LessonVideoPlayerFragment videoFragment = new LessonVideoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Const.LESSON_ID, task.id);
        bundle.putString(Const.REMAINT_TIME, task.length);
        videoFragment.setArguments(bundle);
        transaction.replace(R.id.task_container, videoFragment, FRAGMENT_VIDEO_TAG);
        transaction.commitAllowingStateLoss();
    }

    private void playAudio(CourseTask task) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LessonAudioPlayerFragment audioFragment = new LessonAudioPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LessonAudioPlayerFragment.COVER, mCourseCoverImageUrl);
        bundle.putInt(Const.LESSON_ID, task.id);
        audioFragment.setArguments(bundle);
        transaction.replace(R.id.task_container, audioFragment, FRAGMENT_AUDIO_TAG);
        transaction.commitAllowingStateLoss();
    }

    private void clearTaskFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.task_container);
        if (fragment == null) {
            return;
        }
        if (fragment instanceof LessonAudioPlayerFragment) {
            ((LessonAudioPlayerFragment) fragment).destoryService();
        }

        transaction.remove(fragment).commitAllowingStateLoss();
    }

    @Override
    public void setTaskFinishButtonBackground(boolean learned) {
        mFinishTask.setVisibility(View.VISIBLE);
        if (learned) {
            mFinishTask.setCompoundDrawablesWithIntrinsicBounds(R.drawable.task_finish_left_icon, 0, 0, 0);
            mFinishTask.setBackground(getResources().getDrawable(R.drawable.task_finish_button_bg));
        } else {
            mFinishTask.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mFinishTask.setBackground(getResources().getDrawable(R.drawable.task_unfinish_button_bg));
        }
    }

    @Override
    public void setCurrentTaskStatus(TaskResultEnum status) {
        CourseTask task = (CourseTask) mFinishTask.getTag();
        if (task != null) {
            task.result.status = TaskResultEnum.FINISH.toString();
        }
    }

    @Override
    public void launchLoginActivity() {
        LoginActivity.startLogin(this);
    }

    private class CourseProjectViewPagerAdapter extends FragmentPagerAdapter {

        private List<CourseProjectEnum> mCourseProjectModules;
        private CourseProject mCourseProject;
        private FragmentManager mFragmentManager;

        public CourseProjectViewPagerAdapter(FragmentManager fm, List<CourseProjectEnum> courseProjects, CourseProject courseProject) {
            super(fm);
            mFragments = new TreeMap<>();
            mFragmentManager = fm;
            mCourseProjectModules = courseProjects;
            mCourseProject = courseProject;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = Fragment.instantiate(CourseProjectActivity.this, mCourseProjectModules.get(position).getModuleName());
            Bundle bundle = new Bundle();
            bundle.putSerializable(((CourseProjectFragmentListener) fragment).getBundleKey(), mCourseProject);
            fragment.setArguments(bundle);
            mFragments.put(mCourseProjectModules.get(position).getModuleName(), fragment);
            return fragment;
        }

        public void addFragment(CourseProjectEnum courseEnum) {
            mCourseProjectModules.add(courseEnum.getPosition(), courseEnum);
        }

        @Override
        public long getItemId(int position) {
            return position;
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

        public void clear() {
            mCourseProjectModules.clear();
            mFragmentManager.getFragments().clear();
        }

        private void destroyItem(CourseProjectEnum courseEnum) {
            try {
                for (int position = 0; position < mCourseProjectModules.size(); position++) {
                    if (mCourseProjectModules.get(position).getModuleName().equals(courseEnum.getModuleName())) {
                        this.destroyItem(null, position, mFragmentManager.getFragments().get(position));
                        mCourseProjectModules.remove(position);
                        mFragmentManager.getFragments().remove(position);
                        mFragments.remove(mCourseProjectModules.get(position).getModuleName());
                        notifyDataSetChanged();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public enum DialogType {
        COURSE_EXPIRED, COURSE_MEMBER_EXPIRED
    }

    public enum JoinButtonStatusEnum {
        NORMAL, VIP_FREE, COURSE_EXPIRED
    }

    @Override
    public void setShowError(CourseProjectPresenter.ShowActionHelper helper) {
        mShowDialogHelper = helper;
    }
}
