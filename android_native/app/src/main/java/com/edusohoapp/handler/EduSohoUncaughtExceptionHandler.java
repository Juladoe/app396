package com.edusohoapp.handler;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by howzhi on 14-6-6.
 */
public class EduSohoUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;

    public static EduSohoUncaughtExceptionHandler caughtHandler;

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public EduSohoUncaughtExceptionHandler(Context context)
    {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!handleException(throwable) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, throwable);
        } else {

        }
    }

    public static EduSohoUncaughtExceptionHandler initCaughtHandler(Context context)
    {
        if (caughtHandler == null) {
            caughtHandler = new EduSohoUncaughtExceptionHandler(context);
        }
        return caughtHandler;
    }

    private boolean handleException(Throwable throwable)
    {
        if (throwable == null) {
            return false;
        }
        Toast.makeText(mContext, "系统运行异常", Toast.LENGTH_LONG).show();
        return true;
    }
}
