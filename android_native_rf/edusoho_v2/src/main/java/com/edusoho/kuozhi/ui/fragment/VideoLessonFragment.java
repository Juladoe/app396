package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.plugin.video.CustomMediaController;

import java.util.Timer;

/**
 * Created by howzhi on 14-9-26.
 */
public class VideoLessonFragment extends BaseFragment {

    private VideoView mVideoView;
    @Override
    public String getTitle() {
        return "视频课时";
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.video_lesson_fragment_layout);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        mUri = Uri.parse(bundle.getString(Const.MEDIA_URL));
        Log.d(null, "uri->" + mUri);
        autoHideTimer = new Timer();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mVideoView = (VideoView) view.findViewById(R.id.video_view);
        mLoadView = view.findViewById(R.id.load_layout);

        mMediaController = (CustomMediaController) view.findViewById(R.id.custom_mediaController);
        mMediaController.setVideoView(mVideoView);
        mMediaController.setActivity(mActivity);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mVideoView.requestLayout();
                mMediaController.ready();
                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int progress) {
                        Log.d(null, "progress->" + progress);
                        if (progress == 100) {
                            mLoadView.setVisibility(View.GONE);
                        } else {
                            if (mLoadView.getVisibility() == View.GONE) {
                                mLoadView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                if (Build.VERSION.SDK_INT < 19) {
                    //EdusohoApp.app.sendMessage(EdusohoVideoManagerActivity.SUPPORTMAP_CHANGE, new MessageModel(null));
                } else {
                    PopupDialog.createNormal(
                            mContext, "视频播放", "不好意思～此视频不能在该设备上播放，请联系网站管理员！").show();
                }
                mLoadView.setVisibility(View.GONE);
                return true;
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mMediaController.stop(mediaPlayer);
            }
        });
    }

    private View mLoadView;
    private Uri mUri;
    private int mPositionWhenPaused = -1;

    private CustomMediaController mMediaController;
    private Timer autoHideTimer;

    @Override
    public void onStart() {
        // Play Video
        if (mUri != null) {
            mVideoView.setVideoURI(mUri);
            mMediaController.play();
        }
        System.out.println("start->play");
        super.onStart();
    }

    @Override
    public void onPause() {
        // Stop video when the activity is pause.
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mMediaController.pause();

        super.onPause();
    }

    @Override
    public void onResume() {
        // Resume video player
        if (mPositionWhenPaused >= 0) {
            mVideoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }

        super.onResume();
    }

    @Override
    public void onDestroy() {
        System.out.println("video player distory");
        super.onDestroy();
        if (autoHideTimer != null) {
            autoHideTimer.cancel();
        }
        mMediaController.destory();
        mVideoView.pause();
        mVideoView = null;
    }
}
