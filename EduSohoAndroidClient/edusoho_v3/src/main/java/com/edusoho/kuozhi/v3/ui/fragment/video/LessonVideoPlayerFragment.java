package com.edusoho.kuozhi.v3.ui.fragment.video;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
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
import com.edusoho.kuozhi.v3.service.M3U8DownService;
import com.edusoho.kuozhi.v3.ui.BaseStudyDetailActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.MediaUtil;
import com.edusoho.kuozhi.v3.util.helper.LessonMenuHelper;
import com.edusoho.kuozhi.v3.util.helper.LocalLessonHelper;
import com.edusoho.kuozhi.v3.util.server.CacheServerFactory;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.videoplayer.media.ILogoutListener;
import com.edusoho.videoplayer.media.listener.SimpleVideoControllerListener;
import com.edusoho.videoplayer.ui.VideoPlayerFragment;
import com.edusoho.videoplayer.util.VLCOptions;
import com.edusoho.videoplayer.view.VideoControllerView;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import org.videolan.libvlc.util.AndroidUtil;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by suju on 16/12/16.
 */

public class LessonVideoPlayerFragment extends VideoPlayerFragment implements View.OnFocusChangeListener {

    private int mLessonId;
    private int mCourseId;
    private String mLessonTitle;
    private int mPlayTime;
    private int mTotalTime;
    private long mSaveSeekTime;
    private boolean mIsContinue;
    private boolean mIsPlay;
    private Timer mTimer;

    private AlertDialog mErrorDialog;
    private BaseStudyDetailActivity mMenuCallback;
    private LessonMenuHelper mLessonMenuHelper;
    private SharedPreferences mSeekPositionSetting;
    private static final String SEEK_POSITION = "seek_position";
    private String mRemainTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLessonId = getArguments().getInt(Const.LESSON_ID);
        mCourseId = getArguments().getInt(Const.COURSE_ID);
        mLessonTitle = getArguments().getString(Const.LESSON_NAME);
        mRemainTime = getArguments().getString(Const.REMAINT_TIME);
        mSeekPositionSetting = getContext().getSharedPreferences(SEEK_POSITION, Context.MODE_PRIVATE);
        mSaveSeekTime = mSeekPositionSetting.getLong(String.format("%d-%d", mCourseId, mLessonId), 0);

        setSeekPosition(mSaveSeekTime);
        if (mRemainTime != null) {
            startReturnData();
            startTiming();
        }
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

        School school = getAppSettingProvider().getCurrentSchool();
        if (school != null) {
            getArguments().putString(PLAY_DIGEST_KET, school.getDomain());
        }
    }

    private void loadPlayUrl() {
        LessonItem cachedLesson = getCachedLesson();
        if (cachedLesson != null) {
            cachedLesson.mediaUri = String.format("http://%s:8800/playlist/%d.m3u8", "localhost", mLessonId);
            if (MediaUtil.getMediaSupportType(getContext()) == VLCOptions.SUPPORT_RATE) {
                playVideo(cachedLesson.mediaUri);
                return;
            }
            File localFile = new LocalLessonHelper(getContext(), mLessonId).createLocalPlayListFile();
            if (localFile != null) {
                MobclickAgent.reportError(getContext(), String.format("playVideo file:%s", localFile.getAbsolutePath()));
                playVideo("file://" + localFile.getAbsolutePath());
                return;
            }
            MobclickAgent.reportError(getContext(), String.format("file:%d not found", localFile.getAbsolutePath()));
            CommonUtil.shortToast(getContext(), "本地资源不存在,开始加载网络视频");
        }
        new LessonProvider(getContext()).getLesson(mLessonId)
                .success(new NormalCallback<LessonItem>() {
                    @Override
                    public void success(LessonItem lessonItem) {
                        changeHeaderViewStatus(false);
                        if (lessonItem == null || TextUtils.isEmpty(lessonItem.mediaUri)) {
                            MobclickAgent.reportError(getContext(), String.format("mediaUri not found:%d", mLessonId));
                            CommonUtil.shortToast(getContext(), "媒体资源不存在");
                            ((ViewGroup) getView()).removeAllViews();
                            return;
                        }
                        Uri mediaUri = Uri.parse(lessonItem.mediaUri);
                        MobclickAgent.reportError(getContext(), String.format("play video not mediaUri:%s", mediaUri));
                        playVideo(String.format("%s://%s%s", mediaUri.getScheme(), mediaUri.getHost(), mediaUri.getPath()));
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                MobclickAgent.reportError(getContext(), obj);
            }
        });
    }

    private LessonItem getCachedLesson() {
        User user = getAppSettingProvider().getCurrentUser();
        School school = getAppSettingProvider().getCurrentSchool();
        if (user == null || school == null) {
            MobclickAgent.reportError(getContext(), "getCachedLesson : user or school is null");
            return null;
        }
        M3U8DbModel m3U8DbModel = M3U8Util.queryM3U8Model(
                getContext(), user.id, mLessonId, school.getDomain(), M3U8Util.FINISH);
        if (m3U8DbModel == null) {
            MobclickAgent.reportError(getContext(), String.format("m3U8DbModel is null lessonId:%d", mLessonId));
            return null;
        }
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(getContext());
        return sqliteUtil.queryForObj(
                new TypeToken<LessonItem>() {
                },
                "where type=? and key=?",
                Const.CACHE_LESSON_TYPE,
                "lesson-" + mLessonId
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
        School school = getAppSettingProvider().getCurrentSchool();
        int mediaCoder = MediaUtil.getMediaSupportType(getContext());
        User user = getAppSettingProvider().getCurrentUser();
        if(user != null) {
            if (mediaCoder == VLCOptions.SUPPORT_RATE && !checkCacheServerIsStarted(school.host, user.id)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("播放错误")
                        .setMessage("本地播放服务启动失败,继续将不能播放本地缓存视频,是否重新进入课程?")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        })
                        .setPositiveButton("取消", null)
                        .create();
                return;
            }
        }
        super.onViewCreated(view, savedInstanceState);
        if (mMenuCallback != null && mMenuCallback.getMenu() != null) {
            mLessonMenuHelper = new LessonMenuHelper(getContext(), mLessonId, mCourseId);
            mLessonMenuHelper.initMenu(mMenuCallback.getMenu());
        }
        loadPlayUrl();
        bindLogoutListener();
    }

    private boolean checkCacheServerIsStarted(String host, int userId) {
        return CacheServerFactory.getInstance().cacheServerIsRuning(host, userId);
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
        ViewGroup parent = (ViewGroup) viewParent.getParent();
        MessageEngine.getInstance().sendMsg(Const.FULL_SCREEN, null);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        ViewGroup.LayoutParams lp = parent.getLayoutParams();
        lp.height = orientation == Configuration.ORIENTATION_LANDSCAPE ?
                wm.getDefaultDisplay().getHeight() : getContext().getResources().getDimensionPixelOffset(com.edusoho.videoplayer.R.dimen.video_height);
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        parent.setLayoutParams(lp);
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
                CourseDetailModel.sendTime(mLessonId, mPlayTime, null);
            }
            mTimer.cancel();
            mIsContinue = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLessonMenuHelper != null) {
            mLessonMenuHelper.updatePluginItemState();
        }
    }

    @Override
    protected void savePosition(long seekTime) {
        super.savePosition(seekTime);
        SharedPreferences.Editor editor = mSeekPositionSetting.edit();
        editor.putLong(String.format("%d-%d", mCourseId, mLessonId), seekTime);
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
                                            CourseDetailModel.sendTime(mLessonId, mPlayTime, null);
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
                CourseDetailModel.sendTime(mLessonId, mPlayTime, new ResponseCallbackListener<String>() {
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

    protected void bindLogoutListener() {
        addLogoutListener(new ILogoutListener() {
            @Override
            public void onLog(String tag, Object message) {
                if (message instanceof Throwable) {
                    MobclickAgent.reportError(getContext(), (Throwable) message);
                } else {
                    MobclickAgent.reportError(getContext(), message.toString());
                }
            }
        });
    }

    @Override
    public void onReceive(String type, String mesasge) {
        synchronized (this) {
            if (mErrorDialog != null && mErrorDialog.isShowing()) {
                return;
            }
        }

        if ("FileDataSourceException".equals(type)
                || "VideoFileNotFound".equals(type)) {
            pause();
            //delete file
            if ("VideoFileNotFound".equals(type) && !TextUtils.isEmpty(mesasge)) {
                File delFile = new File(mesasge);
                if (delFile.exists()) {
                    delFile.delete();
                }
            }
            mErrorDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("播放错误")
                    .setMessage("视频文件损坏,正在重新下载,请进入我的缓存里查看下载进度")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    })
                    .setPositiveButton("重新下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                            updateM3U8ModelUnFinish(mLessonId, mCourseId);
                        }
                    })
                    .create();
            mErrorDialog.show();
        }
    }

    private void updateM3U8ModelUnFinish(int lessonId, int courseId) {
        M3U8Util.deleteM3U8Model(getContext(), lessonId);
        School school = getAppSettingProvider().getCurrentSchool();
        User user = getAppSettingProvider().getCurrentUser();
        M3U8Util.saveM3U8Model(getContext(), lessonId, school.getDomain(), user.id);
        M3U8DownService.startDown(getContext(), lessonId, courseId, TextUtils.isEmpty(mLessonTitle) ? "更新视频文件" : mLessonTitle);
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }
}
