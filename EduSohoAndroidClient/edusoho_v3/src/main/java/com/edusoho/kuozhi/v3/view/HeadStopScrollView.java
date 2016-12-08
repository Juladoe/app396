package com.edusoho.kuozhi.v3.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.edusoho.kuozhi.v3.util.AppUtil;

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
    private View view;

    /**
     * 监听的滑动的子View，用于判断是否要恢复滑动事件
     */
    private View controlChildView;
    /**
     * 滑动停止的高度
     */
    private int viewHeight = 0;
    /**
     *firstViewHeight 头View的高度，用来判断什么时候应该向下分发事件
     */
    private int firstViewHeight = 0;
    public static final String HEADVIEW_CLASSNAME = "xxx";

    public void setView(View view) {
        this.view = view;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }


    public void setFirstViewHeight(int firstViewHeight) {
        this.firstViewHeight = firstViewHeight;
    }

    public void setControlChildView(View controlChildView) {
        this.controlChildView = controlChildView;
    }

    private void init() {
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (t < firstViewHeight && HeadStopScrollView.this.view != null) {
            HeadStopScrollView.this.view.setVisibility(GONE);
        }
        if (t >= firstViewHeight && t - oldt >= 0) {
            canScroll = false;
        }
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    private OnScrollChangeListener onScrollChangeListener;

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    float moveY;

    private boolean canScroll = true;

    public void setCanScroll(boolean isScrolled) {
        this.canScroll = isScrolled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return canScroll ? super.onTouchEvent(motionEvent) : false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return canScroll ? super.onInterceptTouchEvent(ev) : false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = ev.getRawY() - startY;
                if(moveY > 0){
                    if(controlChildView != null
                            && controlChildView.getTop() >= -AppUtil.dp2px(getContext(),2)){
                        setCanScroll(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                startY = 0;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
