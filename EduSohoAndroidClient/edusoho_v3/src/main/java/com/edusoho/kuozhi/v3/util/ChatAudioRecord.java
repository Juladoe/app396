package com.edusoho.kuozhi.v3.util;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Vibrator;

import com.edusoho.kuozhi.v3.EdusohoApp;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by JesseHuang on 15/7/16.
 */
public class ChatAudioRecord {
    private MediaRecorder mMediaRecorder;
    private File mAudioFolderPath;
    private File mAudioFile;
    private SimpleDateFormat mSDF;
    private long mAudioStartTime;
    private long mAudioEndTime;
    private Vibrator mVibrator;

    public ChatAudioRecord(Context ctx) {
        mVibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        mAudioFolderPath = new File(EdusohoApp.getChatCacheFile() + "/audio");
        if (!mAudioFolderPath.exists()) {
            mAudioFolderPath.mkdir();
        }
        mSDF = new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public MediaRecorder getMediaRecorder() {
        return mMediaRecorder;
    }

    public void ready() {
        mAudioFile = new File(mAudioFolderPath + "/" + mSDF.format(System.currentTimeMillis()) + Const.AUDIO_EXTENSION);
        try {
            mAudioFile.createNewFile();
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            mVibrator.vibrate(50);
            mMediaRecorder.setOutputFile(mAudioFile.getPath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mAudioStartTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File stop(boolean cancelSave) {
        try {
            if (mAudioFile != null && mAudioFile.exists()) {
                mMediaRecorder.stop();
                mAudioEndTime = System.currentTimeMillis();
                mMediaRecorder.reset();
                mMediaRecorder.release();
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
    }
}
