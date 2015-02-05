package com.soooner.EplayerPluginLibary.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.soooner.EplayerPluginLibary.R;
import com.soooner.EplayerPluginLibary.util.DateUtil;
import com.soooner.EplayerPluginLibary.util.StringUtils;
import com.soooner.playback.entity.EPlaybackSessionInfo;
import com.soooner.source.common.util.DeviceUtil;
import com.soooner.source.common.util.LogUtil;

/**
 * Created by zhaoxu2014 on 14-12-5.
 */
public class PlayerControllerView extends LinearLayout {
    MyControllerListener listener;
    Context context;
    ImageView img_pre, img_next, img_player_state;
    MySeekbar seekbar;
    TextView tv_bar_start, tv_bar_end, tv_bar_current;
    boolean manualPausePlayer;

    long progress;
    long totalTime;
    long playbackTime;
    long playbackBeginTime;
    long playbackEndTime;
    int seekbar_left_padding=0;
    public static int DEVICE_TYPE = DeviceUtil.DEVICE_TYPE_PHONE;

    public enum PlayerState {
        PLAYERSTATE_PLAY, PLAYERSTATE_PAUSE
    }

    PlayerState playerState = PlayerState.PLAYERSTATE_PLAY;


    String current_hms="";

    public boolean isManualPausePlayer() {
        return manualPausePlayer;
    }

    public void setManualPausePlayer(boolean manualPausePlayer) {
        this.manualPausePlayer = manualPausePlayer;
    }

    public static final  int MSG_INIT_PROGRESS_TIME=100;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_INIT_PROGRESS_TIME:{
                    PlayBackTime playBackTime= (PlayBackTime) msg.obj;
                    if (playBackTime.totalTime <= 0) {
                        return;
                    }

                    playBackTime.progress =  playBackTime.progress >  playBackTime.totalTime ?  playBackTime.totalTime :  playBackTime.progress;
                    playBackTime.playbackBeginTime =  playBackTime.playbackBeginTime >  playBackTime.playbackEndTime ?  playBackTime.playbackEndTime :  playBackTime.playbackBeginTime;
                    playBackTime.playbackTime =  playBackTime.playbackTime >  playBackTime.playbackEndTime ?  playBackTime.playbackEndTime :  playBackTime.playbackTime;
                    playBackTime.playbackTime =  playBackTime.playbackTime <  playBackTime.playbackBeginTime ?  playBackTime.playbackBeginTime :  playBackTime.playbackTime;

                    if (PlayerControllerView.this.totalTime !=  playBackTime.totalTime) {
                        PlayerControllerView.this.totalTime =  playBackTime.totalTime;
                        seekbar.setMax((int) totalTime);
                    }


                    if (PlayerControllerView.this.progress !=  playBackTime.progress) {
                        PlayerControllerView.this.progress =  playBackTime.progress;
                        seekbar.setProgress((int) progress);
                    }

                    if (PlayerControllerView.this.playbackBeginTime !=  playBackTime.playbackBeginTime) {
                        PlayerControllerView.this.playbackBeginTime =  playBackTime.playbackBeginTime;
                        //String beginTime = DateUtil.getHms(playbackBeginTime);
                        String beginTime = DateUtil.getHmsFromMilliSecond(0);
                        tv_bar_start.setText(beginTime);
                    }

                    PlayerControllerView.this.playbackEndTime =  playBackTime.playbackEndTime;
                   // String endTime = DateUtil.getHms(playbackEndTime);

                    String endTime = DateUtil.getHmsFromMilliSecond(totalTime);
                    tv_bar_end.setText(endTime);

                    PlayerControllerView.this.playbackTime =  playBackTime.playbackTime;
                    //String hms = DateUtil.getHms(playbackTime);

                    String hms = DateUtil.getHmsFromMilliSecond(progress);
                    changeProgressTime(hms);
                    break;
                }
            }
            super.handleMessage(msg);
        }
    };

    public PlayerControllerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PlayerControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerControllerView(Context context) {
        super(context);

    }

    public void init(Context context, int DEVICE_TYPE, MyControllerListener listener) {
        this.context = context;
        this.listener = listener;
        this.DEVICE_TYPE = DEVICE_TYPE;
        View view = null;
        switch (DEVICE_TYPE) {
            case DeviceUtil.DEVICE_TYPE_PHONE: {
                view = View.inflate(context, R.layout.playercontroller_phone, null);
                seekbar_left_padding= (int) context.getResources().getDimension(R.dimen.phone_seekbar_left_padding);
                break;
            }
            case DeviceUtil.DEVICE_TYPE_PAD: {
                view = View.inflate(context, R.layout.playercontroller_pad, null);
                seekbar_left_padding= (int) context.getResources().getDimension(R.dimen.pad_seekbar_left_padding);
                break;
            }
        }

        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        initView(view);

        this.addView(view);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        img_pre.setEnabled(enabled);
        img_next.setEnabled(enabled);
        img_player_state.setEnabled(enabled);
        seekbar.setEnabled(enabled);

    } 


    private void initView(View view) {
        img_pre = (ImageView) view.findViewById(R.id.img_pre);
        img_next = (ImageView) view.findViewById(R.id.img_next);
        img_player_state = (ImageView) view.findViewById(R.id.img_player_state);
        img_pre.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {


                if (null != listener) {

                    listener.previousPlaybackPPT();
                }
            }
        });
        img_next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (null != listener) {

                    listener.nextPlaybackPPT();
                }
            }
        });
        img_player_state.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (null != listener) {

                    switch (playerState) {
                        case PLAYERSTATE_PLAY: {
                            setManualPausePlayer(true);
                            listener.pausePlayback();
                            playerState = PlayerState.PLAYERSTATE_PAUSE;
                            break;
                        }
                        case PLAYERSTATE_PAUSE: {
                            setManualPausePlayer(false);
                            listener.resumePlayback();
                            playerState = PlayerState.PLAYERSTATE_PLAY;
                            break;
                        }
                    }

                }
            }
        });

        seekbar = (MySeekbar) view.findViewById(R.id.seekbar);
        seekbar.setListener(sizeChangedListener);
        tv_bar_start = (TextView) view.findViewById(R.id.tv_bar_start);
        tv_bar_end = (TextView) view.findViewById(R.id.tv_bar_end);
        tv_bar_current = (TextView) view.findViewById(R.id.tv_bar_current);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override//fromUser如果是用户触发的改变则返回True
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {

                if(fromUser) {
                   //String time = EPlaybackSessionInfo.sharedSessionInfo().loadTimeWithProgress(i);
                    String time = DateUtil.getHmsFromMilliSecond(i);
                    changeProgressTime(time);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (null != listener) {
                    listener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setManualPausePlayer(false);
                if (null != listener) {
                    listener.onStopTrackingTouch(seekBar);
                }
            }
        });

    }


    /*
        改变进度条当前时间
       */
    public void changeProgressTime( String hms){
        LogUtil.d("changeProgressTime", "seekbar.getWidth():"+seekbar.getWidth());
        if(!StringUtils.isValid(hms)){
            return;
        }
        current_hms=hms;

        tv_bar_current.setText(hms);
        int width = seekbar.getWidth()-2*seekbar_left_padding;

        long currentPostion = (long) (seekbar.getProgress()/ ((double)seekbar.getMax()/(width) ));
        int tvWidth = tv_bar_end.getWidth();
        int tvheight = tv_bar_end.getHeight();
        int left = 0;
        if ((currentPostion - tvWidth / 2) <= 0) {
            left = 0;
        } else if ((currentPostion + tvWidth / 2) > width) {
            left = width - tvWidth;
        } else {
            left = (int) currentPostion - tvWidth / 2;
        }
        LogUtil.d("leftlfet", "tvWidth:" + tvWidth + ";left:" + left + ",currentPostion:" + currentPostion);
        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(tvWidth, tvheight, left, 0);
        tv_bar_current.setLayoutParams(lp);
    }

    /*
      phone横竖切换时要重新设置tv_bar_current的位置
      */
    public void changeProgressTime( String hms,int w){
        LogUtil.d("changeProgressTime", "seekbar.getWidth():"+seekbar.getWidth());
        if(!StringUtils.isValid(hms)){
            return;
        }
        current_hms=hms;

        tv_bar_current.setText(hms);
        int width = w-2*seekbar_left_padding;

        long currentPostion = (long) (seekbar.getProgress()/ ((double)seekbar.getMax()/(width) ));
        int tvWidth = tv_bar_end.getWidth();
        int tvheight = tv_bar_end.getHeight();
        int left = 0;
        if ((currentPostion - tvWidth / 2) <= 0) {
            left = 0;
        } else if ((currentPostion + tvWidth / 2) > width) {
            left = width - tvWidth;
        } else {
            left = (int) currentPostion - tvWidth / 2;
        }
        LogUtil.d("leftlfet", "tvWidth:" + tvWidth + ";left:" + left + ",currentPostion:" + currentPostion);
        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(tvWidth, tvheight, left, 0);
        tv_bar_current.setLayoutParams(lp);
    }



    public void showProgressTime(long pg, long tt, long pbt, long pbbt, long pbet) {
        PlayBackTime playBackTime = new PlayBackTime();
        playBackTime.progress = pg;
        playBackTime.totalTime = tt;
        playBackTime.playbackTime = pbt;
        playBackTime.playbackBeginTime = pbbt;
        playBackTime.playbackEndTime = pbet;

        Message message = Message.obtain();
        message.what = MSG_INIT_PROGRESS_TIME;
        message.obj = playBackTime;
        handler.sendMessage(message);


    }

    public void changePlayerState(PlayerState ps){
        playerState=ps;
        switch (playerState) {
            case PLAYERSTATE_PLAY: {
                switch (DEVICE_TYPE) {
                    case DeviceUtil.DEVICE_TYPE_PHONE: {
                        img_player_state.setBackgroundResource(R.drawable.player_play);
                        break;
                    }
                    case DeviceUtil.DEVICE_TYPE_PAD: {
                        img_player_state.setBackgroundResource(R.drawable.pad_player_play);
                        break;
                    }
                }

                break;
            }
            case PLAYERSTATE_PAUSE: {
                switch (DEVICE_TYPE) {
                    case DeviceUtil.DEVICE_TYPE_PHONE: {
                        img_player_state.setBackgroundResource(R.drawable.player_pause);
                        break;
                    }
                    case DeviceUtil.DEVICE_TYPE_PAD: {
                        img_player_state.setBackgroundResource(R.drawable.pad_player_pause);
                        break;
                    }
                }
                break;
            }
        }

    }


    public interface MyControllerListener extends SeekBar.OnSeekBarChangeListener {
        public void previousPlaybackPPT();

        public void nextPlaybackPPT();

        public void pausePlayback();

        public void resumePlayback();
    }

    public static class PlayBackTime{
        long progress;
        long totalTime;
        long playbackTime;
        long playbackBeginTime;
        long playbackEndTime;
    }
    MySeekbar.SizeChangedListener sizeChangedListener=new MySeekbar.SizeChangedListener(){
        @Override
        public void sizeChanged(int w) {
            if (DEVICE_TYPE == DeviceUtil.DEVICE_TYPE_PHONE) {
                changeProgressTime(current_hms,w);
            }
        }
    };


}
