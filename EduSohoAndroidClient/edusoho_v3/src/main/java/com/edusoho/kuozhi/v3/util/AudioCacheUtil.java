package com.edusoho.kuozhi.v3.util;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.sys.AudioCacheEntity;
import com.edusoho.kuozhi.v3.util.sql.AudioCacheDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;

/**
 * Created by JesseHuang on 15/12/29.
 */
public class AudioCacheUtil {
    private static AudioCacheUtil mAudioCacheUtil;
    private static AudioCacheDataSource mAudioCacheDataSource;

    private AudioCacheUtil(AudioCacheDataSource dataSource) {
        mAudioCacheDataSource = dataSource;
    }

    public static AudioCacheUtil getInstance() {
        if (mAudioCacheUtil == null) {
            mAudioCacheUtil = new AudioCacheUtil(new AudioCacheDataSource(SqliteUtil.getUtil(EdusohoApp.app.getApplicationContext())));
        }
        return mAudioCacheUtil;
    }

    public AudioCacheEntity getAudioCacheByPath(String path) {
        return mAudioCacheDataSource.getAudio(path);
    }

    public long create(String local, String online) {
        return mAudioCacheDataSource.create(local, online);
    }

    public long create(AudioCacheEntity model) {
        long id = 0;
        if (mAudioCacheDataSource.getAudio(model.localPath) == null || mAudioCacheDataSource.getAudio(model.onlinePath) == null) {
            id = mAudioCacheDataSource.create(model);
        }
        return id;
    }

    public void update(AudioCacheEntity model) {
        mAudioCacheDataSource.update(model);
    }
}
