package com.edusoho.kuozhi.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by howzhi on 14/12/3.
 */
public class FixHeightViewPager extends ViewPager {

    private boolean isMeasure;
    private ArrayList<Integer> childHeightList;

    public FixHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        childHeightList = new ArrayList<Integer>();
    }

    public FixHeightViewPager(Context context) {
        super(context);
        childHeightList = new ArrayList<Integer>();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isMeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int height = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int expandSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >>2, MeasureSpec.AT_MOST);
            child.measure(widthMeasureSpec, expandSpec);
            //child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            //int h = child.getMeasuredHeight();
            int h = child.getMeasuredHeight();
            Log.d(null, "h-> " + h);
            childHeightList.add(i, h);
            if (h > height)
                height = h;
        }
        Log.d(null, "viewpager count->" + count);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        //setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setIsMeasure(boolean isMeasure)
    {
        this.isMeasure = isMeasure;
    }

    public int getChildHeight(int i)
    {
        Integer height = childHeightList.get(i);
        return height == null ? 0 : height;
    }
}
