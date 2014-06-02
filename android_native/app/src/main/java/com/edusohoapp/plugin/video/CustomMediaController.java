package com.edusohoapp.plugin.video;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.edusohoapp.app.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by howzhi on 14-6-1.
 */
public class CustomMediaController extends RelativeLayout {

    private Context mContext;
    private Activity mActivity;
    private ImageView playBtn;
    private ImageView customRotationBtn;
    private ImageView prevBtn;
    private ImageView nextBtn;
    private TextView currentTime;
    private TextView totalTime;
    private SeekBar playSeekBar;
    private VideoView mVideoView;
    private boolean mIsShowController;
    private boolean mIsSetTotalTime;

    private Timer updateTimer;
    private Timer autoHideTimer;

    private Handler updateHandler;
    private static final int UPDATE_PLAY_TIME = 0001;
    private static final int SET_TOTALTIME = 0002;
    private static final int HIDE = 0003;

    private static final int DEFAULT_TIME = 3600 * 1000 * 16;
    private static SimpleDateFormat dateFromat = new SimpleDateFormat("HH:mm:ss");

    public CustomMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public CustomMediaController(Context context) {
        super(context);
        this.mContext = context;
    }

    public void setVideoView(VideoView view)
    {
        mVideoView = view;
        initView();
    }

    public void setActivity(Activity activity)
    {
        mActivity = activity;
    }

    public void ready()
    {
        updateTimer.schedule(new TimerTask() {
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
        }, 0, 1000);

        show();
    }

    public void initView()
    {
        mIsSetTotalTime = false;
        mIsShowController = true;

        playBtn = (ImageView) findViewById(R.id.custom_play);
        prevBtn = (ImageView) findViewById(R.id.custom_prev);
        nextBtn = (ImageView) findViewById(R.id.custom_next);
        customRotationBtn = (ImageView) findViewById(R.id.custom_rotation_btn);
        playSeekBar = (SeekBar) findViewById(R.id.custom_seekbar);
        currentTime = (TextView) findViewById(R.id.custom_currentTime);
        totalTime = (TextView) findViewById(R.id.custom_totalTime);

        updateHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String time = dateFromat.format(new Date(msg.arg1 + DEFAULT_TIME));
                switch (msg.what) {
                    case UPDATE_PLAY_TIME:
                        currentTime.setText(time);
                        playSeekBar.setProgress(msg.arg1);
                        break;
                    case SET_TOTALTIME:
                        playSeekBar.setMax(msg.arg1);
                        totalTime.setText(time);
                        break;
                    case HIDE:
                        hide();
                        break;
                }
            }
        };

        bindClickListener();
        updateTimer = new Timer();
        autoHideTimer = new Timer();
    }

    private void hide()
    {
        mIsShowController = false;
        setVisibility(View.INVISIBLE);
    }

    private void show()
    {
        mIsShowController = true;
        setVisibility(View.VISIBLE);
        autoHideTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mIsShowController) {
                    updateHandler.obtainMessage(HIDE).sendToTarget();
                }
            }
        }, 3000);
    }

    public void play()
    {
        playBtn.setImageResource(R.drawable.custom_pause);
        mVideoView.start();
    }

    public void pause()
    {
        playBtn.setImageResource(R.drawable.custom_play);
        mVideoView.pause();
    }

    public void stop()
    {
        playBtn.setImageResource(R.drawable.custom_play);
        mVideoView.seekTo(0);
    }

    /**
     * @suju
     */
    private class ClickListener implements OnClickListener
    {
        @Override
        public void onClick(View view) {
            int current = 0;
            switch (view.getId()) {
                case R.id.custom_play:
                    if (mVideoView.isPlaying()) {
                        pause();
                    } else {
                        play();
                    }
                    break;
                case R.id.custom_next:
                    current = mVideoView.getCurrentPosition();
                    current += 15 * 1000;
                    if (current < mVideoView.getDuration()) {
                        mVideoView.seekTo(current);
                    }
                    break;
                case R.id.custom_prev:
                    current = mVideoView.getCurrentPosition();
                    current -= 15 * 1000;
                    if (current > 0) {
                        mVideoView.seekTo(current);
                    }
                    break;
                case R.id.custom_rotation_btn:
                    //水平
                    int screenOrientation = mActivity.getRequestedOrientation();
                    if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
            }
        }
    }

    private void bindClickListener()
    {
        playSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                if (fromUser) {
                    mVideoView.seekTo(value);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        ClickListener clickListener = new ClickListener();
        playBtn.setOnClickListener(clickListener);
        prevBtn.setOnClickListener(clickListener);
        nextBtn.setOnClickListener(clickListener);
        customRotationBtn.setOnClickListener(clickListener);

        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mIsShowController) {
                        hide();
                    } else {
                        show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void destory()
    {
        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer = null;
        }

        if (autoHideTimer != null) {
            autoHideTimer.cancel();
            autoHideTimer = null;
        }
    }
}
