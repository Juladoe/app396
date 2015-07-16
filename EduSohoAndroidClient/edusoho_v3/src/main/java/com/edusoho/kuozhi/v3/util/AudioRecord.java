package com.edusoho.kuozhi.v3.util;

import android.media.MediaRecorder;

import com.edusoho.kuozhi.v3.EdusohoApp;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by JesseHuang on 15/7/16.
 */
public class AudioRecord {
    private static AudioRecord instance;

    private MediaRecorder mMediaRecorder;
    private File mAudioFolderPath;
    private File mAudioFile;
    private SimpleDateFormat mSDF;


    private AudioRecord() {
        mAudioFolderPath = new File(EdusohoApp.app.getWorkSpace() + "/audio");
        if (!mAudioFolderPath.exists()) {
            mAudioFolderPath.mkdir();
        }
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mSDF = new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public static AudioRecord getInstance() {
        if (instance == null) {
            instance = new AudioRecord();
        }
        return instance;
    }

    public void start() {
        mAudioFile = new File(mAudioFolderPath + "/" + mSDF.format(System.currentTimeMillis()));
        try {
            mAudioFile.createNewFile();
            mMediaRecorder.setOutputFile(mAudioFile.getPath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File stop(boolean isSave) {
        if (mAudioFile != null && mAudioFile.exists()) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            if (!isSave) {
                mAudioFile.delete();
                mAudioFile = null;
            }
        }
        return mAudioFile;
    }
}
