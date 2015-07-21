package com.edusoho.kuozhi.v3.util;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.edusoho.kuozhi.R;
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
    private ImageView mSpeakerImageView;
    private VolumeHandler mHandler;
    private MediaRecordThread mThread;

    private int[] mSpeakerAnimResId = new int[]{R.drawable.record_animate_1,
            R.drawable.record_animate_2,
            R.drawable.record_animate_3,
            R.drawable.record_animate_4};

    private AudioRecord() {
        mAudioFolderPath = new File(EdusohoApp.getWorkSpace() + "/audio");
        if (!mAudioFolderPath.exists()) {
            mAudioFolderPath.mkdir();
        }
        mHandler = new VolumeHandler();
        mSDF = new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public synchronized static AudioRecord getInstance() {
        if (instance == null) {
            instance = new AudioRecord();
        }
        return instance;
    }

    public AudioRecord setSpeakerImageView(ImageView imageView) {
        if (mSpeakerImageView == null) {
            mSpeakerImageView = imageView;
        }
        return this;
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
            mThread = new MediaRecordThread();
            mThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized File stop(boolean isSave) {
        if (mAudioFile != null && mAudioFile.exists()) {
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
            mThread.exit();
        }
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

    public class MediaRecordThread extends Thread {
        private volatile boolean running = true;

        public void exit() {
            Log.d("MediaRecordThread", "stop");
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    if (!running) {
                        break;
                    }
                    double ratio = 0;
                    if (mMediaRecorder != null) {
                        ratio = (double) mMediaRecorder.getMaxAmplitude();
                    }
                    double db = 0;
                    if (ratio > 1) {
                        db = 20 * Math.log10(ratio);
                    }

                    if (db < 60) {
                        mHandler.sendEmptyMessage(0);
                    } else if (db < 70) {
                        mHandler.sendEmptyMessage(1);
                    } else if (db < 80) {
                        mHandler.sendEmptyMessage(2);
                    } else if (db < 90) {
                        mHandler.sendEmptyMessage(3);
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class VolumeHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            mSpeakerImageView.setImageResource(mSpeakerAnimResId[msg.what]);
        }
    }
}
