package com.soooner.EplayerPluginLibary.util;

import android.util.Log;
import com.soooner.EplayerSetting;

/**
 * Created with IntelliJ IDEA.
 * User: SlothMonkey
 * Date: 13-11-14
 * Time: 下午5:45
 * To change this template use File | Settings | File Templates.
 */
public class LogUtil {

    private static final String DEFAULT_TAG = LogUtil.class.getSimpleName();
    private static final String TEST_TAG = "---Test Log---";


    /**
     * 用于测试日志，随时可以关闭输出
     *
     * @param msg 测试日志
     */
    public static void d(String msg) {
        if (EplayerSetting.enbledLog) {
            Log.d(TEST_TAG, msg);
        }
    }

    public static void e(String msg) {
        if (EplayerSetting.enbledLog) {
            Log.e(TEST_TAG, msg);
        }
    }

    public static void e(String msg, Throwable tr) {
        if (EplayerSetting.enbledLog) {
            Log.e(TEST_TAG, msg, tr);
        }
    }


    public static void d(String tag, String msg) {
        if (EplayerSetting.enbledLog) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (EplayerSetting.enbledLog) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (EplayerSetting.enbledLog) {
            Log.e(tag, msg, tr);
        }
    }


}
