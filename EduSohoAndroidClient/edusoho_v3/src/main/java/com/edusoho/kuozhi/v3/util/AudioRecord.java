package com.edusoho.kuozhi.v3.util;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by JesseHuang on 15/7/16.
 */
public class AudioRecord {
    private static final String TAG = "AudioRecord";
    private static AudioRecord instance;

    private MediaRecorder mMediaRecorder;
    private File mAudioFolderPath;
    private File mAudioFile;
    private SimpleDateFormat mSDF;
    private long mAudioStartTime;
    private long mAudioEndTime;

    private AudioRecord() {
        mAudioFolderPath = new File(EdusohoApp.app.getWorkSpace() + "/audio");
        if (!mAudioFolderPath.exists()) {
            mAudioFolderPath.mkdir();
        }

        mSDF = new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public synchronized static AudioRecord getInstance(Context ctx) {
        if (instance == null) {
            instance = new AudioRecord();
        }
        return instance;
    }

    public MediaRecorder getMediaRecorder() {
        return mMediaRecorder;
    }

    public void ready(NormalCallback callback) {
        mAudioFile = new File(mAudioFolderPath + "/" + mSDF.format(System.currentTimeMillis()) + Const.AUDIO_EXTENSION);
        try {
            mAudioFile.createNewFile();
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            }
            callback.success(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void start() {
        try {
            mMediaRecorder.setOutputFile(mAudioFile.getPath());
            mMediaRecorder.prepare();
            mAudioStartTime = System.currentTimeMillis();
            mMediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized File stop(boolean isSave) {
        if (mAudioFile != null && mAudioFile.exists()) {
            Log.d(TAG, "audio file is not null");
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {

                }
            });
            if (!isSave) {
                mAudioFile.delete();
            }
        }
        Log.d(TAG, "AudioRecord_stop");
        mAudioEndTime = System.currentTimeMillis();

        return mAudioFile;
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
