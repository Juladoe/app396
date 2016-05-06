package com.plugin.edusoho.bdvideoplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by howzhi on 14-8-5.
 */
public class BdVideoPlayerFragment extends Fragment implements OnPreparedListener, OnCompletionListener, OnErrorListener,
        OnInfoListener, OnPlayingBufferCacheListener {
    protected final String TAG = "BdVideoPlayerFragment";

    private String AK = "6ZB2kShzunG7baVCPLWe7Ebc";
    private String SK = "wt18pcUSSryXdl09jFvGvsuNHhGCZTvF";

    protected String mCurMediaSource = null;
    protected String mVideoSource = null;
    protected String mVideoHead = null;

    private Activity mContext = null;
    private BVideoView mVV = null;

    private LinearLayout llayoutPlayerControlPanel;
    private ImageView ivVideoPlay = null;
    private ImageView ivVideoReplay = null;
    private CheckBox chkFullScreen = null;
    private View popupView;
    private PopupWindow popupWindow;

    protected RelativeLayout rlayoutTitleStatus;
    protected ImageView ivBack;
    protected TextView tvVideoTitle;
    protected ImageView ivShare;
    protected TextView tvSDVideo;
    protected TextView tvHDVideo;
    protected TextView tvSHDVideo;
    protected TextView tvStreamType;
    protected ImageView ivQuestion;
    protected ImageView ivNote;

    private SeekBar mProgress = null;
    private TextView mDuration = null;
    private TextView mCurrPosition = null;

    protected boolean mIsHwDecode = false;
    protected boolean mIsPlayEnd;
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
        PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED, PLAYER_PAUSE
    }

    protected enum PLAYER_HEAD_STATUS {
        PLAYER_START, PLAYER_END, PLAYER_IDLE
    }

    protected PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;

    /**
     * 记录播放位置
     */
    protected int mLastPos = 0;
    protected int mCurrentPos = 0;
    protected int mDurationCount = 0;
    private PLAYER_HEAD_STATUS mPlayHeadStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "bd fragment create");
        mContext = getActivity();

        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, POWER_LOCK);
        initSoLib();
        setMediaSource();
        autoHideTimer = new Timer();
        mPlayHeadStatus = PLAYER_HEAD_STATUS.PLAYER_IDLE;
        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void setMediaSource() {
        Bundle bundle = getArguments();
        mIsHwDecode = bundle.getBoolean("isHW", false);
        isCacheVideo = bundle.getBoolean("from_cache", false);
        mVideoSource = getUrlPath(bundle.getString("streamUrls"));
        int decodeMode = TextUtils.isEmpty(mVideoSource) || mVideoSource.contains("Lesson/getLocalVideo") ? BVideoView.DECODE_HW : BVideoView.DECODE_SW;
        mDecodeMode = bundle.getInt("decode", decodeMode);
        mVideoHead = getUrlPath(bundle.getString("headUrl"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.controllerplaying, container, false);
        initView(view);

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
        if (null != mWakeLock && (!mWakeLock.isHeld())) {
            mWakeLock.acquire();
        }

        ivVideoReplay.setVisibility(View.GONE);
        resumePlay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: BdVideoPlayer ");
        /**
         * 结束后台事件处理线程
         */

        mHandlerThread.quit();
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

    private void initSoLib() {
        File libDir = mContext.getDir(getCpuType() + "lib", Context.MODE_PRIVATE);
        if (new File(libDir, "libcyberplayer.so").exists()) {
            mSoLibDir = libDir.getAbsolutePath();
        }
    }

    protected void recordCurrentPosition() {
        if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
            mLastPos = mVV.getCurrentPosition();
        }
    }

    /**
     * 初始化界面
     */
    protected void initView(View view) {
        llayoutPlayerControlPanel = (LinearLayout) view.findViewById(R.id.llayout_player_control_panel);
        ivVideoPlay = (ImageView) view.findViewById(R.id.play_btn);
        chkFullScreen = (CheckBox) view.findViewById(R.id.full_btn);
        ivVideoReplay = (ImageView) view.findViewById(R.id.video_replay);
        mProgress = (SeekBar) view.findViewById(R.id.media_progress);
        mDuration = (TextView) view.findViewById(R.id.time_total);
        mCurrPosition = (TextView) view.findViewById(R.id.time_current);
        rlayoutTitleStatus = (RelativeLayout) view.findViewById(R.id.rlayout_title_status);
        ivBack = (ImageView) view.findViewById(R.id.iv_back);
        tvVideoTitle = (TextView) view.findViewById(R.id.tv_video_title);
        ivShare = (ImageView) view.findViewById(R.id.iv_share);
        ivQuestion = (ImageView) view.findViewById(R.id.iv_question);
        ivNote = (ImageView) view.findViewById(R.id.iv_note);
        tvStreamType = (TextView) view.findViewById(R.id.tv_stream);
        registerCallbackForControl();
        /**
         * 设置ak及sk的前16位
         */
        BVideoView.setAKSK(AK, SK);
        if (!TextUtils.isEmpty(mSoLibDir)) {
            BVideoView.setNativeLibsDirectory(mSoLibDir);
        }
        mVV = (BVideoView) view.findViewById(R.id.video_view);

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
         *关联BMediaController
         */
        //mVV.setMediaController(mVVCtl);
        /**
         *设置解码模式
         */

        mVV.setDecodeMode(mDecodeMode);
        if (mDecodeMode == BVideoView.DECODE_HW) {
            mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        }
        llayoutPlayerControlPanel.setOnTouchListener(new View.OnTouchListener() {
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

    // region handler

    Handler mUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * 更新进度及时间
                 */
                case UI_EVENT_UPDATE_CURRPOSITION:
                    mCurrentPos = mVV.getCurrentPosition();
                    int duration = mVV.getDuration();
                    updateTextViewWithTimeFormat(mCurrPosition, mCurrentPos);
                    updateTextViewWithTimeFormat(mDuration, duration);
                    mProgress.setMax(duration);
                    mProgress.setProgress(mCurrentPos);
                    mUIHandler.sendEmptyMessageDelayed(UI_EVENT_UPDATE_CURRPOSITION, 200);
                    break;
                case UI_EVENT_ERROR:
                    showErrorDialog();
                    break;
                case UI_EVENT_FINISH:
                    ivVideoReplay.setVisibility(View.VISIBLE);
                    ivVideoPlay.setImageResource(R.drawable.icon_video_play);
                    break;
                case HIDE:
                    hideController();
                    break;
                case UI_EVENT_PAUSE:
                    Log.d(TAG, "UI_EVENT_PAUSE");
                    ivVideoPlay.setImageResource(R.drawable.icon_video_play);
                    break;
                case UI_EVENT_PLAY:
                    ivVideoPlay.setImageResource(R.drawable.icon_video_pause);
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

    // endregion

    // region player action

    private void playVideo() {
        /**
         * 如果已经播放了，等待上一次播放结束
         */
        if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
            synchronized (SYNC_Playing) {
                try {
                    SYNC_Playing.wait();
                    Log.v(TAG, "wait player status to idle");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 设置播放url
         */
        mVV.setVideoPath(mCurMediaSource);

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

    protected void startVideo() {
        try {
            checkVideoCanPlayer();
            mVV.start();
            mVV.setTag("start");
        } catch (Exception e) {
            Log.d(TAG, "error:" + e.getMessage());
        }
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

    // endregion

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

    private void checkVideoCanPlayer() {
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
        }
    };

    private boolean mIsShowController = true;
    private int mIsShowControllerCount;

    private void hideController() {
        mIsShowController = false;
        llayoutPlayerControlPanel.setVisibility(View.GONE);
        rlayoutTitleStatus.setVisibility(View.GONE);
    }

    private void showController() {
        mIsShowController = true;
        llayoutPlayerControlPanel.setVisibility(View.VISIBLE);
        rlayoutTitleStatus.setVisibility(View.VISIBLE);
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
        return playUrl;
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
        Log.v(TAG, "onError what:" + what + " extra:" + extra);
        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }

        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
        mUIHandler.sendEmptyMessage(UI_EVENT_ERROR);
        return true;
    }

    private void changeDecodeMode() {
        mDecodeMode = BVideoView.DECODE_HW;
        mVV.setDecodeMode(mDecodeMode);
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mEventHandler.sendEmptyMessage(EVENT_START);
    }

    private void showErrorDialog() {
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
        chkFullScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        ivVideoPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mVV.isPlaying()) {
                    mIsPlayEnd = true;
                    ivVideoPlay.setImageResource(R.drawable.icon_video_play);
                    /**
                     * 暂停播放
                     */
                    mVV.pause();
                } else {
                    mIsPlayEnd = false;
                    ivVideoPlay.setImageResource(R.drawable.icon_video_pause);
                    if (mPlayerStatus == PLAYER_STATUS.PLAYER_IDLE) {
                        ivVideoReplay.setVisibility(View.GONE);
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
                updateTextViewWithTimeFormat(mCurrPosition, progress);
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

        ivVideoReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivVideoReplay.setVisibility(View.GONE);
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
    }

    protected void showPopupWindows() {
        if (popupWindow != null) {
            popupWindow.showAsDropDown(tvStreamType, dip2px(mContext, 60) / -4, mProgress.getHeight() + 10);
        } else {
            Toast.makeText(mContext, "视频信息获取失败", Toast.LENGTH_LONG).show();
        }
    }

    protected void initPopupWindows(List<StreamInfo> streamInfoLists) {
        if (popupWindow == null) {
            LinearLayout popupView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.popup_stream_windows, null);
            createMediaTypeTextViews(popupView, streamInfoLists);
            popupWindow = new PopupWindow(mContext);
            popupWindow.setHeight(dip2px(mContext, 15 * 2 + 30 * (streamInfoLists.size() - 1)) + sp2px(mContext, 14 * streamInfoLists.size()) + dip2px(mContext, 15));
            popupWindow.setWidth(dip2px(mContext, 60));
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(127, 52, 53, 55)));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setContentView(popupView);
        }
    }

    private void createMediaTypeTextViews(final LinearLayout parentView, List<StreamInfo> streamInfoLists) {
        int TopBottomMargin = dip2px(mContext, 15);
        int middleMargin = dip2px(mContext, 30);
        for (int i = 0; i < streamInfoLists.size(); i++) {
            TextView tv = new TextView(mContext);
            final StreamInfo streamInfo = streamInfoLists.get(i);
            if (i == 0) {
                tv.setTextColor(getResources().getColor(R.color.video_checked));
            } else {
                tv.setTextColor(getResources().getColor(android.R.color.white));
            }
            tv.setText(convertSourceName(streamInfo.name));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_x_s));
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            tv.setTag(streamInfo);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StreamInfo si = (StreamInfo) v.getTag();
                    mCurMediaSource = si.src;
                    mLastPos = mVV.getCurrentPosition();
                    tvStreamType.setText(((TextView) v).getText());
                    initMediaSourceTextViewColor(parentView, si.name);
                    mEventHandler.sendEmptyMessage(EVENT_REPLAY);
                }
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i == streamInfoLists.size() - 1 && streamInfoLists.size() == 1) {
                params.setMargins(0, TopBottomMargin, 0, TopBottomMargin);
            } else {
                if (i == 0) {
                    params.setMargins(0, TopBottomMargin, 0, 0);
                } else if (i == streamInfoLists.size() - 1) {
                    params.setMargins(0, middleMargin, 0, TopBottomMargin);
                } else {
                    params.setMargins(0, middleMargin, 0, 0);
                }
            }
            parentView.addView(tv, params);
        }
    }

    private void initMediaSourceTextViewColor(LinearLayout parentView, String name) {
        int size = parentView.getChildCount();
        for (int i = 0; i < size; i++) {
            TextView tv = (TextView) parentView.getChildAt(i);
            StreamInfo streamInfo = (StreamInfo) tv.getTag();
            if (name.equals(streamInfo.name)) {
                tv.setTextColor(getResources().getColor(R.color.video_checked));
            } else {
                tv.setTextColor(getResources().getColor(android.R.color.white));
            }
        }
    }

    public String convertSourceName(String name) {
        if ("SD".equals(name)) {
            return "标清";
        } else if ("HD".equals(name)) {
            return "高清";
        } else {
            return "超清";
        }
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }
}

