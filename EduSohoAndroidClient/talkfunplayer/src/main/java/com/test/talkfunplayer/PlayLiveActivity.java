package com.test.talkfunplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.talkfun.media.player.TFVideoPlayer;
import com.talkfun.media.player.interfaces.OnLiveNotFoundListener;
import com.talkfun.media.player.interfaces.ValidateFailListener;
import com.talkfun.media.player.option.VideoScaleMode;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class PlayLiveActivity extends Activity {

    RelativeLayout mRelativeLayout;
    private ProgressBar progressBar;

    private FrameLayout container;

    private TFVideoPlayer tfVideoPlayer;

    private MediaController mController;

    private boolean isAutoPlay = true;

    private Button btnSeek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String url = getIntent().getExtras().getString("url");
        //url = "http://p2-2.talk-fun.com/mediaCloudLive/333/10000/mzmz/video-cdn.mp4?appid=10000&vdoid=1030&limit=25K&sinature=1f51485f8d696e5686a80dee2c4e7b44&expire=1469675701";
        if (url.isEmpty()) {
            Toast.makeText(this, "播放地址为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar = (ProgressBar) findViewById(R.id.loading);
        tfVideoPlayer = (TFVideoPlayer) findViewById(R.id.tf_video_player);
        // btnSeek = (Button) findViewById(R.id.btn_seek);

        initPlayer();
        initEventListeners();
        // 设置MediaController
        mController = new MediaController(this);
        tfVideoPlayer.setMediaController(mController);

        tfVideoPlayer.setDataSource(url);
        //tfVideoPlayer.videoView.setVideoPath("http://p2-2.talk-fun.com/live/114/14372/mti5ntexna/video-client-1.mp4");

        //tintDrawable
  /*     btnSeek.setOnClickListener(new View.OnClickListener() {
           boolean isPort = true;
           @Override
           public void onClick(View v) {
              // tfVideoPlayer.seekTo(120*1000);
               if(isPort){
                   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                   isPort = false;
               }else{
                   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                   isPort = true;
               }

           }
       });*/


    }

    private void initPlayer(){
        tfVideoPlayer.setAppId(Config.appid)
                .setAccessKey(Config.accessKey)
                .setAutoPlay(isAutoPlay)
                .setVideoScaleMode(VideoScaleMode.FILL_PARENT)
                .setIsLive(true);
        tfVideoPlayer.setBufferingIndicator(progressBar);

    }
    private void initEventListeners(){
        tfVideoPlayer.setValidateFailListener(mValidateFailListener);
        tfVideoPlayer.setOnLiveNotFoundListener(mOnLiveNotFoundListener);
        tfVideoPlayer.setOnPreparedListener(mOnPreparedListener);
        tfVideoPlayer.setOnCompletionListener(onCompletionListener);
        tfVideoPlayer.setOnErrorListener(mOnErrorLister);
    }

    OnLiveNotFoundListener mOnLiveNotFoundListener = new OnLiveNotFoundListener() {
        @Override
        public void OnLiveNotFound(String msg) {
            new AlertDialog.Builder(PlayLiveActivity.this)
                    .setTitle("直播未开始")
                    .setMessage(msg)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PlayLiveActivity.this.finish();
                        }
                    })
                    .setCancelable(false).show();
        }
    };

    ValidateFailListener mValidateFailListener = new ValidateFailListener() {
        @Override
        public void onValidataFail(int code,String msg) {
            new AlertDialog.Builder(PlayLiveActivity.this)
                    .setTitle("验证失败")
                    .setMessage(msg)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PlayLiveActivity.this.finish();
                        }
                    })
                    .setCancelable(false).show();
        }
    };

    IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            if(!isAutoPlay)
                tfVideoPlayer.start();
        }
    };

    IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            Toast.makeText(PlayLiveActivity.this,"播放结束",Toast.LENGTH_SHORT).show();
        }
    };

    IMediaPlayer.OnErrorListener mOnErrorLister = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            new AlertDialog.Builder(PlayLiveActivity.this)
                    .setTitle("播放失败")
                    .setMessage("该视频播放失败")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCancelable(false).show();
            return true;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(tfVideoPlayer != null){
            tfVideoPlayer.onResum();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(tfVideoPlayer != null){
            tfVideoPlayer.onPause();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //  mVideoView.onConfigurationChange();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tfVideoPlayer != null)
            tfVideoPlayer.release();
    }

}