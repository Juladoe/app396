package com.edusoho.kuozhi.v3.view.headStopScroll;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.HeadStopScrollView;

/**
 * Created by Zhang on 2016/12/9.
 */

public class StopScrollView extends ScrollView implements HeadStopScrollView.CanStopView{
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
            Bundle bundle = new Bundle();
            bundle.putString("class", getContext().getClass().getSimpleName());
            ((EdusohoApp) ((Activity) getContext()).getApplication())
                    .sendMessage(Const.SCROLL_STATE_SAVE, bundle);
            mCanScroll = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mCanScroll ? super.onTouchEvent(ev) : false;
    }

    @Override
    public void setCanScroll(boolean canScroll) {
        mCanScroll = true;
    }
}
