package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by howzhi on 14-8-28.
 */
public class RecommendScrollView extends ScrollView {

    public RecommendScrollView(Context context) {
        super(context);
    }

    public RecommendScrollView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        Log.d(null, "scroll->");
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
