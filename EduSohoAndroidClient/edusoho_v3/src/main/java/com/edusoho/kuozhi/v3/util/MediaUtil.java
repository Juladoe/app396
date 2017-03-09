package com.edusoho.kuozhi.v3.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.edusoho.videoplayer.util.VLCOptions;

/**
 * Created by suju on 17/3/2.
 */

public class MediaUtil {

    public static void saveMediaSupportType(Context context, int type) {
        SharedPreferences sp = context.getSharedPreferences("mediaSupportRate", Context.MODE_PRIVATE);
        sp.edit().putInt("type", type).commit();
    }

    public static int getMediaSupportType(Context context) {
        SharedPreferences sp = context.getSharedPreferences("mediaSupportRate", Context.MODE_PRIVATE);
        return sp.getInt("type", VLCOptions.NONE_RATE);
    }
}
