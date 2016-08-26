package com.edusoho.kuozhi.imserver.ui.helper;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by suju on 16/8/26.
 */
public class MessageHelper {

    public static final String UPLOAD_AUDIO_CACHE_FILE = "/audio";
    public static final String UPLOAD_IMAGE_CACHE_FILE = "/image";
    public static final String UPLOAD_IMAGE_CACHE_THUMB_FILE = "/image/thumb";

    private Context mContext;

    public MessageHelper(Context context) {
        this.mContext = context;
    }

    private File getMessageStorage() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return mContext.getExternalCacheDir();
        }

        return mContext.getCacheDir();
    }

    public File getAudioStorage() {
        File dir = new File(getMessageStorage(), UPLOAD_AUDIO_CACHE_FILE);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public File getImageStorage() {
        File dir = new File(getMessageStorage(), UPLOAD_IMAGE_CACHE_FILE);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public File getThumbImageStorage() {
        File dir = new File(getMessageStorage(), UPLOAD_IMAGE_CACHE_THUMB_FILE);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public String getThumbImagePath(String path) {
        Uri uri = Uri.parse(path);
        if (uri == null) {
            return path;
        }

        String uriPath = uri.getPath();
        File thumbFile = new File(getThumbImageStorage(), uriPath);
        if (thumbFile.exists()) {
            return String.format("file://%s", thumbFile.getAbsolutePath());
        }

        File realFile = new File(getImageStorage(), uriPath);
        if (realFile.exists()) {
            return String.format("file://%s", realFile.getAbsolutePath());
        }
        return path;
    }
}
