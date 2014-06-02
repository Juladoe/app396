package com.edusohoapp.plugin.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;

import com.edusohoapp.app.R;
import com.edusohoapp.app.view.PopupDialog;

import java.util.Timer;
import java.util.TimerTask;

public class EduSohoVideoActivity extends Activity implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private CustomPlayerView mVideoView;
    private Context mContext;
    private View mLoadView;
    private Uri mUri;
    private int mPositionWhenPaused = -1;

    private CustomMediaController mMediaController;
    private Timer autoHideTimer;
    private Handler updateHandler;
    private boolean mIsShowController;

    private static final int HIDE = 0001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mIsShowController = false;
        setContentView(R.layout.frame_video);

        mLoadView = findViewById(R.id.load_layout);
        mVideoView = (CustomPlayerView)findViewById(R.id.playVideoView);
        //Create media controller，组件可以控制视频的播放，暂停，回复，seek等操作，不需要你实现
        mMediaController = (CustomMediaController) findViewById(R.id.custom_mediaController);
        mMediaController.setVideoView(mVideoView);
        mMediaController.setActivity(this);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                mVideoView.videoWidth = mediaPlayer.getVideoWidth();
                mVideoView.videoHeight = mediaPlayer.getVideoHeight();
                mediaPlayer.start();
                mVideoView.requestLayout();
                mLoadView.setVisibility(View.GONE);
                mMediaController.ready();
            }

        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                PopupDialog.createNormal(mContext, "播放错误", "设备不能播放该视频！").show();
                return true;
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mMediaController.stop();
            }
        });
        initView();
    }

    private void hideController()
    {
        mIsShowController = false;
        mMediaController.setVisibility(View.GONE);
    }

    private void showController()
    {
        mIsShowController = true;
        mMediaController.setVisibility(View.VISIBLE);
        autoHideTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mIsShowController) {
                    updateHandler.obtainMessage(HIDE).sendToTarget();
                }
            }
        }, 3000);
    }

    private void initView()
    {
        Intent intentData = getIntent();
        if (!intentData.hasExtra("url")) {
            return;
        }

        mUri = Uri.parse(intentData.getStringExtra("url"));
        autoHideTimer = new Timer();
    }

    public void onStart() {
        // Play Video
        if (mUri != null) {
            mVideoView.setVideoURI(mUri);
            mMediaController.play();
        }

        super.onStart();
    }

    public void onPause() {
        // Stop video when the activity is pause.
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mMediaController.pause();

        super.onPause();
    }

    public void onResume() {
        // Resume video player
        if(mPositionWhenPaused >= 0) {
            mVideoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }

        super.onResume();
    }

    public boolean onError(MediaPlayer player, int arg1, int arg2) {
        return false;
    }

    public void onCompletion(MediaPlayer mp) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (autoHideTimer != null) {
            autoHideTimer.cancel();
        }
        mMediaController.destory();
    }
}