package com.edusoho.kuozhi.imserver.ui.util;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Vibrator;

import com.czt.mp3recorder.MP3Recorder;
import com.edusoho.kuozhi.imserver.helper.recorder.MessageAudioRecorder;
import com.edusoho.kuozhi.imserver.ui.helper.MessageHelper;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by JesseHuang on 15/7/16.
 */
public class ChatAudioRecord {

    public static final String AUDIO_EXTENSION = ".mp3";
    private MessageHelper mMessageHelper;
    private File mAudioFolderPath;
    private File mAudioFile;
    private SimpleDateFormat mSDF;
    private long mAudioStartTime;
    private long mAudioEndTime;
    private MessageAudioRecorder mRecorder;
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

    public MessageAudioRecorder getMediaRecorder() {
        return mRecorder;
    }

    public void ready() {
        mAudioFile = new File(mAudioFolderPath, String.format("%s%s", mSDF.format(System.currentTimeMillis()), AUDIO_EXTENSION));
        try {
            mAudioFile.createNewFile();
            if (mRecorder == null) {
                mRecorder = new MessageAudioRecorder(mAudioFile);
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

    public long getAudioLength() {
        long length = mAudioEndTime - mAudioStartTime;
        if (length < 0) {
            return 0;
        }
        return length;
    }

    public void clear() {
        if (mRecorder != null && mRecorder.isRecording()) {
            mRecorder.stop();
        }
        mRecorder = null;
    }
}
