package com.edusoho.kuozhi.v3.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * 头部向上滑一段距离后，滑动事件交给子View的ScrollView
 * Created by zhang on 2016/12/8.
 */
public class HeadStopScrollView extends ScrollView {
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
    /**
     * firstViewHeight 头View的高度，用来判断什么时候应该向下分发事件
     */
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
        }else{
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

    private OnScrollChangeListener onScrollChangeListener;

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    float moveY;

    private List<CanStopView> mChildScrolls = new ArrayList<>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            mChildScrolls.clear();
            searchCanScrollChild(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    private void searchCanScrollChild(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view instanceof CanStopView) {
                mChildScrolls.add((CanStopView) view);
                ((CanStopView) view).bindParent(this);
                ((CanStopView) view).setScrollHeight(firstViewHeight);
                continue;
            }
            if (view instanceof ViewGroup) {
                searchCanScrollChild((ViewGroup) view);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = ev.getRawY() - startY;
                break;
            case MotionEvent.ACTION_UP:
                startY = 0;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isTab = false;

    public interface CanStopView {
        void bindParent(HeadStopScrollView headStopScrollView);

        void setScrollHeight(int height);
    }

    public int getFirstViewHeight() {
        return firstViewHeight;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }

}
