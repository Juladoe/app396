package com.edusoho.kuozhi.v3.ui.fragment.lesson;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.DetailActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.ImageUtil;
import com.edusoho.kuozhi.v3.util.helper.LessonMenuHelper;
import com.edusoho.videoplayer.ui.AudioPlayerFragment;
import com.edusoho.videoplayer.util.ControllerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by suju on 16/12/18.
 */

public class LessonAudioPlayerFragment extends AudioPlayerFragment {

    public static final String COVER = "cover";

    private int mLessonId;
    private int mCourseId;
    protected String mCoverUrl;
    protected float mAudioCoverAnimOffset;
    protected ObjectAnimator mAudioCoverAnim;
    private ImageView mCoverImageView;
    private DetailActivity mMenuCallback;
    private LessonMenuHelper mLessonMenuHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCoverUrl = getArguments().getString(COVER);
        mLessonId = getArguments().getInt(Const.LESSON_ID);
        mCourseId = getArguments().getInt(Const.COURSE_ID);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMenuCallback = (DetailActivity) activity;
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
        mLessonMenuHelper = new LessonMenuHelper(getContext(), mLessonId, mCourseId);
        mLessonMenuHelper.initMenu(mMenuCallback.getMenu());
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
            mAudioCoverAnim.setFloatValues(mAudioCoverAnimOffset, mAudioCoverAnimOffset + 359f);
            mAudioCoverAnim.start();
        } else {
            mAudioCoverAnimOffset = (float) mAudioCoverAnim.getAnimatedValue();
            mAudioCoverAnim.cancel();
        }
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
        if (mAudioCoverAnim != null) {
            mAudioCoverAnim.cancel();
            mAudioCoverAnim = null;
        }
        if (mMenuCallback != null && mMenuCallback.getMenu() != null) {
            mMenuCallback.getMenu().setVisibility(false);
        }
    }
}
