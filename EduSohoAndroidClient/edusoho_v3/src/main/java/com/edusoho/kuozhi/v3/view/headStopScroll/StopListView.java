package com.edusoho.kuozhi.v3.view.headStopScroll;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.HeadStopScrollView;

/**
 * Created by Zhang on 2016/12/9.
 */

public class StopListView extends ListView implements AbsListView.OnScrollListener,
        HeadStopScrollView.CanStopView {
    public StopListView(Context context) {
        super(context);
        init();
    }

    public StopListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StopListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (getFirstVisiblePosition() == 0 &&
                getChildAt(0).getTop() >= -AppUtil.dp2px(getContext(), 2)) {
            Bundle bundle = new Bundle();
            bundle.putString("class", getContext().getClass().getSimpleName());
            ((EdusohoApp) ((Activity) getContext()).getApplication())
                    .sendMessage(Const.SCROLL_STATE_SAVE, bundle);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void setCanScroll(boolean canScroll) {

    }
}
