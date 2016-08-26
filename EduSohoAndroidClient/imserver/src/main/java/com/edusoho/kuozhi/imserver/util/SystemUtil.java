package com.edusoho.kuozhi.imserver.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by suju on 16/8/20.
 */
public class SystemUtil {

    public static final int SHOW_KEYBOARD = 1;
    public static final int HIDE_KEYBOARD = 2;

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

    public static void setSoftKeyBoard(EditText editText, Context context, int status) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getApplicationContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (SHOW_KEYBOARD == status) {
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } else if (HIDE_KEYBOARD == status) {
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}
