package com.edusoho.kuozhi.ui.fragment.video;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.EduVideoViewListener;
import com.edusoho.plugin.video.CustomMediaController;

import java.util.Timer;

/**
 * Created by howzhi on 14-10-23.
 */
public class EduVideoViewFragment extends Fragment {

    public static final String TAG = "EduVideoViewFragment";

    private Timer hideLoadTimer;
    private int mPositionWhenPaused = -1;
    private CustomMediaController mCustomMediaController;
    private View mLoadView;
    private Uri mUri;
    private boolean isPlayed;
    private VideoView mVideoView;
    private int mPos;
    private EduVideoViewListener mEduVideoViewListener;

    public static final int HIDE_LOADING = 0001;

    public static final int RELOAD = 0001;
    public static final int ERROR = 0002;
    public static final int CHANGE_PLAYER = 0003;

    private Handler workHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIDE_LOADING:
                    mLoadView.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideLoadTimer = new Timer();
        Bundle bundle = getArguments();
        String url = bundle.getString(Const.MEDIA_URL);
        if (url != null && !TextUtils.isEmpty(url)) {
            mUri = Uri.parse(url);
        }
        Log.d(TAG, "uri->" + mUri);
    }

    private void initView(View view)
    {
        mLoadView = view.findViewById(R.id.load_layout);
        mVideoView = (VideoView) view.findViewById(R.id.video_view);
        mCustomMediaController.setVideoView(mVideoView);
        mCustomMediaController.setHideListener(mVideoView);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                mVideoView.requestLayout();
                mCustomMediaController.ready(new CustomMediaController.MediaControllerListener() {
                    @Override
                    public void startPlay() {
                        isPlayed = true;
                        hideLoadTimer.cancel();
                        workHandler.obtainMessage(HIDE_LOADING).sendToTarget();
                    }
                });
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int i2) {
                mLoadView.setVisibility(View.GONE);
                Log.d(null, "isPlayed> " + isPlayed);
                if (mEduVideoViewListener == null) {
                    return true;
                }
                if (isPlayed) {
                    switch (what) {
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                            Log.d(null, "error->MEDIA_ERROR_SERVER_DIED");
                            mEduVideoViewListener.error(RELOAD);
                            return true;
                    }
                    mEduVideoViewListener.error(ERROR);
                    return true;
                }
                mEduVideoViewListener.error(CHANGE_PLAYER);
                return true;
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mCustomMediaController.stop(mediaPlayer);
            }
        });
    }

    public void setOnErrorListener(EduVideoViewListener listener)
    {
        this.mEduVideoViewListener = listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edu_videoview_layout, null);
        initView(view);

        return view;
    }

    public void setController(CustomMediaController controller)
    {
        this.mCustomMediaController = controller;
    }

    public void setPos(int pos)
    {
        this.mPos = pos;
    }

    @Override
    public void onPause() {
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mCustomMediaController.pause();
        super.onPause();
    }

    @Override
    public void onResume() {

        if (mPositionWhenPaused >= 0) {
            mCustomMediaController.play(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        } else {
            if (mUri != null) {
                mVideoView.setVideoURI(mUri);
                mCustomMediaController.play(mPos);
            }
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " distory");
        super.onDestroy();
        mVideoView = null;
        hideLoadTimer = null;
        mCustomMediaController.destory();
    }
}
