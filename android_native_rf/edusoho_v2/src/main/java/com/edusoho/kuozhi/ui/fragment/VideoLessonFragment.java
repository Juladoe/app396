package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.edusoho.plugin.video.CustomMediaController;
import com.google.gson.reflect.TypeToken;

import java.util.Timer;

/**
 * Created by howzhi on 14-9-26.
 */
public class VideoLessonFragment extends BaseFragment {

    private VideoView mVideoView;
    private Timer hideLoadTimer;
    private MediaPlayer mMediaPlayer;
    private boolean isPlayed;

    private int mCourseId;
    private int mLessonId;

    public static final int HIDE_LOADING = 0001;

    @Override
    public String getTitle() {
        return "视频课时";
    }

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
        setContainerView(R.layout.video_lesson_fragment_layout);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        String url = bundle.getString(Const.MEDIA_URL);
        mCourseId = bundle.getInt(Const.COURSE_ID);
        mLessonId = bundle.getInt(Const.LESSON_ID);
        if (url != null && !TextUtils.isEmpty(url)) {
            mUri = Uri.parse(url);
        }
        Log.d(null, "uri->" + mUri);
        autoHideTimer = new Timer();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mVideoView = (VideoView) view.findViewById(R.id.video_view);
        mLoadView = view.findViewById(R.id.load_layout);

        mMediaController = (CustomMediaController) view.findViewById(R.id.custom_mediaController);
        mMediaController.setVideoView(mVideoView);
        mMediaController.setActivity(mActivity);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mMediaPlayer = mediaPlayer;
                mVideoView.requestLayout();
                mMediaController.ready(new CustomMediaController.MediaControllerListener() {
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
                Log.d(null, "play error-> " + what + "  -> " + i2);
                mLoadView.setVisibility(View.GONE);
                Log.d(null, "isPlayed> " + isPlayed);
                if (isPlayed) {
                    showErrorDialog();
                    return true;
                }
                reloadLessonMediaUrl();
                return true;
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mMediaController.stop(mediaPlayer);
            }
        });
    }

    private void changeBDPlayFragment(final String url)
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = app.mEngine.runPluginWithFragment(
                "BDVideoLessonFragment", mActivity, new PluginFragmentCallback() {
            @Override
            public void setArguments(Bundle bundle) {
                bundle.putString(Const.MEDIA_URL, url);
            }
        });
        fragmentTransaction.replace(R.id.lesson_content, fragment);
        fragmentTransaction.setCustomAnimations(
                FragmentTransaction.TRANSIT_FRAGMENT_FADE, FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentTransaction.commit();
    }

    private void reloadLessonMediaUrl()
    {
        RequestUrl requestUrl = app.bindUrl(Const.COURSELESSON, true);
        requestUrl.setParams(new String[] {
                "courseId", mCourseId + "",
                "lessonId", mLessonId + ""
        });

        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                LessonItem lessonItem = mActivity.parseJsonValue(
                        object, new TypeToken<LessonItem<String>>() {
                });
                if (lessonItem == null) {
                    showErrorDialog();
                    return;
                }

                changeBDPlayFragment(lessonItem.mediaUri);
            }
        });
    }

    private void showErrorDialog()
    {
        PopupDialog popupDialog = PopupDialog.createNormal(mActivity, "播放提示", "该课时视频不能播放");
        popupDialog.setOkListener(new PopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button) {
                getActivity().finish();
            }
        });
        popupDialog.show();
    }

    private View mLoadView;
    private Uri mUri;
    private int mPositionWhenPaused = -1;

    private CustomMediaController mMediaController;
    private Timer autoHideTimer;

    @Override
    public void onStart() {
        // Play Video
        if (mUri != null) {
            mVideoView.setVideoURI(mUri);
            mMediaController.play();
        }
        System.out.println("start->play");
        super.onStart();
    }

    @Override
    public void onPause() {
        // Stop video when the activity is pause.
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mMediaController.pause();
        mVideoView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        // Resume video player
        if (mPositionWhenPaused >= 0) {
            mVideoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }

        super.onResume();
    }

    @Override
    public void onDestroy() {
        System.out.println("video player distory");
        super.onDestroy();
        if (autoHideTimer != null) {
            autoHideTimer.cancel();
        }
        mMediaController.destory();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mVideoView = null;
        hideLoadTimer = null;
    }
}
