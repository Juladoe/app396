package com.edusoho.kuozhi.imserver.ui.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.edusoho.kuozhi.imserver.ui.listener.AudioPlayStatusListener;

/**
 * Created by suju on 16/8/28.
 */
public class MessageAudioPlayer {

    private String mAudioFile;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private AudioPlayStatusListener mAudioPlayStatusListener;

    public MessageAudioPlayer(Context context, String audioFile, AudioPlayStatusListener listener) {
        this.mContext = context;
        this.mAudioFile = audioFile;
        this.mAudioPlayStatusListener = listener;
    }

    public void play() {
        mMediaPlayer = MediaPlayer.create(mContext, Uri.parse(mAudioFile));
        if (mMediaPlayer == null) {
            stop();
            return;
        }
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });
        mMediaPlayer.start();
        mAudioPlayStatusListener.onPlay();
    }

    public void stop() {
        if (mMediaPlayer == null) {
            return;
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        if (mAudioPlayStatusListener != null) {
            mAudioPlayStatusListener.onStop();
            mAudioPlayStatusListener = null;
        }

        mMediaPlayer.release();
        mMediaPlayer = null;
    }
}
