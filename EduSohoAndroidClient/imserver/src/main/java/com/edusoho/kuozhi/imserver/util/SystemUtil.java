package com.edusoho.kuozhi.imserver.util;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by suju on 16/8/20.
 */
public class SystemUtil {

    public static boolean isServiceRunning(Context context, Class serviceClass) {
        if (serviceClass == null) {
            return false;
        }
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
