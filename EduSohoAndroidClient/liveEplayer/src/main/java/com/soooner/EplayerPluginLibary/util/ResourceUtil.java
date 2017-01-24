package com.soooner.EplayerPluginLibary.util;

import android.content.Context;

/**
 * Created by zhaoxu2014 on 15-1-9.
 */
public class ResourceUtil {
    /**
     *
     * @param context
     * @param name
     * @param defType 资源的类型，如drawable, string 。。。
     * @return
     */
    public static  int getResId(Context context,String name, String defType) {
        String packageName = context.getApplicationInfo().packageName;
        return context.getResources().getIdentifier(name, defType, packageName);
    }
}
