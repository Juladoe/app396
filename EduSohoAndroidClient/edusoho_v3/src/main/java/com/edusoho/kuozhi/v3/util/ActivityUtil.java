package com.edusoho.kuozhi.v3.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


/**
 * Created by suju on 16/10/21.
 */
public class ActivityUtil {

    public static void setStatusBarTranslucent(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public static void setRootViewFitsWindow(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View statusView = createStatusView(activity, color);
            decorView.addView(statusView);
        }
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        if (rootView.getChildCount() == 0) {
            return;
        }
        ViewGroup contentView = (ViewGroup) rootView.getChildAt(0);
        contentView.setFitsSystemWindows(true);
        contentView.setClipToPadding(true);
    }

    private static View createStatusView(Activity activity, int color) {
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

        View statusView = new View(activity.getBaseContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }
}
