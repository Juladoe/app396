package com.soooner.EplayerPluginLibary.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-3-10
 * Time: 上午9:58
 * To change this template use File | Settings | File Templates.
 */
public class ServiceUtil {

    /**
     * 用来判断服务是否运行.
     *
     * @param mContext
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {

        if (StringUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> serviceList = activityManager.getRunningServices(40);
        if (serviceList == null) {
            return false;
        }
        for (RunningServiceInfo aServiceList : serviceList) {
            String aClassName = aServiceList.service.getClassName();
            if (aClassName == null)
                continue;
            if (className.equals(aClassName)) {
                return true;
            }
        }
        return false;
    }
}
