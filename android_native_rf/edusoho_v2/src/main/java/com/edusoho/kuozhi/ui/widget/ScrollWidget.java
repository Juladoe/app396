package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ScrollView;

import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by howzhi on 14-8-27.
 */
public class ScrollWidget extends ScrollView {

    private int mPaddingHeight;
    private boolean isMoveing;
    private GestureDetector mGestureDetector;
    private boolean isTop;
    private boolean isDown;
    private ScrollListener mScrollListener;
    private ScrollChangeListener mScrollChangeListener;

    public ScrollWidget(Context context) {
        super(context);
        initListener();
    }

    public ScrollWidget(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        initListener();
    }

    public void setScrollChangeListener(ScrollChangeListener listener)
    {
        this.mScrollChangeListener = listener;
    }

    public void setScrollListener(ScrollListener scrollListener)
    {
        mScrollListener = scrollListener;
    }

    private void scrollToUpAnim()
    {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(
                new EdusohoAnimWrap(this), "paddingTop", this.getPaddingTop(), 0);
        objectAnimator.setDuration(240);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    private void scrollToDwonAnim()
    {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(
                new EdusohoAnimWrap(this), "paddingTop", this.getPaddingTop(), mPaddingHeight);
        objectAnimator.setDuration(100);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    private void setContentFragmentPaddingTop(int top)
    {
        int oldTop = this.getPaddingTop();
        this.setPadding(
                this.getPaddingLeft(),
                oldTop - top,
                this.getPaddingRight(),
                this.getPaddingBottom()
        );
    }

    private void initListener()
    {
        mPaddingHeight = getPaddingTop();
        mScrollListener = new DefaultScrollListener();
        mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                int paddingTop = getPaddingTop();
                if (paddingTop > 0 && distanceY > 0) {
                    mScrollListener.scrollUp((int)distanceY);
                    return true;
                }

                if (distanceY < 0 && paddingTop < mPaddingHeight) {
                    mScrollListener.scrollDown((int)distanceY);
                    return true;
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                int paddingTop = getPaddingTop();
                if (paddingTop < mPaddingHeight && velocityY > 0) {
                    Log.d(null, "下滑动");
                    isDown = true;
                    isTop = false;
                    mScrollListener.scrollToDown();
                    return true;
                }
                if (paddingTop >= 0 && velocityY < 0) {
                    Log.d(null, "上滑动");
                    isTop = true;
                    isDown = false;
                    mScrollListener.scrollToUp();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int scrollY = getScrollY();
                if (scrollY > 0) {
                    return false;
                }
                boolean result = mGestureDetector.onTouchEvent(motionEvent);
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        int paddingTop = getPaddingTop();
                        Log.d(null, "padding->" + paddingTop);
                        if (paddingTop > 0 && paddingTop < mPaddingHeight) {
                            return true;
                        }
                }
                return result;
            }
        });

        setScrollListener(new ScrollWidget.DefaultScrollListener(){
            @Override
            public void scrollToDown() {
                scrollToDwonAnim();
            }

            @Override
            public void scrollToUp() {
                scrollToUpAnim();
            }

            @Override
            public void scrollUp(int distanceY) {
                setContentFragmentPaddingTop(distanceY);
            }

            @Override
            public void scrollDown(int distanceY) {
                setContentFragmentPaddingTop(distanceY);
            }
        });
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollChangeListener == null) {
            return;
        }
        mScrollChangeListener.onScroll(l, t, oldl, oldt);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int paddingTop = getPaddingTop();
        if (paddingTop > 0 && paddingTop < mPaddingHeight) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public static class DefaultScrollListener implements ScrollListener
    {
        public void scrollToDown(){}
        public void scrollToUp(){}
        public void scrollUp(int distanceY){}
        public void scrollDown(int distanceY){}
    }

    public static interface ScrollChangeListener
    {
        public void onScroll(int l, int t, int oldl, int oldt);
    }

    public static interface ScrollListener
    {
        public void scrollToDown();
        public void scrollToUp();
        public void scrollUp(int distanceY);
        public void scrollDown(int distanceY);
    }
}
