package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by hby on 14-9-16.
 */
public class QuestionRefreshListWidget extends CourseRefreshListWidget {
    private Context mContext;

    public QuestionRefreshListWidget(Context context) {
        super(context);
        this.mContext = context;
    }

    public QuestionRefreshListWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }
}
