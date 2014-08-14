package com.plugin.edusoho.bdvideoplayer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by howzhi on 14-8-5.
 */
public class BdVideoPlayerFragment extends Fragment implements OnPreparedListener,
        OnCompletionListener,
        OnErrorListener,
        OnInfoListener,
        OnPlayingBufferCacheListener
{
    private final String TAG = "BdVideoPlayerFragment";

    private String AK = "6ZB2kShzunG7baVCPLWe7Ebc";
    private String SK = "wt18pcUSSryXdl09jFvGvsuNHhGCZTvF";


    private String mVideoSource = null;

    private BVideoView mVV = null;
    private Activity mContext = null;

    private ImageButton mPlaybtn = null;
    private ImageButton mBackbtn = null;
    private ImageButton mForwardbtn = null;
    private ImageButton mFullBtn = null;

    private LinearLayout mController = null;

    private SeekBar mProgress = null;
    private TextView mDuration = null;
    private TextView mCurrPostion = null;

    private boolean mIsHwDecode = false;

    private EventHandler mEventHandler;
    private HandlerThread mHandlerThread;
    private String mSoLibDir;

    private final Object SYNC_Playing = new Object();

    private final int EVENT_PLAY = 0;
    private final int UI_EVENT_UPDATE_CURRPOSITION = 1;
    private static final int HIDE = 2;

    private PowerManager.WakeLock mWakeLock = null;
    private static final String POWER_LOCK = "BdVideoPlayerActivity";

    /**
     * 播放状态
     */
    private  enum PLAYER_STATUS {
        PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
    }

    private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;


    /**
     * 记录播放位置
     */
    private int mLastPos = 0;

    Handler mUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * 更新进度及时间
                 */
                case UI_EVENT_UPDATE_CURRPOSITION:
                    int currPosition = mVV.getCurrentPosition();
                    int duration = mVV.getDuration();
                    updateTextViewWithTimeFormat(mCurrPostion, currPosition);
                    updateTextViewWithTimeFormat(mDuration, duration);
                    mProgress.setMax(duration);
                    mProgress.setProgress(currPosition);

                    mUIHandler.sendEmptyMessageDelayed(UI_EVENT_UPDATE_CURRPOSITION, 200);
                    break;
                case HIDE:
                    hideController();
                    break;
                default:
                    break;
            }
        }
    };

    class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_PLAY:
                    /**
                     * 如果已经播放了，等待上一次播放结束
                     */
                    if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                        synchronized (SYNC_Playing) {
                            try {
                                SYNC_Playing.wait();
                                Log.v(TAG, "wait player status to idle");
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    /**
                     * 设置播放url
                     */
                    mVV.setVideoPath(mVideoSource);

                    /**
                     * 续播，如果需要如此
                     */
                    if (mLastPos > 0) {

                        mVV.seekTo(mLastPos);
                        mLastPos = 0;
                    }

                    /**
                     * 显示或者隐藏缓冲提示
                     */
                    mVV.showCacheInfo(true);

                    /**
                     * 开始播放
                     */
                    mVV.start();

                    mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
                    break;
                default:
                    break;
            }
        }
    }


    private Timer autoHideTimer;
    private boolean mIsShowController;

    private void hideController()
    {
        mIsShowController = false;
        mController.setVisibility(View.GONE);
    }

    private void showController()
    {
        mIsShowController = true;
        mController.setVisibility(View.VISIBLE);
        autoHideTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mIsShowController) {
                    mUIHandler.obtainMessage(HIDE).sendToTarget();
                }
            }
        }, 3000);
    }

    /**
     * 实现切换示例
     */
    private View.OnClickListener mPreListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.v(TAG, "pre btn clicked");
            /**
             * 如果已经开发播放，先停止播放
             */
            if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
                mVV.stopPlayback();
            }

            /**
             * 发起一次新的播放任务
            */
            if(mEventHandler.hasMessages(EVENT_PLAY))
                mEventHandler.removeMessages(EVENT_PLAY);
            mEventHandler.sendEmptyMessage(EVENT_PLAY);
        }
    };

    private View.OnClickListener mNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.v(TAG, "next btn clicked");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.controllerplaying, container, false);
        initUI(view);

        /**
         * 开启后台事件处理线程
         */
        mHandlerThread = new HandlerThread("event handler thread",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mEventHandler = new EventHandler(mHandlerThread.getLooper());
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, POWER_LOCK);

        Bundle bundle = getArguments();
        mIsHwDecode = bundle.getBoolean("isHW", false);
        mSoLibDir = bundle.getString("soLibDir");
        String url = bundle.getString("url");
        Uri uriPath = Uri.parse(url);
        if (null != uriPath) {
            String scheme = uriPath.getScheme();
            if (null != scheme) {
                mVideoSource = uriPath.toString();
            } else {
                mVideoSource = uriPath.getPath();
            }
        }

        autoHideTimer = new Timer();
    }

    /**
     * 初始化界面
     */
    private void initUI(View view) {
        mPlaybtn = (ImageButton)view.findViewById(R.id.play_btn);
        mBackbtn = (ImageButton)view.findViewById(R.id.back_btn);
        mForwardbtn = (ImageButton)view.findViewById(R.id.forward_btn);
        mFullBtn = (ImageButton)view.findViewById(R.id.full_btn);
        mController = (LinearLayout)view.findViewById(R.id.controlbar);

        mProgress = (SeekBar)view.findViewById(R.id.media_progress);
        mDuration = (TextView)view.findViewById(R.id.time_total);
        mCurrPostion = (TextView)view.findViewById(R.id.time_current);

        registerCallbackForControl();
        /**
         * 设置ak及sk的前16位
         */
        BVideoView.setAKSK(AK, SK);
        if (mSoLibDir != null && ! BdPlayerManager.NORMAL_LIB_DIR.equals(mSoLibDir)) {
            BVideoView.setNativeLibsDirectory(mSoLibDir);
        }

        /**
         *创建BVideoView和BMediaController
        */
        mVV = (BVideoView) view.findViewById(R.id.video_view);
        /*
        mVVCtl = new BMediaController(this);
        mViewHolder.addView(mVV);
        mControllerHolder.addView(mVVCtl);
         */

        /**
         * 注册listener
         */
        mVV.setOnPreparedListener(this);
        mVV.setOnCompletionListener(this);
        mVV.setOnErrorListener(this);
        mVV.setOnInfoListener(this);
        mVV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mIsShowController) {
                        hideController();
                    } else {
                        showController();
                    }
                    return true;
                }
                return false;
            }
        });

        /**
         * 关联BMediaController
        */
        //mVV.setMediaController(mVVCtl);
        /**
         * 设置解码模式
        */
        mVV.setDecodeMode(BVideoView.DECODE_SW);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.v(TAG, "onPause");
        /**
         * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
         */
        if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
            mLastPos = mVV.getCurrentPosition();
            mVV.stopPlayback();
        }
    }


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.v(TAG, "onResume");
        if (null != mWakeLock && (!mWakeLock.isHeld())) {
            mWakeLock.acquire();
        }
        /**
         * 发起一次播放任务,当然您不一定要在这发起
         */
        mEventHandler.sendEmptyMessage(EVENT_PLAY);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        /**
         * 结束后台事件处理线程
         */
        mHandlerThread.quit();
        Log.v(TAG, "onDestroy");
    }

    @Override
    public boolean onInfo(int what, int extra) {
        // TODO Auto-generated method stub
        switch(what){
            /**
             * 开始缓冲
             */
            case BVideoView.MEDIA_INFO_BUFFERING_START:
                break;
            /**
             * 结束缓冲
             */
            case BVideoView.MEDIA_INFO_BUFFERING_END:
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 当前缓冲的百分比， 可以配合onInfo中的开始缓冲和结束缓冲来显示百分比到界面
     */
    @Override
    public void onPlayingBufferCache(int percent) {
        // TODO Auto-generated method stub

    }

    /**
     * 播放出错
     */
    @Override
    public boolean onError(int what, int extra) {
        // TODO Auto-generated method stub
        Log.v(TAG, "onError");
        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
        return true;
    }

    /**
     * 为控件注册回调处理函数
     */
    private void registerCallbackForControl(){
        mFullBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //水平
                int screenOrientation = mContext.getRequestedOrientation();
                if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });

        mPlaybtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (mVV.isPlaying()) {
                    mPlaybtn.setImageResource(R.drawable.play_btn_style);
                    /**
                     * 暂停播放
                     */
                    mVV.pause();
                } else {
                    mPlaybtn.setImageResource(R.drawable.pause_btn_style);
                    /**
                     * 继续播放
                     */
                    mVV.resume();
                }

            }
        });

        /**
         * 实现切换示例
         */
        mBackbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mVV.seekTo(mProgress.getProgress() - 5);
                mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
            }
        });


        mForwardbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mVV.seekTo(mProgress.getProgress() + 5);
                mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
            }
        });

        SeekBar.OnSeekBarChangeListener osbc1 = new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                //Log.v(TAG, "progress: " + progress);
                updateTextViewWithTimeFormat(mCurrPostion, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                /**
                 * SeekBar开始seek时停止更新
                 */
                mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                int iseekPos = seekBar.getProgress();

                /**
                 * SeekBark完成seek时执行seekTo操作并更新界面
                 *
                 */
                mVV.seekTo(iseekPos);
                Log.v(TAG, "seek to " + iseekPos);
                mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
            }
        };
        mProgress.setOnSeekBarChangeListener(osbc1);
    }

    private void updateTextViewWithTimeFormat(TextView view, int second){
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        view.setText(strTemp);
    }

    /**
     * 播放完成
     */
    @Override
    public void onCompletion() {
        // TODO Auto-generated method stub
        Log.v(TAG, "onCompletion");

        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
    }

    /**
     * 播放准备就绪
     */
    @Override
    public void onPrepared() {
        // TODO Auto-generated method stub
        Log.v(TAG, "onPrepared");
        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
        mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
    }
}

