package com.edusoho.kuozhi.imserver.ui.util;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Vibrator;

import com.czt.mp3recorder.MP3Recorder;
import com.edusoho.kuozhi.imserver.ui.helper.MessageHelper;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by JesseHuang on 15/7/16.
 */
public class ChatAudioRecord {

    public static final String AUDIO_EXTENSION = ".mp3";
    private MessageHelper mMessageHelper;
    private MediaRecorder mMediaRecorder;
    private File mAudioFolderPath;
    private File mAudioFile;
    private SimpleDateFormat mSDF;
    private long mAudioStartTime;
    private long mAudioEndTime;
    private Vibrator mVibrator;

    public ChatAudioRecord(Context ctx) {
        mMessageHelper = new MessageHelper(ctx);
        mVibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        mAudioFolderPath = mMessageHelper.getAudioStorage();
        if (!mAudioFolderPath.exists()) {
            mAudioFolderPath.mkdir();
        }
        mSDF = new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public MP3Recorder getMediaRecorder() {
        return mRecorder;
    }

    MP3Recorder mRecorder;

    public void ready() {
        mAudioFile = new File(mAudioFolderPath, String.format("%s%s", mSDF.format(System.currentTimeMillis()), AUDIO_EXTENSION));
        try {
            mAudioFile.createNewFile();
            if (mMediaRecorder == null) {
                mRecorder = new MP3Recorder(mAudioFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            mRecorder.start();
            mAudioStartTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File stop(boolean cancelSave) {
        try {
            if (mAudioFile != null && mAudioFile.exists()) {
                mAudioEndTime = System.currentTimeMillis();
                mRecorder.stop();
                if (cancelSave) {
                    mAudioFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mAudioFile;
    }

    public void delete() {
        try {
            if (mAudioFile != null && mAudioFile.exists()) {
                mAudioFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getAudioStartTime() {
        return mAudioStartTime;
    }

    public int getAudioLength() {
        int length = (int) ((mAudioEndTime - mAudioStartTime) / 1000);
        if (length < 1) {
            return -1;
        }
        return length;
    }

    public void clear() {
        if (mMediaRecorder != null) {
            mMediaRecorder = null;
        }
        mRecorder = null;
    }
}
