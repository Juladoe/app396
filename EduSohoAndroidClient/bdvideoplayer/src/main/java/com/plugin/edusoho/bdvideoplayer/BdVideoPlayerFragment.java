package com.plugin.edusoho.bdvideoplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by howzhi on 14-8-5.
 */
public class BdVideoPlayerFragment extends Fragment implements OnPreparedListener,
        OnCompletionListener,
        OnErrorListener,
        OnInfoListener,
        OnPlayingBufferCacheListener {
    protected final String TAG = "BdVideoPlayerFragment";

    private String AK = "6ZB2kShzunG7baVCPLWe7Ebc";
    private String SK = "wt18pcUSSryXdl09jFvGvsuNHhGCZTvF";

    protected String mVideoSource = null;
    protected String mVideoHead = null;

    protected BVideoView mVV = null;
    private Activity mContext = null;

    private ImageView mPlaybtn = null;
    private ImageView mBackbtn = null;
    private ImageView mForwardbtn = null;
    private ImageView mReplayBtn = null;
    private CheckBox mFullBtn = null;

    private LinearLayout mController = null;

    private SeekBar mProgress = null;
    private TextView mDuration = null;
    private TextView mCurrPostion = null;

    private boolean mIsHwDecode = false;
    private boolean mIsPlayEnd;
    protected boolean isCacheVideo;
    protected int mDecodeMode;

    protected EventHandler mEventHandler;
    protected HandlerThread mHandlerThread;
    private String mSoLibDir;

    protected final Object SYNC_Playing = new Object();

    public final int EVENT_START = 0;
    public final int EVENT_PLAY = 3;
    public final int EVENT_PAUSE = 4;
    public final int EVENT_FINISH = 5;
    public final int EVENT_REPLAY = 6;

    private final int UI_EVENT_UPDATE_CURRPOSITION = 1;
    private final int UI_EVENT_ERROR = 5;
    private final int UI_EVENT_PAUSE = 3;
    private final int UI_EVENT_PLAY = 4;
    private final int UI_EVENT_FINISH = 6;
    private static final int HIDE = 2;

    protected PowerManager.WakeLock mWakeLock = null;
    private static final String POWER_LOCK = "BdVideoPlayerActivity";

    /**
     * 播放状态
     */
    protected enum PLAYER_STATUS {
        PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED, PLAYER_PAUSE;
    }

    protected enum PLAYER_HEAD_STATUS {
        PLAYER_START, PLAYER_END, PLAYER_IDLE;
    }

    protected PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;


    /**
     * 记录播放位置
     */
    protected int mLastPos = 0;
    protected int mCurrentPos = 0;
    protected int mDurationCount = 0;
    private PLAYER_HEAD_STATUS mPlayHeadStatus;

    Handler mUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * 更新进度及时间
                 */
                case UI_EVENT_UPDATE_CURRPOSITION:
                    mCurrentPos = mVV.getCurrentPosition();
                    int duration = mVV.getDuration();

                    updateTextViewWithTimeFormat(mCurrPostion, mCurrentPos);
                    updateTextViewWithTimeFormat(mDuration, duration);
                    mProgress.setMax(duration);
                    mProgress.setProgress(mCurrentPos);
                    mUIHandler.sendEmptyMessageDelayed(UI_EVENT_UPDATE_CURRPOSITION, 200);
                    break;
                case UI_EVENT_ERROR:
                    showErrorDialog(msg.arg1, msg.arg2);
                    break;
                case UI_EVENT_FINISH:
                    mReplayBtn.setVisibility(View.VISIBLE);
                    mPlaybtn.setImageResource(R.drawable.video_play);
                    break;
                case HIDE:
                    hideController();
                    break;
                case UI_EVENT_PAUSE:
                    Log.d(TAG, "UI_EVENT_PAUSE");
                    mPlaybtn.setImageResource(R.drawable.video_play);
                    break;
                case UI_EVENT_PLAY:
                    mPlaybtn.setImageResource(R.drawable.video_pause);
                    break;
                default:
                    break;
            }
        }
    };

    public class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_START:
                    Log.d(TAG, "EVENT_START");
                    if (isCacheVideo || mVideoHead == null || mPlayHeadStatus == PLAYER_HEAD_STATUS.PLAYER_END) {
                        playVideo();
                    } else {
                        playHeadUrl();
                    }
                    break;
                case EVENT_PAUSE:
                    mVV.pause();
                    break;
                case EVENT_REPLAY:
                    Log.d(TAG, "EVENT_REPLAY");
                    startVideo();
                    mVV.seekTo(mLastPos);
                    mLastPos = 0;
                    break;
                case EVENT_FINISH:
                    mUIHandler.sendEmptyMessage(UI_EVENT_PAUSE);
                    Log.d(TAG, "EVENT_FINISH");
                    break;
                default:
                    break;
            }
        }
    }

    private void playVideo() {
        Log.v(TAG, "playVideo " + mVideoSource);
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
        startVideo();

        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
    }

    private void playHeadUrl() {
        Log.v(TAG, "playHeadUrl " + mVideoHead);
        mVV.setVideoPath(mVideoHead);
        /**
         * 显示或者隐藏缓冲提示
         */
        mVV.showCacheInfo(true);
        /**
         * 开始播放
         */

        startVideo();

        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
        mPlayHeadStatus = PLAYER_HEAD_STATUS.PLAYER_START;
    }

    private String getCpuType() {
        String CPU_ABI = android.os.Build.CPU_ABI;
        if (TextUtils.isEmpty(CPU_ABI)) {
            CPU_ABI = Build.CPU_ABI2;
        }

        return TextUtils.isEmpty(CPU_ABI) ? "" : CPU_ABI;
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alertDialog = builder.setTitle("播放提示")
                .setMessage(message)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        getActivity().finish();
                    }
                })
                .create();
        alertDialog.show();
    }

    protected void startVideo() {
        try {
            chackVideoCanPlayer();
            mVV.start();
            mVV.setTag("start");
        } catch (Exception e) {
            Log.d(TAG, "error:" + e.getMessage());
        }
    }

    private int getCpuBit() {
        int cupBit;
        try {
            Field SUPPORTED_64_BIT_ABIS_FIELD = android.os.Build.class.getField("SUPPORTED_64_BIT_ABIS");
            SUPPORTED_64_BIT_ABIS_FIELD.setAccessible(true);
            String[] SUPPORTED_64_BIT_ABIS = (String[])  SUPPORTED_64_BIT_ABIS_FIELD.get(null);
            cupBit = SUPPORTED_64_BIT_ABIS == null || SUPPORTED_64_BIT_ABIS.length == 0 ? 32 : 64;
        } catch (Exception e) {
            cupBit = 32;
        }

        return cupBit;
    }

    private void chackVideoCanPlayer() {
        String CPU_ABI = getCpuType();
        if (CPU_ABI.contains("x86")) {
            File libDir = mContext.getDir("x86lib", Context.MODE_PRIVATE);
            if (new File(libDir, "libcyberplayer.so").exists()) {
                return;
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    updateSoLib();
                }
            });
            throw new RuntimeException("not_support");
        }
    }

    private void updateSoLib() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d(TAG, "" + Thread.currentThread());
        new AsyncTask<Integer, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Integer... params) {
                SoLibManager.DownProcessListener listener = new SoLibManager.DownProcessListener() {
                    @Override
                    public void update(int count, int process) {
                        publishProgress(count, process);
                    }
                };
                return new SoLibManager(listener).downPlayerSoLib("x86", mContext);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                Log.d(TAG, "" + Thread.currentThread());
                progressDialog.setMessage(String.format(
                        "正在下载解码包:%s/%s"
                        , Formatter.formatFileSize(mContext, values[0]),
                        Formatter.formatFileSize(mContext, values[1])
                ));
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    progressDialog.cancel();
                    showAlertDialog("解码库更新完成，请重新打开视频播放");
                    return;
                }
                showAlertDialog("暂不支持在该设备上播放视频");
            }

        }.execute(0);
    }

    private Timer autoHideTimer;
    private TimerTask autoHideTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (!mIsShowController) {
                return;
            }
            if (mIsShowController && mIsShowControllerCount > 5) {
                mIsShowControllerCount = 0;
                mUIHandler.obtainMessage(HIDE).sendToTarget();
                return;
            }
            mIsShowControllerCount++;
            Log.d(null, "mIsShowControllerCount " + mIsShowControllerCount);
        }
    };

    private boolean mIsShowController = true;
    private int mIsShowControllerCount;

    private void hideController() {
        mIsShowController = false;
        mController.setVisibility(View.GONE);
    }

    private void showController() {
        mIsShowController = true;
        mController.setVisibility(View.VISIBLE);
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
             * 如果已经播放，先停止播放
             */
            if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                mVV.stopPlayback();
            }

            /**
             * 发起一次新的播放任务
             */
            if (mEventHandler.hasMessages(EVENT_START))
                mEventHandler.removeMessages(EVENT_START);
            mEventHandler.sendEmptyMessage(EVENT_START);
        }
    };

    private View.OnClickListener mNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
        Log.d(TAG, "bd fragment create");
        mContext = getActivity();

        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, POWER_LOCK);

        Bundle bundle = getArguments();
        mIsHwDecode = bundle.getBoolean("isHW", false);
        isCacheVideo = bundle.getBoolean("from_cache", false);
        Log.d(TAG, "isCacheVideo " + isCacheVideo);

        initSoLib();
        mVideoSource = getUrlPath(bundle.getString("mediaUrl"));

        int decodeMode = TextUtils.isEmpty(mVideoSource) || mVideoSource.contains("Lesson/getLocalVideo") ? BVideoView.DECODE_HW : BVideoView.DECODE_SW;
        mDecodeMode = bundle.getInt("decode", decodeMode);
        mVideoHead = getUrlPath(bundle.getString("headUrl"));
        autoHideTimer = new Timer();

        mPlayHeadStatus = PLAYER_HEAD_STATUS.PLAYER_IDLE;
    }

    private void initSoLib() {
        File libDir = mContext.getDir(getCpuType() + "lib", Context.MODE_PRIVATE);
        if (new File(libDir, "libcyberplayer.so").exists()) {
            mSoLibDir = libDir.getAbsolutePath();
        }
    }

    private String getUrlPath(String uriPath) {
        String playUrl = null;
        if (uriPath == null) {
            return null;
        }
        Uri uri = Uri.parse(uriPath);
        if (null != uri) {
            String scheme = uri.getScheme();
            if (null != scheme) {
                playUrl = uri.toString();
            } else {
                playUrl = uri.getPath();
            }
        }
        Log.d(TAG, "playUrl->" + playUrl);
        return playUrl;
    }

    /**
     * 初始化界面
     */
    private void initUI(View view) {
        mPlaybtn = (ImageView) view.findViewById(R.id.play_btn);
        mFullBtn = (CheckBox) view.findViewById(R.id.full_btn);
        mReplayBtn = (ImageView) view.findViewById(R.id.video_replay);

        mProgress = (SeekBar) view.findViewById(R.id.media_progress);
        mDuration = (TextView) view.findViewById(R.id.time_total);
        mCurrPostion = (TextView) view.findViewById(R.id.time_current);

        registerCallbackForControl();
        /**
         * 设置ak及sk的前16位
         */
        BVideoView.setAKSK(AK, SK);
        if (!TextUtils.isEmpty(mSoLibDir)) {
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
        mController = (LinearLayout) view.findViewById(R.id.video_controller);
        /**
         *注册listener
         */
        mVV.setOnPreparedListener(this);
        mVV.setOnCompletionListener(this);
        mVV.setOnErrorListener(this);
        mVV.setOnInfoListener(this);
        mVV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mIsShowControllerCount = 0;
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
         *设置解码模式
         */

        mVV.setDecodeMode(mDecodeMode);
        if (mDecodeMode == BVideoView.DECODE_HW) {
            mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        }
        mController.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mIsShowControllerCount = 0;
                    return true;
                }
                return false;
            }
        });
        autoHideTimer.schedule(autoHideTimerTask, 1000, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        /**
         *在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
         */
        if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
            mLastPos = mVV.getCurrentPosition();
            mVV.pause();
            mVV.setTag("pause");
            Log.v(TAG, "mVV onPause");
            mUIHandler.sendEmptyMessage(UI_EVENT_PAUSE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        if (null != mWakeLock && (!mWakeLock.isHeld())) {
            mWakeLock.acquire();
        }

        mReplayBtn.setVisibility(View.GONE);
        resumePlay();
    }

    protected void resumePlay() {
        /**
         *发起一次播放任务,当然您不一定要在这发起
         */
        if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED
                || mPlayerStatus == PLAYER_STATUS.PLAYER_PAUSE) {
            mEventHandler.sendEmptyMessage(EVENT_REPLAY);
        } else {
            mEventHandler.sendEmptyMessage(EVENT_START);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**
         * 结束后台事件处理线程
         */

        mHandlerThread.quit();
        Log.v(TAG, "onDestroy");
        if (mWakeLock != null) {
            try {
                mWakeLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (autoHideTimer != null) {
            autoHideTimer.cancel();
            autoHideTimer = null;
        }
    }

    @Override
    public boolean onInfo(int what, int extra) {
        switch (what) {
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
    }

    /**
     * 播放出错
     */
    @Override
    public boolean onError(int what, int extra) {
        mLastPos = mCurrentPos > 0 ? mCurrentPos + 16 : 0;
        Log.v(TAG, "onError what:" + what + " mCurrentPos:" + extra);
        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }

        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
        mUIHandler.obtainMessage(UI_EVENT_ERROR, what, extra).sendToTarget();
        return true;
    }

    private void changeDecodeMode() {
        mDecodeMode = BVideoView.DECODE_HW;
        mVV.setDecodeMode(mDecodeMode);
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mEventHandler.sendEmptyMessage(EVENT_START);
    }

    protected void showErrorDialog(int what, int extra) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog alertDialog = builder.setTitle("播放提示")
                .setMessage("该课时视频不能播放")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        getActivity().finish();
                    }
                })
                .create();
        alertDialog.show();
    }

    /**
     * 为控件注册回调处理函数
     */
    private void registerCallbackForControl() {
        mFullBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
                if (mVV.isPlaying()) {
                    mIsPlayEnd = true;
                    mPlaybtn.setImageResource(R.drawable.video_play);
                    /**
                     * 暂停播放
                     */
                    mVV.pause();
                } else {
                    mIsPlayEnd = false;
                    mPlaybtn.setImageResource(R.drawable.video_pause);
                    if (mPlayerStatus == PLAYER_STATUS.PLAYER_IDLE) {
                        mReplayBtn.setVisibility(View.GONE);
                        resumePlay();
                        return;
                    }
                    /**
                     * 继续播放
                     */
                    mVV.resume();
                }

            }
        });

        SeekBar.OnSeekBarChangeListener osbc1 = new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                updateTextViewWithTimeFormat(mCurrPostion, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                /**
                 * SeekBar开始seek时停止更新
                 */
                mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
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

        mReplayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReplayBtn.setVisibility(View.GONE);
                resumePlay();
            }
        });
    }

    private void updateTextViewWithTimeFormat(TextView view, int second) {
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
        Log.v(TAG, "onCompletion");

        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
            Log.v(TAG, "SYNC_Playing notify");
        }

        boolean isPlayEnd = mCurrentPos > 0 && mCurrentPos >= mDurationCount;
        if (isPlayEnd && mPlayHeadStatus == PLAYER_HEAD_STATUS.PLAYER_START) {
            Log.v(TAG, "start media");
            mPlayHeadStatus = PLAYER_HEAD_STATUS.PLAYER_END;
            mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
            mEventHandler.sendEmptyMessage(EVENT_START);
            return;
        }

        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mUIHandler.sendEmptyMessage(UI_EVENT_FINISH);
    }

    /**
     * 播放准备就绪
     */
    @Override
    public void onPrepared() {
        Log.v(TAG, "onPrepared" + mPlayerStatus);
        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
        mUIHandler.sendEmptyMessage(UI_EVENT_PLAY);
        mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
        mDurationCount = mVV.getDuration();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                setVideoViewHeight();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setVideoViewHeight();
    }

    private void setVideoViewHeight() {
        int screenWidth = mContext.getWindowManager().getDefaultDisplay().getWidth();

        int videoViewHeight = (int) ( screenWidth / ( 16/9.0f ) );
        ViewGroup.LayoutParams lp = mVV.getLayoutParams();
        lp.height = videoViewHeight;
        mVV.setLayoutParams(lp);
    }
}

