package com.edusoho.kuozhi.v3.ui.fragment.lesson;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.provider.CourseProvider;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by howzhi on 14-9-26.
 */
public class AudioLessonFragment extends BaseFragment {

    private MediaPlayer audioMediaPlayer;
    private ImageView mPlayBtn;
    private SeekBar mAudioProgress;
    private TextView mTotalTime;
    private TextView mCurrentTime;
    private ImageView mAudioCoverView;
    private View mAudioCoverLayout;
    private String mUri;
    protected int mCourseId;
    protected float mAudioCoverAnimOffset;
    protected ObjectAnimator mAudioCoverAnim;

    private Timer updateTimer;
    private boolean mIsSetTotalTime;
    public static final int HIDE_LOADING = 0001;
    private static final int UPDATE_PLAY_TIME = 0001;
    private static final int SET_TOTALTIME = 0002;

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String time = timeFormat(msg.arg1 / 1000);
            switch (msg.what) {
                case UPDATE_PLAY_TIME:
                    mCurrentTime.setText(time);
                    mAudioProgress.setProgress(msg.arg1);
                    break;
                case SET_TOTALTIME:
                    mIsSetTotalTime = true;
                    mTotalTime.setText(time);
                    mAudioProgress.setMax(msg.arg1);
                    break;
            }
        }
    };

    @Override
    public String getTitle() {
        return "音频课时";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioMediaPlayer = new MediaPlayer();
        updateTimer = new Timer();
        setContainerView(R.layout.audio_fragment_layout);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        mCourseId = bundle.getInt(Const.COURSE_ID, 0);
        mUri = bundle.getString(Const.MEDIA_URL);
    }

    private String timeFormat(int second) {
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String strTemp = "";
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }

        return strTemp;
    }

    private void updateAudioCoverViewStatus(boolean isPlay) {
        if (mAudioCoverAnim == null) {
            mAudioCoverAnim = ObjectAnimator.ofFloat(mAudioCoverLayout, "rotation", 0f, 359f);
            mAudioCoverAnim.setDuration(10000);
            mAudioCoverAnim.setInterpolator(new LinearInterpolator());
            mAudioCoverAnim.setRepeatCount(-1);
        }
        if (isPlay) {
            mAudioCoverAnim.setFloatValues(mAudioCoverAnimOffset, mAudioCoverAnimOffset + 359f);
            mAudioCoverAnim.start();
        } else {
            mAudioCoverAnimOffset = (float) mAudioCoverAnim.getAnimatedValue();
            mAudioCoverAnim.cancel();
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mTotalTime = (TextView) view.findViewById(R.id.audio_totalTime);
        mCurrentTime = (TextView) view.findViewById(R.id.audio_currentTime);
        mPlayBtn = (ImageView) view.findViewById(R.id.audio_playbtn);
        mAudioCoverView = (ImageView) view.findViewById(R.id.audio_play_cover);
        mAudioCoverLayout = view.findViewById(R.id.audio_play_cover_layout);
        mAudioProgress = (SeekBar) view.findViewById(R.id.audio_progress);

        if (TextUtils.isEmpty(mUri)) {
            CommonUtil.longToast(mContext, "无效的音频课时!");
            return;
        }

        CourseProvider courseProvider = ModelProvider.initProvider(mContext, CourseProvider.class);
        RequestUrl requestUrl = app.bindUrl(String.format("%s?courseId=%d", Const.COURSE, mCourseId), false);
        courseProvider.getCourse(requestUrl).success(new NormalCallback<CourseDetailsResult>() {
            @Override
            public void success(CourseDetailsResult courseDetailsResult) {
                if (courseDetailsResult != null && courseDetailsResult.course != null) {
                    ImageLoader.getInstance().displayImage(courseDetailsResult.course.middlePicture, mAudioCoverView);
                }
            }
        });

        try {
            audioMediaPlayer.setDataSource(mUri);
            audioMediaPlayer.prepareAsync();
            initListener();
        } catch (IllegalStateException e) {
            CommonUtil.longToast(mContext, "不能播放此音频课程!");
        } catch (Exception e) {
            CommonUtil.longToast(mContext, "不能播放此音频课程!");
        }
    }

    private void initListener() {
        View.OnClickListener playListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audioMediaPlayer.isPlaying()) {
                    updateAudioCoverViewStatus(false);
                    audioMediaPlayer.pause();
                    mPlayBtn.setImageResource(R.drawable.icon_video_play);
                } else {
                    updateAudioCoverViewStatus(true);
                    audioMediaPlayer.start();
                    mPlayBtn.setImageResource(R.drawable.icon_video_pause);
                }
            }
        };
        mAudioCoverLayout.setOnClickListener(playListener);
        mPlayBtn.setOnClickListener(playListener);

        audioMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mPlayBtn.setImageResource(R.drawable.icon_video_pause);
                updateAudioCoverViewStatus(true);
                updateTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = updateHandler.obtainMessage(UPDATE_PLAY_TIME);
                        msg.arg1 = 2 + audioMediaPlayer.getCurrentPosition();
                        msg.sendToTarget();

                        if (!mIsSetTotalTime) {
                            msg = updateHandler.obtainMessage(SET_TOTALTIME);
                            msg.arg1 = audioMediaPlayer.getDuration();
                            msg.sendToTarget();
                        }
                    }
                }, 0, 1000);
            }
        });

        audioMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                updateAudioCoverViewStatus(false);
                audioMediaPlayer.seekTo(0);
                mPlayBtn.setImageResource(R.drawable.icon_video_play);
            }
        });

        audioMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                updateAudioCoverViewStatus(false);
                CommonUtil.longToast(mContext, "不能播放此音频课程!");
                return true;
            }
        });

        mAudioProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    audioMediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateTimer.cancel();
        if (audioMediaPlayer != null) {
            if (audioMediaPlayer.isPlaying()) {
                audioMediaPlayer.stop();
            }
        }
        if (mAudioCoverAnim != null) {
            mAudioCoverAnim.cancel();
        }
        audioMediaPlayer.release();
        audioMediaPlayer = null;
    }
}
