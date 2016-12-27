package com.edusoho.kuozhi.v3.view.headStopScroll;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;
import android.widget.ScrollView;
import android.widget.Scroller;

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

    private HeadStopScrollView mParent;


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    float moveY;
    float moveYOld;
    float moveByY;
    float startY;
    private ViewGroup mChildView;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        acquireVelocityTracker(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getRawY();
                mPointerId = ev.getPointerId(0);
                mVelocityY = 0;
                moveY = 0;
                mUnClick = false;
                moveByY = 0;
                mChildView = getChildCount() == 0 ? null :
                        getChildAt(0) instanceof ViewGroup ? (ViewGroup) getChildAt(0) : null;
                break;
            case MotionEvent.ACTION_MOVE:
                moveYOld = moveY;
                moveY = ev.getRawY() - startY;
                float move = moveY - moveYOld;
                moveByY = move < 0 ? -move : move;
                mVelocityTracker.computeCurrentVelocity(1000);
                mVelocityY = (int) mVelocityTracker.getYVelocity(mPointerId);
                if (Math.abs(moveY) > 10 && mChildView != null) {
                    mUnClick = true;
                }
                if (mParent.isStay()) {
                    break;
                }
                if (moveY > 0 && getScrollY() <= 10) {
                    mParent.scrollBy(0, (int) (-moveByY));
                    return true;
                }
                if (firstViewHeight > Math.abs(mParent.getScrollY())
                        && moveY < 0) {
                    mParent.scrollBy(0, (int) moveByY);
                    return true;
                } else {
                    break;
                }
            case MotionEvent.ACTION_UP:
                startY = 0;
                if (moveY < 0) {
                    smoothScrollBy(0, Math.abs(mVelocityY) / 5);
                    mParent.smoothScrollBy(0, Math.abs(mVelocityY) / 5);
                } else {
                    int scrollY = getScrollY();
                    smoothScrollBy(0, -Math.abs(mVelocityY) / 2);
                    if (scrollY - Math.abs(mVelocityY) / 5 <= 10) {
                        mParent.smoothScrollBy(0, -Math.abs(mVelocityY) / 5);
                    }
                }
                if (Math.abs(moveY) < 5 && mChildView != null) {
                    mUnClick = false;
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private VelocityTracker mVelocityTracker;
    private int mVelocityY;
    private int mPointerId;
    private boolean mUnClick = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mUnClick ? true : super.onInterceptTouchEvent(ev);
    }

    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private int firstViewHeight = 0;

    @Override
    public void setScrollHeight(int height) {
        firstViewHeight = height;
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
