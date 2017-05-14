package com.edusoho.kuozhi.clean.module.course;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskResultEnum;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum;
import com.edusoho.kuozhi.clean.module.order.confirm.ConfirmOrderActivity;
import com.edusoho.kuozhi.clean.utils.AppUtils;
import com.edusoho.kuozhi.clean.widget.ESIconTextButton;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESProgressBar;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.ImChatActivity;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.LessonDownloadingActivity;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.ui.fragment.lesson.LessonAudioPlayerFragment;
import com.edusoho.kuozhi.v3.ui.fragment.video.LessonVideoPlayerFragment;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseCacheHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private View mFragmentsLayout;
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
    private TextView mLatestTaskTitle;
    private TextView mImmediateLearn;
    private TextView mFinishTask;
    private FrameLayout mTaskPlayContainer;

    private Map<String, Fragment> mFragments;

    private CourseProjectPresenter.ShowActionHelper mShowDialogHelper;

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
        mFragmentsLayout = findViewById(R.id.layout_fragments);
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
                if (mShowDialogHelper != null && !mShowDialogHelper.isLearnClick()) {
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
                    stopAudio();
                }
            }
        });
        mPlayLayout = findViewById(R.id.rl_play_layout);
        mLatestTaskTitle = (TextView) findViewById(R.id.tv_latest_task_title);
        mImmediateLearn = (TextView) findViewById(R.id.tv_immediate_learn);
        mImmediateLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowDialogHelper != null) {
                    mShowDialogHelper.doAction();
                } else {
                    CourseTask task = (CourseTask) v.getTag();
                    mPresenter.learnTask(task);
                }
            }
        });

        mFinishTask = (TextView) findViewById(R.id.tv_finish_task);
        mFinishTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseTask task = (CourseTask) v.getTag();
                if (task.result != null && !TaskResultEnum.FINISH.toString().equals(task.result.status)) {
                    //mPresenter.finishTask(task);
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.task_container);
                    if (fragment instanceof TaskFinishListener) {
                        ((TaskFinishListener) fragment).doFinish();
                    }
                }
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    fullScreen();
                } else {
                    finish();
                }
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
    public void initTrailTask(CourseTask trialTask) {
        setPlayLayoutVisible(true);
        mLatestTaskTitle.setText(trialTask.title);
        mImmediateLearn.setText(R.string.start_learn_trial_task);
        mImmediateLearn.setBackgroundResource(R.drawable.bg_trial_learned);
        mImmediateLearn.setTag(trialTask);
    }

    @Override
    public void initNextTask(CourseTask nextTask, boolean isFirstTask) {
        setPlayLayoutVisible(true);
        mLatestTaskTitle.setText(String.format("%s %s", nextTask.toTaskItemSequence(), nextTask.title));
        mImmediateLearn.setText(isFirstTask && nextTask.result == null ? R.string.start_learn_first_task : R.string.start_learn_next_task);
        mImmediateLearn.setBackgroundResource(R.drawable.bg_latest_learned);
        mImmediateLearn.setTag(nextTask);
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
        if (mViewPager.getAdapter() == null) {
            mAdapter = new CourseProjectViewPagerAdapter(getSupportFragmentManager(), courseProjectModules, courseProject);
            mViewPager.setAdapter(mAdapter);
            mViewPager.setOffscreenPageLimit(courseProjectModules.size());
            mTabLayout.setupWithViewPager(mViewPager);
        } else {
            initJoinCourseLayout(CourseProject.LearnMode.getMode(courseProject.learnMode));
        }
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

    public boolean isJoin() {
        return mPresenter.isJoin();
    }

    @Override
    public void learnTask(CourseTask task, CourseProject courseProject, CourseMember courseMember) {
        setPlayLayoutVisible(false);
        mFinishTask.setVisibility(View.GONE);
        clearTaskFragment();
        TaskTypeEnum taskType = TaskTypeEnum.fromString(task.type);
        switch (taskType) {
            case LIVE:
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
                bundle.putSerializable(LessonActivity.COURSE_TASK, task);
                bundle.putSerializable(LessonActivity.COURSE, courseProject);
                bundle.putBoolean(LessonActivity.MEMBER_STATE, courseMember != null);
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
        LessonVideoPlayerFragment videoFragment = LessonVideoPlayerFragment.newInstance(task, mPresenter.getCourseProject());
        transaction.replace(R.id.task_container, videoFragment, FRAGMENT_VIDEO_TAG);
        transaction.commitAllowingStateLoss();
    }

    private void playAudio(CourseTask task) {
        LessonAudioPlayerFragment audioFragment = LessonAudioPlayerFragment.newInstance(mCourseCoverImageUrl, task, mPresenter.getCourseProject());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.task_container, audioFragment, FRAGMENT_AUDIO_TAG);
        transaction.commitAllowingStateLoss();
    }

    public void stopAudio() {
        LessonAudioPlayerFragment fragment = (LessonAudioPlayerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_AUDIO_TAG);
        if (fragment != null) {
            fragment.pause();
        }
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
    public void clearCoursesCache(int... courseIds) {
        AppSettingProvider appSettingProvider = FactoryManager.getInstance().create(AppSettingProvider.class);
        School school = appSettingProvider.getCurrentSchool();
        User user = appSettingProvider.getCurrentUser();
        new CourseCacheHelper(getApplicationContext(), school.getDomain(), user.id).clearLocalCacheByCourseId(courseIds);
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

    public enum JoinButtonStatusEnum {
        NORMAL, VIP_FREE, COURSE_EXPIRED
    }

    @Override
    public void setShowError(CourseProjectPresenter.ShowActionHelper helper) {
        mShowDialogHelper = helper;
    }

    private boolean mIsFullScreen;

    private void fullScreen() {
        ViewGroup.LayoutParams params = mTaskPlayContainer.getLayoutParams();
        if (!mIsFullScreen) {
            MobclickAgent.onEvent(this, "videoClassroom_fullScreen");
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mIsFullScreen = true;
            params.height = AppUtil.getWidthPx(this);
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            mTaskPlayContainer.setLayoutParams(params);
            mFragmentsLayout.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mCache.setVisibility(View.GONE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mIsFullScreen = false;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = AppUtil.dp2px(this, 222);
            mTaskPlayContainer.setLayoutParams(params);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mFragmentsLayout.setVisibility(View.VISIBLE);
            mCache.setVisibility(View.VISIBLE);
        }
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
                        mPresenter.learnTask(task);
                    }
                    break;
            }
        } else if (messageEvent.getType() == MessageEvent.SHOW_NEXT_TASK) {
            SparseArray<Object> nextTaskInfo = (SparseArray<Object>) messageEvent.getMessageBody();
            initNextTask((CourseTask) nextTaskInfo.get(0), (boolean) nextTaskInfo.get(1));
        } else if (messageEvent.getType() == MessageEvent.FULL_SCREEN) {
            fullScreen();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.LOGIN) {
            mPresenter.subscribe();
        } else if (messageEvent.getType() == MessageEvent.FINISH_TASK_SUCCESS) {
            setTaskFinishButtonBackground(true);
        }
    }

    public interface TaskFinishListener {
        void doFinish();
    }
}
