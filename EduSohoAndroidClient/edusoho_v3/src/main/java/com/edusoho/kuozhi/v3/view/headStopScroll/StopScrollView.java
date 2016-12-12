package com.edusoho.kuozhi.v3.view.headStopScroll;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;

/**
 * Created by Zhang on 2016/12/9.
 */

public class StopScrollView extends ScrollView{
    public StopScrollView(Context context) {
        super(context);
    }

    public StopScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StopScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(getChildAt(0).getTop() >= -AppUtil.dp2px(getContext(),2)){
            Bundle bundle = new Bundle();
            bundle.putString("class",getContext().getClass().getSimpleName());
            ((EdusohoApp) ((Activity)getContext()).getApplication())
                    .sendMessage(Const.SCROLL_STATE_SAVE, bundle);
        }
    }
}
