package com.soooner.EplayerPluginLibary.util;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-11-5
 * Time: 上午11:39
 * To change this template use File | Settings | File Templates.
 */
public class VibratorUtil {

    public static void Vibrate( Context context, long milliseconds) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }
    public static void Vibrate(Context context, long[] pattern,boolean isRepeat) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }
}
