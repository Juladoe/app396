package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.EdusohoMaterialDialog;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by howzhi on 14-9-16.
 */
public class VideoLessonFragment extends BaseFragment implements View.OnClickListener {

    private VideoView mVideoView;
    private MediaPlayer mMediaPlayer;
    private SeekBar mSeekBar;
    private ImageView mScreenBtn;
    private ImageView mPreBtn;
    private ImageView mNextBtn;
    private ImageView mPlayBtn;
    private TextView mPlayTimeView;
    private TextView mTotalTimeView;

    private Uri mUrl;

    private Handler updateHandler;
    private static final int UPDATE_PLAY_TIME = 0001;
    private static final int SET_TOTALTIME = 0002;
    private Timer updateTimer;
    private boolean mIsSetTotalTime;

    private static final int DEFAULT_TIME = 3600 * 1000 * 16;
    private static SimpleDateFormat dateFromat = new SimpleDateFormat("HH:mm:ss");

    @Override
    public String getTitle() {
        return "视频课时";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.video_lesson_fragment_layout);
        updateTimer = new Timer();
        updateHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String time = dateFromat.format(new Date(msg.arg1 + DEFAULT_TIME));
                switch (msg.what) {
                    case UPDATE_PLAY_TIME:
                        mPlayTimeView.setText(time);
                        mSeekBar.setProgress(msg.arg1);
                        break;
                    case SET_TOTALTIME:
                        mIsSetTotalTime = true;
                        mSeekBar.setMax(msg.arg1);
                        mTotalTimeView.setText(time);
                        break;
                }
            }
        };
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUrl = Uri.parse(bundle.getString(Const.MEDIA_URL));
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mSeekBar = (SeekBar) view.findViewById(R.id.lesson_video_seekbar);
        mPreBtn = (ImageView) view.findViewById(R.id.lesson_video_prebtn);
        mNextBtn = (ImageView) view.findViewById(R.id.lesson_video_nextbtn);
        mPlayBtn = (ImageView) view.findViewById(R.id.lesson_video_playbtn);
        mScreenBtn = (ImageView) view.findViewById(R.id.lesson_video_screenbtn);
        mPlayTimeView = (TextView) view.findViewById(R.id.lesson_video_playtime);
        mTotalTimeView = (TextView) view.findViewById(R.id.lesson_video_totaltime);

        mVideoView = (VideoView) view.findViewById(R.id.lesson_videoview);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (mediaPlayer == null) {
                    return;
                }

                mMediaPlayer = mediaPlayer;
                initMediaPlayerListener();
                if (mediaPlayer.getVideoHeight() == 0 || mediaPlayer.getVideoWidth() == 0) {
                    ToastUtils.show(mContext, "不好意思～此视频不能在该设备上播放，请联系网站管理员！");
                    return;
                }
            }
        });

        Log.d(null, "video url->" + mUrl);
        mVideoView.setVideoURI(mUrl);
        mVideoView.start();

        mPreBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        mScreenBtn.setOnClickListener(this);
    }

    private void initMediaPlayerListener()
    {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean isTouch) {
                if (isTouch) {
                    mVideoView.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (mVideoView != null) {
                    Message msg = updateHandler.obtainMessage(UPDATE_PLAY_TIME);
                    msg.arg1 = mVideoView.getCurrentPosition();
                    msg.sendToTarget();

                    if (!mIsSetTotalTime) {
                        msg = updateHandler.obtainMessage(SET_TOTALTIME);
                        msg.arg1 = mVideoView.getDuration();
                        msg.sendToTarget();
                    }
                }
            }
        };
        updateTimer.schedule(timerTask, 0, 1000);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.lesson_video_nextbtn) {

        } else if (id == R.id.lesson_video_prebtn){

        } else if (id == R.id.lesson_video_playbtn) {
            if (mVideoView.isPlaying()) {
                videoPause();
            } else {
                videoPlay();
            }
        }
    }

    private void videoPause()
    {
        mVideoView.pause();
        mPlayBtn.setImageResource(R.drawable.video_play);
    }

    private void videoPlay()
    {
        mVideoView.start();
        mPlayBtn.setImageResource(R.drawable.video_pause);
    }

    @Override
    public void onPause() {
        super.onPause();
        videoPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateTimer.cancel();
    }
}
