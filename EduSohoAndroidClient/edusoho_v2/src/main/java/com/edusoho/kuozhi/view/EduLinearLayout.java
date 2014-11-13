package com.edusoho.kuozhi.view;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by howzhi on 14-9-5.
 */
public class EduLinearLayout extends LinearLayout {

    public EduLinearLayout(Context context) {
        super(context);
    }

    public EduLinearLayout(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }
}
