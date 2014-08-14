package com.edusoho.plugin.video;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.MessageModel;
import com.edusoho.kuozhi.view.dialog.PopupDialog;

import java.util.Timer;

public class EduSohoVideoFragment extends Fragment implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private CustomPlayerView mVideoView;
    private Activity mContext;
    private View mLoadView;
    private Uri mUri;
    private int mPositionWhenPaused = -1;

    private CustomMediaController mMediaController;
    private Timer autoHideTimer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_video, container, false);
        mContext = getActivity();

        mLoadView = view.findViewById(R.id.load_layout);
        mVideoView = (CustomPlayerView)view.findViewById(R.id.playVideoView);

        mMediaController = (CustomMediaController) view.findViewById(R.id.custom_mediaController);
        mMediaController.setVideoView(mVideoView);
        mMediaController.setActivity(mContext);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {

                mVideoView.videoWidth = mediaPlayer.getVideoWidth();
                mVideoView.videoHeight = mediaPlayer.getVideoHeight();
                mediaPlayer.start();
                mVideoView.requestLayout();
                mMediaController.ready(mediaPlayer);
                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int progress) {
                        Log.d(null, "progress->" + progress);
                        if (mLoadView.getVisibility() == View.VISIBLE) {
                            mLoadView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                if (Build.VERSION.SDK_INT < 19) {
                    EdusohoApp.app.sendMessage(EdusohoVideoManagerActivity.SUPPORTMAP_CHANGE, new MessageModel(null));
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

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView()
    {
        Bundle bundle = getArguments();
        if (!bundle.containsKey("url")) {
            return;
        }

        mUri = Uri.parse(bundle.getString("url"));
        autoHideTimer = new Timer();
    }

    @Override
    public void onStart() {
        // Play Video
        if (mUri != null) {
            mVideoView.setVideoURI(mUri);
            mMediaController.play();
        }
        System.out.println("start->");
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
        if(mPositionWhenPaused >= 0) {
            mVideoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }

        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onError(MediaPlayer player, int arg1, int arg2) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

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