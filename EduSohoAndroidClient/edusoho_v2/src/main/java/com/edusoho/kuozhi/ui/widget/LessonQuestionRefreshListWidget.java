package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListAdapter;

/**
 * Created by MyPC on 2014/11/14.
 */
public class LessonQuestionRefreshListWidget extends RefreshListWidget {
    private static final String TAG = "LessonQuestionRefreshListWidget";

    public LessonQuestionRefreshListWidget(Context context) {
        super(context);
    }

    public LessonQuestionRefreshListWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        try {
            if (adapter.isEmpty()) {
                adapter = getEmptyLayoutAdapter();
            }
            super.setAdapter(adapter);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }
}
