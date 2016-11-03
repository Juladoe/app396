package com.edusoho.liveplayer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by suju on 16/10/12.
 */
public class Utils {

    public static final int SHOW_KEYBOARD = 1;
    public static final int HIDE_KEYBOARD = 2;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Is the live streaming still available
     * @return is the live streaming is available
     */
    public static boolean isLiveStreamingAvailable() {
        return true;
    }

    public static void setSoftKeyBoard(View editText, Context context, int status) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getApplicationContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (SHOW_KEYBOARD == status) {
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } else if (HIDE_KEYBOARD == status) {
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}