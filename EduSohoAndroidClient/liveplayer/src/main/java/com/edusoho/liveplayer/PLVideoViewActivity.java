package com.edusoho.liveplayer;

/**
 * Created by suju on 16/10/12
 */

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.liveplayer.widget.MediaController;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;

/**
 *  This is a demo activity of PLVideoView
 */
public class PLVideoViewActivity extends AppCompatActivity {

    private static final String TAG = "PLVideoViewActivity";

    private static final int MESSAGE_ID_RECONNECTING = 0x01;

    private MediaController mMediaController;
    private PLVideoView mVideoView;
    private Toast mToast = null;
    private String mVideoPath = null;
    private int mDisplayAspectRatio = PLVideoView.ASPECT_RATIO_FIT_PARENT;
    private boolean mIsActivityPaused = true;
    private View mLoadingView;
    private ImageView mLiveCoverView;
    private TextView mLiveTitleView;
    private TextView mLiveDescView;
    private Toolbar mToolBar;
    private ViewGroup mBottomLayout;
    private ViewGroup mVideoContainer;
    private int mIsLiveStreaming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pl_video_view);
        mIsLiveStreaming = getIntent().getIntExtra("liveStreaming", 1);
        initView();
        AVOptions avOptions = getOptions(getIntent());
        mVideoView.setAVOptions(avOptions);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        setMediaController();
        bindListener();
    }

    protected void setBottomView(View contentView) {
        mBottomLayout.addView(contentView);
    }

    protected void setMediaController() {
        mMediaController = new MediaController(this, false, mIsLiveStreaming == 1);
        mMediaController.setOnShownListener(new MediaController.OnShownListener() {
            @Override
            public void onShown() {
                getSupportActionBar().show();
                int currentOrientation = getResources().getConfiguration().orientation;
                if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    return;
                }
            }
        });
        mMediaController.setOnHiddenListener(new MediaController.OnHiddenListener() {
            @Override
            public void onHidden() {
                getSupportActionBar().hide();
                int currentOrientation = getResources().getConfiguration().orientation;
                if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    return;
                }
            }
        });
        mMediaController.setOnScreenChangeListener(new MediaController.OnScreenChangeListener() {
            @Override
            public void onChange(int orientation) {
                int currentOrientation = getResources().getConfiguration().orientation;
                if (orientation == currentOrientation) {
                    return;
                }
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    changeScreenToPortrait();
                } else  {
                    changeScreenToLandspace();
                }
            }
        });
        mVideoView.setMediaController(mMediaController);
    }

    private void changeScreenToPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewGroup.LayoutParams lp = mVideoContainer.getLayoutParams();
        float scale = getResources().getDisplayMetrics().density;
        lp.height = (int) (240 * scale + 0.5f);
        mBottomLayout.setVisibility(View.VISIBLE);
        mVideoContainer.setLayoutParams(lp);
    }

    private void changeScreenToLandspace() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup.LayoutParams lp = mVideoContainer.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mBottomLayout.setVisibility(View.INVISIBLE);
        mVideoContainer.setLayoutParams(lp);
    }

    protected void startPlay(String videoUri) {
        if (TextUtils.isEmpty(videoUri)) {
            Toast.makeText(getBaseContext(), R.string.live_uri_error, Toast.LENGTH_LONG).show();
            return;
        }
        mVideoPath = videoUri;
        mVideoView.setVideoPath(mVideoPath);
    }

    private void bindListener() {
        // Set some listeners
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnPreparedListener(mOnPreparedListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
    }

    private AVOptions getOptions(Intent intent) {
        AVOptions options = new AVOptions();

        int isLiveStreaming = intent.getIntExtra("liveStreaming", 1);
        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, isLiveStreaming);
        if (isLiveStreaming == 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        }

        // 1 -> hw codec enable, 0 -> disable [recommended]
        int codec = getIntent().getIntExtra("mediaCodec", 0);
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        return options;
    }

    protected void initView() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mVideoView = (PLVideoView) findViewById(R.id.VideoView);
        mVideoContainer = (ViewGroup) findViewById(R.id.fl_live_container);
        mLiveTitleView = (TextView) findViewById(R.id.tv_live_title);
        mLiveDescView = (TextView) findViewById(R.id.tv_live_desc);
        mLiveCoverView = (ImageView) findViewById(R.id.tv_live_cover);

        mBottomLayout = (ViewGroup) findViewById(R.id.fl_live_bottom_layout);
        mLoadingView = findViewById(R.id.LoadingView);
        mVideoView.setBufferingIndicator(mLoadingView);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    protected void setLiveTitle(String title) {
        mLiveTitleView.setText(title);
    }

    protected void setLiveDesc(String desc) {
        mLiveDescView.setText(desc);
    }

    protected void setLiveCoverStatus(int status) {
        mLiveCoverView.setVisibility(status);
    }

    protected void setLiveCover(Bitmap coverBitmap) {
        mLiveCoverView.setImageBitmap(coverBitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsActivityPaused = false;
        resumeLive();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mToast = null;
        mIsActivityPaused = true;
        pauseLive();
    }

    protected void pauseLive() {
        mVideoView.pause();
    }

    protected void resumeLive() {
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                changeScreenToPortrait();
                return true;
            }
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

    public void onClickSwitchScreen(View v) {
        mDisplayAspectRatio = (mDisplayAspectRatio + 1) % 5;
        mVideoView.setDisplayAspectRatio(mDisplayAspectRatio);
        switch (mVideoView.getDisplayAspectRatio()) {
            case PLVideoView.ASPECT_RATIO_ORIGIN:
                showToastTips("Origin mode");
                break;
            case PLVideoView.ASPECT_RATIO_FIT_PARENT:
                showToastTips("Fit parent !");
                break;
            case PLVideoView.ASPECT_RATIO_PAVED_PARENT:
                showToastTips("Paved parent !");
                break;
            case PLVideoView.ASPECT_RATIO_16_9:
                showToastTips("16 : 9 !");
                break;
            case PLVideoView.ASPECT_RATIO_4_3:
                showToastTips("4 : 3 !");
                break;
            default:
                break;
        }
    }

    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            Log.d(TAG, "onInfo: " + what + ", " + extra);
            return false;
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
            boolean isNeedReconnect = false;
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    showToastTips("Invalid URL !");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    showToastTips("404 resource not found !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    showToastTips("Connection refused !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    showToastTips("Connection timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    showToastTips("Empty playlist !");
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    showToastTips("Stream disconnected !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    showToastTips("Network IO Error !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    showToastTips("Unauthorized Error !");
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    showToastTips("Prepare timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    showToastTips("Read frame timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
                    showToastTips("unknown error !");
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
            if (isNeedReconnect) {
                sendReconnectMessage();
            } else {
                finish();
            }
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            Log.d(TAG, "Play Completed !");
            showToastTips("Play Completed !");
        }
    };

    private PLMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new PLMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int precent) {
            Log.d(TAG, "onBufferingUpdate: " + precent);
        }
    };

    private PLMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new PLMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(PLMediaPlayer plMediaPlayer) {
            Log.d(TAG, "onSeekComplete !");
        };
    };

    private PLMediaPlayer.OnPreparedListener mOnPreparedListener = new PLMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(PLMediaPlayer plMediaPlayer) {
            setLiveCoverStatus(View.GONE);
        }
    };

    private PLMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height) {
            Log.d(TAG, "onVideoSizeChanged: " + width + "," + height);
        }
    };

    private void showToastTips(final String tips) {
        if (mIsActivityPaused) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(PLVideoViewActivity.this, tips, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != MESSAGE_ID_RECONNECTING) {
                return;
            }
            if (mIsActivityPaused || !Utils.isLiveStreamingAvailable()) {
                finish();
                return;
            }
            if (!Utils.isNetworkAvailable(PLVideoViewActivity.this)) {
                sendReconnectMessage();
                return;
            }
            mVideoView.setVideoPath(mVideoPath);
            mVideoView.start();
        }
    };

    private void sendReconnectMessage() {
        showToastTips("正在重连...");
        mLoadingView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 500);
    }
}
