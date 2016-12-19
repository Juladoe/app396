package com.edusoho.kuozhi.v3.ui.fragment.lesson;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.edusoho.kuozhi.R;
import com.edusoho.videoplayer.ui.AudioPlayerFragment;
import com.edusoho.videoplayer.util.ControllerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by suju on 16/12/18.
 */

public class LessonAudioPlayerFragment extends AudioPlayerFragment {

    public static final String COVER = "cover";

    protected String mCoverUrl;
    protected float mAudioCoverAnimOffset;
    protected ObjectAnimator mAudioCoverAnim;

    private boolean isPlay;
    private ImageView mCoverImageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCoverUrl = getArguments().getString(COVER);
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
        ImageLoader.getInstance().displayImage(mCoverUrl, mCoverImageView);
    }

    protected void initPlayContainer() {
        View containerView = LayoutInflater.from(getContext()).inflate(R.layout.view_audio_container_layout, null);
        setContainerView(containerView);
        mCoverImageView = (ImageView) containerView.findViewById(R.id.rl_audio_cover);
        initCoverSize();
        mCoverImageView.setOnClickListener(getCoverClickListener());
    }

    private void initCoverSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int parentWidth = wm.getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams lp = mCoverImageView.getLayoutParams();
        lp.width = parentWidth / 3;
        lp.height = parentWidth / 3;
        mCoverImageView.setLayoutParams(lp);
    }

    private View.OnClickListener getCoverClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlay = !isPlay;
                mVideoControllerView.updatePlayStatus(isPlay);
            }
        };
    }

    @Override
    public void onPlayStatusChange(boolean isPlay) {
        super.onPlayStatusChange(isPlay);
        this.isPlay = isPlay;
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
            mAudioCoverAnim.setFloatValues(mAudioCoverAnimOffset, mAudioCoverAnimOffset + 359f);
            mAudioCoverAnim.start();
        } else {
            mAudioCoverAnimOffset = (float) mAudioCoverAnim.getAnimatedValue();
            mAudioCoverAnim.cancel();
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
}
