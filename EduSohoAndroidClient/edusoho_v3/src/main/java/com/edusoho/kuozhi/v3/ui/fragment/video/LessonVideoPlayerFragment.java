package com.edusoho.kuozhi.v3.ui.fragment.video;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.course.dialog.TaskFinishDialog;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishHelper;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModel;
import com.edusoho.kuozhi.v3.model.provider.LessonProvider;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.BaseStudyDetailActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.MediaUtil;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.videoplayer.media.listener.SimpleVideoControllerListener;
import com.edusoho.videoplayer.ui.VideoPlayerFragment;
import com.edusoho.videoplayer.util.VLCOptions;
import com.edusoho.videoplayer.view.VideoControllerView;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.videolan.libvlc.util.AndroidUtil;

import java.util.Timer;
import java.util.TimerTask;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by suju on 16/12/16.
 */

public class LessonVideoPlayerFragment extends VideoPlayerFragment implements View.OnFocusChangeListener, CourseProjectActivity.TaskFinishListener {

    private static final String COURSE_PROJECT = "course_project";
    private static final String COURSE_TASK = "course_task";
    private static final String COURSE_ID = "course_id";

    private int mPlayTime;
    private int mTotalTime;
    private long mSaveSeekTime;
    private boolean mIsContinue;
    private boolean mIsPlay;
    private Timer mTimer;
    private BaseStudyDetailActivity mMenuCallback;
    private SharedPreferences mSeekPositionSetting;
    private static final String SEEK_POSITION = "seek_position";
    private String mRemainTime;
    private CourseTask mCourseTask;
    private CourseProject mCourseProject;
    private int mCourseId;
    private TaskFinishHelper mTaskFinishHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mCourseId = bundle.getInt(COURSE_ID);
        mCourseTask = (CourseTask) bundle.getSerializable(COURSE_TASK);
        mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT);
        if (mCourseTask == null) {
            ToastUtils.show(getActivity(), "CourseTask is null");
        }
        mRemainTime = mCourseTask.length;

        mSeekPositionSetting = getContext().getSharedPreferences(SEEK_POSITION, Context.MODE_PRIVATE);
        mSaveSeekTime = mSeekPositionSetting.getLong(String.format("%d-%d", mCourseId, mCourseTask.id), 0);

        setSeekPosition(mSaveSeekTime);
        if (mRemainTime != null) {
            startReturnData();
            startTiming();
        }
    }

    public static LessonVideoPlayerFragment newInstance(CourseTask courseTask, int courseId, CourseProject courseProject) {
        Bundle args = new Bundle();
        args.putSerializable(COURSE_TASK, courseTask);
        args.putSerializable(COURSE_PROJECT, courseProject);
        args.putSerializable(COURSE_ID, courseId);
        LessonVideoPlayerFragment fragment = new LessonVideoPlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BaseStudyDetailActivity) {
            mMenuCallback = (BaseStudyDetailActivity) activity;
        }
        int mediaCoder = MediaUtil.getMediaSupportType(getContext());
        if (mediaCoder == VLCOptions.NONE_RATE && AndroidUtil.isKitKatOrLater()) {
            mediaCoder = VLCOptions.SUPPORT_RATE;
            MediaUtil.saveMediaSupportType(getContext(), mediaCoder);
        }
        getArguments().putInt(PLAY_MEDIA_CODER, mediaCoder);
    }

    private void loadPlayUrl() {
        LessonItem cachedLesson = getCachedLesson();
        if (cachedLesson != null) {
            cachedLesson.mediaUri = String.format("http://%s:8800/playlist/%d.m3u8", "localhost", mCourseTask.id);
            playVideo(cachedLesson.mediaUri);
            return;
        }
        new LessonProvider(getContext()).getLesson(mCourseTask.id)
                .success(new NormalCallback<LessonItem>() {
                    @Override
                    public void success(LessonItem lessonItem) {
                        changeHeaderViewStatus(false);
                        if (lessonItem == null || TextUtils.isEmpty(lessonItem.mediaUri)) {
                            CommonUtil.shortToast(getContext(), "媒体资源不存在");
                            ((ViewGroup) getView()).removeAllViews();
                            return;
                        }
                        //Uri mediaUri = Uri.parse(lessonItem.mediaUri);
                        //playVideo(String.format("%s://%s%s", mediaUri.getScheme(), mediaUri.getHost(), mediaUri.getPath()));
                        playVideo(lessonItem.mediaUri);
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
            }
        });
    }

    private LessonItem getCachedLesson() {
        User user = getAppSettingProvider().getCurrentUser();
        School school = getAppSettingProvider().getCurrentSchool();
        if (user == null || school == null) {
            return null;
        }
        M3U8DbModel m3U8DbModel = M3U8Util.queryM3U8Model(
                getContext(), user.id, mCourseTask.id, school.getDomain(), M3U8Util.FINISH);
        if (m3U8DbModel == null) {
            return null;
        }
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(getContext());
        return sqliteUtil.queryForObj(
                new TypeToken<LessonItem>() {
                },
                "where type=? and key=?",
                Const.CACHE_LESSON_TYPE,
                "lesson-" + mCourseTask.id
        );
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            play();
        } else {
            pause();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TaskFinishHelper.Builder builder = new TaskFinishHelper.Builder()
                .setCourseId(mCourseId)
                .setCourseTask(mCourseTask)
                .setEnableFinish(mCourseProject.enableFinish);

        mTaskFinishHelper = new TaskFinishHelper(builder, getActivity())
                .setActionListener(new TaskFinishHelper.ActionListener() {
                    @Override
                    public void onFinish(TaskEvent taskEvent) {
                        EventBus.getDefault().postSticky(new MessageEvent<>(mCourseTask.id, MessageEvent.FINISH_TASK_SUCCESS));
                        TaskFinishDialog.newInstance(taskEvent, mCourseTask).show(getActivity()
                                .getSupportFragmentManager(), "mTaskFinishDialog");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

        mTaskFinishHelper.invoke();

//        if (mMenuCallback != null && mMenuCallback.getMenu() != null) {
//            mLessonMenuHelper = new LessonMenuHelper(getContext(), mCourseTask.id, mCourseId);
//            mLessonMenuHelper.initMenu(mMenuCallback.getMenu());
//            mLessonMenuHelper = new LessonMenuHelper(getActivity(), mCourseTask.id, mCourseId)
//                    .addCourseProject(mCourseProject)
//                    .setCourseTask(mCourseTask)
//                    .initTaskHelper()
//                    .initMenu(mMenuCallback.getMenu())
//                    .addMenuHelperListener(new LessonMenuHelper.MenuHelperFinishListener() {
//                        @Override
//                        public void showFinishTaskDialog(TaskEvent taskEvent) {
//                            TaskFinishDialog.newInstance(taskEvent, mCourseTask).show(getActivity().getSupportFragmentManager(), "mTaskFinishDialog");
//                        }
//                    });
//        }
        loadPlayUrl();
    }

    @Override
    public void doFinish() {
        mTaskFinishHelper.finish();
    }

    @Override
    protected VideoControllerView.ControllerListener getDefaultControllerListener() {

        return new SimpleVideoControllerListener() {
            @Override
            public void onPlayStatusChange(boolean isPlay) {
                mIsPlay = isPlay;
            }

            @Override
            public void onChangeScreen(int orientation) {
                super.onChangeScreen(orientation);
                changeScreenLayout(orientation);
            }

            @Override
            public void onChangeOverlay(boolean isShow) {
                super.onChangeOverlay(isShow);
                changeHeaderViewStatus(isShow);
            }
        };
    }

    @Override
    protected void changeScreenLayout(final int orientation) {
        if (orientation == getResources().getConfiguration().orientation) {
            return;
        }
        View playView = getView();
        ViewParent viewParent = playView.getParent();
        if (viewParent == null) {
            return;
        }
        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.FULL_SCREEN));

    }

    @Override
    protected void changeHeaderViewStatus(boolean isShow) {
        String changeBarEvent = isShow ?
                Const.COURSE_SHOW_BAR : Const.COURSE_HIDE_BAR;
        MessageEngine.getInstance().sendMsg(changeBarEvent, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMenuCallback != null && mMenuCallback.getMenu() != null) {
            mMenuCallback.getMenu().dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMenuCallback != null && mMenuCallback.getMenu() != null) {
            mMenuCallback.getMenu().setVisibility(false);
        }
        if (mTimer != null) {
            if (mIsContinue) {
                CourseDetailModel.sendTime(mCourseTask.id, mPlayTime, null);
            }
            mTimer.cancel();
            mIsContinue = false;
        }
    }

    @Override
    protected void savePosition(long seekTime) {
        super.savePosition(seekTime);
        SharedPreferences.Editor editor = mSeekPositionSetting.edit();
        editor.putLong(String.format("%d-%d", mCourseId, mCourseTask.id), seekTime);
        editor.commit();
    }

    @Override
    public void play() {
        if (mRemainTime != null && mTotalTime >= Integer.parseInt(mRemainTime) && mMenuCallback != null) {
            CommonUtil.shortCenterToast(mMenuCallback, getResources().getString(R.string.lesson_had_reached_hint));
            return;
        }
        super.play();
    }

    private void startTiming() {
        mIsContinue = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsContinue) {
                    try {
                        Thread.sleep(1000);
                        if (mIsPlay) {
                            mPlayTime++;
                            mTotalTime++;
                            if (mTotalTime >= Integer.parseInt(mRemainTime)) {
                                mIsContinue = false;
                                mTimer.cancel();
                                if (mMenuCallback != null) {
                                    mMenuCallback.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mTimer.cancel();
                                            //pause();
                                            CourseDetailModel.sendTime(mCourseTask.id, mPlayTime, null);
                                            if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
                                                return;
                                            }
                                            CommonUtil.shortCenterToast(mMenuCallback, getResources().getString(R.string.lesson_had_reached_hint));
                                        }
                                    });
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void startReturnData() {
        mTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                CourseDetailModel.sendTime(mCourseTask.id, mPlayTime, new ResponseCallbackListener<String>() {
                    @Override
                    public void onSuccess(String data) {
                        mPlayTime = 0;
                    }

                    @Override
                    public void onFailure(String code, String message) {
                    }
                });
            }
        };
        mTimer.schedule(timerTask, 120000, 120000);
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }
}
