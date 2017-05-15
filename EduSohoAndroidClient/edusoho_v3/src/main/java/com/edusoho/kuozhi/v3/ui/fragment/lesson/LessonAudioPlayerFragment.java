package com.edusoho.kuozhi.v3.ui.fragment.lesson;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

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
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.LessonProvider;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.ImageUtil;
import com.edusoho.videoplayer.ui.AudioPlayerFragment;
import com.edusoho.videoplayer.util.ControllerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.greenrobot.eventbus.EventBus;
import org.videolan.libvlc.MediaPlayer;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by suju on 16/12/18.
 */

public class LessonAudioPlayerFragment extends AudioPlayerFragment implements CourseProjectActivity.TaskFinishListener {

    public static final String COVER = "cover";
    private static final String COURSE_PROJECT = "course_project";
    private static final String COURSE_TASK = "course_task";

    protected String mCoverUrl;
    protected float mAudioCoverAnimOffset;
    protected ObjectAnimator mAudioCoverAnim;
    private ImageView mCoverImageView;
    private CourseTask mCourseTask;
    private CourseProject mCourseProject;
    private TaskFinishHelper mTaskFinishHelper;

    public static LessonAudioPlayerFragment newInstance(String cover, CourseTask courseTask, CourseProject courseProject) {
        Bundle args = new Bundle();
        args.putString(COVER, cover);
        args.putSerializable(COURSE_TASK, courseTask);
        args.putSerializable(COURSE_PROJECT, courseProject);
        LessonAudioPlayerFragment fragment = new LessonAudioPlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mCoverUrl = bundle.getString(COVER);
        mCourseTask = (CourseTask) bundle.getSerializable(COURSE_TASK);
        mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT);
        if (mCourseTask == null) {
            ToastUtils.show(getActivity(), "CourseTask is null");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ControllerOptions options = new ControllerOptions.Builder()
                .addOption(ControllerOptions.RATE, false)
                .addOption(ControllerOptions.SCREEN, false)
                .build();
        mVideoControllerView.setControllerOptions(options);
        initPlayContainer();

        TaskFinishHelper.Builder builder = new TaskFinishHelper.Builder()
                .setCourseId(mCourseProject.id)
                .setCourseTask(mCourseTask)
                .setEnableFinish(mCourseProject.enableFinish);

        mTaskFinishHelper = new TaskFinishHelper(builder, getActivity())
                .setActionListener(new TaskFinishHelper.ActionListener() {
                    @Override
                    public void onFinish(TaskEvent taskEvent) {
                        EventBus.getDefault().postSticky(new MessageEvent<>(mCourseTask, MessageEvent.FINISH_TASK_SUCCESS));
                        TaskFinishDialog.newInstance(taskEvent, mCourseTask).show(getActivity()
                                .getSupportFragmentManager(), "mTaskFinishDialog");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

        mTaskFinishHelper.onInvoke();

        loadPlayUrl();
    }

    @Override
    public void doFinish() {
        mTaskFinishHelper.finish();
    }

    public void pause() {
        if (mService.isPlaying()) {
            mService.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAudioCoverAnim != null) {
            mAudioCoverAnim.cancel();
            mAudioCoverAnim = null;
        }
    }

    @Override
    public void onMediaPlayerEvent(MediaPlayer.Event event) {
        super.onMediaPlayerEvent(event);
        if (mCourseProject.enableFinish == 0 && !mCourseTask.isFinish() && event.type == 265) {
            mTaskFinishHelper.stickyFinish();
        }
    }

    protected void setCoverViewState(boolean isShow) {
        mCoverImageView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void loadPlayUrl() {
        new LessonProvider(getContext()).getLesson(mCourseTask.id)
                .success(new NormalCallback<LessonItem>() {
                    @Override
                    public void success(LessonItem lessonItem) {
                        if (getActivity() == null
                                || getActivity().isFinishing()
                                || !isAdded()
                                || isDetached()) {
                            return;
                        }
                        changeToolBarState(false);
                        setCoverViewState(true);
                        if (lessonItem == null || TextUtils.isEmpty(lessonItem.mediaUri)) {
                            return;
                        }

                        playAudio(lessonItem.mediaUri);
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
            }
        });
    }

    protected void initPlayContainer() {
        final View containerView = LayoutInflater.from(getContext()).inflate(R.layout.view_audio_container_layout, null);
        setContainerView(containerView);
        mCoverImageView = (ImageView) containerView.findViewById(R.id.rl_audio_cover);
        ImageLoader.getInstance().displayImage(mCoverUrl, mCoverImageView);
        ImageLoader.getInstance().loadImage(mCoverUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                if (getActivity() == null || getActivity().isFinishing() || isDetached()) {
                    return;
                }
                Bitmap maskBg = ImageUtil.maskImage(getContext(), loadedImage);
                containerView.setBackground(new BitmapDrawable(maskBg));
            }
        });
        initCoverSize();
    }

    private void initCoverSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int parentWidth = wm.getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams lp = mCoverImageView.getLayoutParams();
        lp.width = parentWidth / 3;
        lp.height = parentWidth / 3;
        mCoverImageView.setLayoutParams(lp);
    }

    @Override
    public void onPlayStatusChange(boolean isPlay) {
        super.onPlayStatusChange(isPlay);
        updateAudioCoverViewStatus(isPlay);
    }

    @Override
    protected void stopPlayback() {
        super.stopPlayback();
        updateAudioCoverViewStatus(false);
    }

    @Override
    public void onChangeOverlay(boolean isShow) {
        super.onChangeOverlay(isShow);
        changeToolBarState(isShow);
    }

    private void changeToolBarState(boolean isShow) {
        String changeBarEvent = isShow ?
                Const.COURSE_SHOW_BAR : Const.COURSE_HIDE_BAR;
        MessageEngine.getInstance().sendMsg(changeBarEvent, null);
    }

    @Override
    protected void updateMediaPlayStatus(boolean isPlay) {
        super.updateMediaPlayStatus(isPlay);
        updateAudioCoverViewStatus(isPlay);
    }

    private void updateAudioCoverViewStatus(boolean isPlay) {
        if (mAudioCoverAnim == null) {
            mAudioCoverAnim = ObjectAnimator.ofFloat(mCoverImageView, "rotation", 0f, 359f);
            mAudioCoverAnim.setDuration(10000);
            mAudioCoverAnim.setInterpolator(new LinearInterpolator());
            mAudioCoverAnim.setRepeatCount(-1);
        }
        if (isPlay) {
            if (mAudioCoverAnim.isRunning()) {
                return;
            }
            mAudioCoverAnim.setFloatValues(mAudioCoverAnimOffset, mAudioCoverAnimOffset + 359f);
            mAudioCoverAnim.start();
        } else {
            mAudioCoverAnimOffset = (float) mAudioCoverAnim.getAnimatedValue();
            mAudioCoverAnim.cancel();
        }
    }

}
