package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by howzhi on 14-8-27.
 */
public class ScrollWidget extends ScrollView {

    private int mPaddingHeight;
    private View mHeadView;
    private GestureDetector mGestureDetector;
    private boolean isTop;
    private boolean isBottom;
    private float lastdistanceY;
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

    @Override
    protected void onScrollChanged(
            int leftOfVisibleView, int topOfVisibleView, int oldLeftOfVisibleView, int oldTopOfVisibleView) {
        super.onScrollChanged(
                leftOfVisibleView, topOfVisibleView, oldLeftOfVisibleView, oldTopOfVisibleView);
        if (mScrollChangeListener == null) {
            return;
        }
        mScrollChangeListener.onScroll(
                leftOfVisibleView, topOfVisibleView, oldLeftOfVisibleView, oldTopOfVisibleView);
        int height = getHeight();
        int childHeight = getChildAt(0).getHeight();
        if (childHeight < height) {
            return;
        }

        if (topOfVisibleView >= (childHeight - height)) {
            if (isBottom) {
                return;
            }
            isBottom = true;
            mScrollChangeListener.onBottom();
        } else {
            isBottom = false;
        }
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
                new EdusohoAnimWrap(mHeadView), "height", mHeadView.getHeight(), 0);
        objectAnimator.setDuration(240);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    private void scrollToDwonAnim()
    {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(
                new EdusohoAnimWrap(mHeadView), "height", 0, mPaddingHeight);
        objectAnimator.setDuration(160);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    private void setContentFragmentPaddingTop(int top)
    {
        int oldTop = this.getPaddingTop();
        int spaceTop = oldTop - top;
        spaceTop = spaceTop < 0 ? 0 : spaceTop;
        spaceTop = spaceTop > mPaddingHeight ? mPaddingHeight : spaceTop;
        this.setPadding(
                this.getPaddingLeft(),
                spaceTop,
                this.getPaddingRight(),
                this.getPaddingBottom()
        );
    }

    private void setMarginTop(int top)
    {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        int oldTopMargin = layoutParams.topMargin;
        layoutParams.topMargin = oldTopMargin - top;
        setLayoutParams(layoutParams);
    }

    private void setHeadViewHeight(int y)
    {
        ViewGroup.LayoutParams layoutParams = mHeadView.getLayoutParams();

        int height = layoutParams.height;
        layoutParams.height = height - y;
        Log.d(null, "layoutParams.height->" + layoutParams.height);
        mHeadView.setLayoutParams(layoutParams);
    }

    private int getHeadViewHeight()
    {
        return mHeadView.getLayoutParams().height;
    }

    public void setHeadView(View view)
    {
        mHeadView = view;
    }

    private void initListener()
    {
        mPaddingHeight = getResources().getDimensionPixelOffset(R.dimen.course_details_pic);
        mScrollListener = new DefaultScrollListener();
        mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(mGestureDetector.onTouchEvent(motionEvent)) {
                    return true;
                }

                return false;
            }
        });

        setScrollListener(new ScrollWidget.DefaultScrollListener(){
            @Override
            public void scrollToDown() {
                Log.d(null, "scrollToDown->");
            }

            @Override
            public void scrollToUp() {
                Log.d(null, "scrollToUp->");
            }

            @Override
            public void scrollUp(int distanceY) {
                setHeadViewHeight(distanceY);
            }

            @Override
            public void scrollDown(int distanceY) {
                setHeadViewHeight(distanceY);
            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
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
        public void onBottom();
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
