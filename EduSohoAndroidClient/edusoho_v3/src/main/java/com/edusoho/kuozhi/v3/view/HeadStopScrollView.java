package com.edusoho.kuozhi.v3.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.edusoho.kuozhi.v3.view.headStopScroll.CanStopView;

/**
 * Created by zhang on 2016/12/8.
 */
public class HeadStopScrollView extends ScrollView implements CanStopView {
    public HeadStopScrollView(Context context) {
        super(context);
        init();
    }

    public HeadStopScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeadStopScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int startY;
    private int firstViewHeight = 0;

    public void setFirstViewHeight(int firstViewHeight) {
        this.firstViewHeight = firstViewHeight;
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);
        setVerticalScrollBarEnabled(false);
    }

    private boolean mStay = false;

    public void setCanScroll(boolean isScrolled) {
        this.canScroll = isScrolled;
    }

    private boolean canScroll = true;

    public boolean isCanScroll() {
        return canScroll;
    }

    public void setStay(boolean stay) {
        this.mStay = stay;
    }

    public boolean isStay() {
        return mStay;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (t >= firstViewHeight && t - oldt >= 0) {
            setCanScroll(false);
        } else {
            setCanScroll(true);
        }
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (mStay) {
            return;
        }
        super.scrollTo(x, y);
    }

    @Override
    public void scrollBy(int x, int y) {
        if (mStay) {
            return;
        }
        super.scrollBy(x, y);
    }

    private OnScrollChangeListener onScrollChangeListener;

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    float moveY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }


    private boolean scrollStay = false;

    public void setScrollStay(boolean scrollStay) {
        this.scrollStay = scrollStay;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return scrollStay ? true : super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    private boolean isTab = false;


    public int getFirstViewHeight() {
        return firstViewHeight;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }
}
