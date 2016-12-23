package com.edusoho.kuozhi.v3.view.headStopScroll;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveYOld = moveY;
                moveY = ev.getRawY() - startY;
                float move = moveY - moveYOld;
                moveByY = move < 0 ? 0 : move;
                if(mParent.isStay()){
                    break;
                }
                if (moveY > 0 && getScrollY() == 0) {
                    mParent.scrollBy(0, (int) (-moveByY));
                    return true;
                }
                if (firstViewHeight > Math.abs(mParent.getScrollY())
                        && moveY < 0) {
                    mParent.scrollTo(0, (int) -moveY);
                    return true;
                } else {
                    break;
                }
            case MotionEvent.ACTION_UP:
                startY = 0;
                moveY = 0;
                moveByY = 0;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                startY = (int) ev.getRawY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                moveY = ev.getRawY() - startY;
//                break;
//            case MotionEvent.ACTION_UP:
//                startY = 0;
//                break;
//        }
//        if (getScrollY() == 0 && moveY > 0) {
//            return true;
//        } else {
//            return super.onTouchEvent(ev);
//        }
//    }

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
