package com.edusoho.kuozhi.v3.view.headStopScroll;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.view.HeadStopScrollView;

/**
 * Created by Zhang on 2016/12/9.
 */

public class StopScrollView extends ScrollView implements HeadStopScrollView.CanStopView {
    public StopScrollView(Context context) {
        super(context);
    }

    public StopScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StopScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean mCanScroll = true;
    private HeadStopScrollView mParent;


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (oldt - t > 0 &&
                t <= AppUtil.dp2px(getContext(), 10)) {
            sendScrollState();
        }
    }

    float moveY;
    float startY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = ev.getRawY() - startY;
                if (moveY > 0 && getScrollY() == 0) {
                    sendScrollState();
                }
                break;
            case MotionEvent.ACTION_UP:
                startY = 0;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void sendScrollState() {
        mCanScroll = false;
//        mParent.stateChange();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mCanScroll ? super.onTouchEvent(ev) : false;
    }

    @Override
    public void setCanScroll(boolean canScroll) {
        mCanScroll = true;
    }

    @Override
    public void bindParent(HeadStopScrollView headStopScrollView) {
        mParent = headStopScrollView;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }
}
