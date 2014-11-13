package com.edusoho.kuozhi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by howzhi on 14-10-8.
 */
public class FixHeightListView extends ListView {

    public FixHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixHeightListView(Context context) {
        super(context);
    }

    public FixHeightListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
