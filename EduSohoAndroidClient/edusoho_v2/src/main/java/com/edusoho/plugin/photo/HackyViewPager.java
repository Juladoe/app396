package com.edusoho.plugin.photo;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewParent;

/**
 * Hacky fix for Issue #4 and
 * http://code.google.com/p/android/issues/detail?id=18990
 * <p/>
 * ScaleGestureDetector seems to mess up the touch events, which means that
 * ViewGroups which make use of onInterceptTouchEvent throw a lot of
 * IllegalArgumentException: pointerIndex out of range.
 * <p/>
 * There's not much I can do in my code for now, but we can mask the result by
 * just catching the problem and ignoring it.
 *
 * @author Chris Banes
 */
public class HackyViewPager extends ViewPager {

    private int isDown = 1;
    private float mLastMotionX;
    private float mLastMotionY;

    public HackyViewPager(Context context) {
        super(context);
    }

    public HackyViewPager(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ViewParent parent = getParent();
        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                parent.requestDisallowInterceptTouchEvent(true);
                isDown = 1;
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDown == 1) {
                    if (x - mLastMotionX > 5 && getCurrentItem() == 0) {
                        isDown = 0;
                        parent.requestDisallowInterceptTouchEvent(false);
                        break;
                    }

                    if (x - mLastMotionX < -5 && getCurrentItem() == getAdapter().getCount() - 1) {
                        isDown = 0;
                        parent.requestDisallowInterceptTouchEvent(false);
                        break;
                    }

                    if (y - mLastMotionY > 50 || y - mLastMotionY < -50) {
                        parent.requestDisallowInterceptTouchEvent(false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                parent.requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
