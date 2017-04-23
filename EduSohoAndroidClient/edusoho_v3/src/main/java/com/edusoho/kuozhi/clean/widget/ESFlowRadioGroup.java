package com.edusoho.kuozhi.clean.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DF on 2017/3/25.
 */

public class ESFlowRadioGroup extends RadioGroup {

    public ESFlowRadioGroup(Context context) {
        super(context);
    }

    public ESFlowRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    List<List<View>> mAllViews;//保存所有行的所有View
    List<Integer> mLineHeight;//保存每一行的行高

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);

        int width = 0, height = 0;
        int lineWidth = 0, lineHeight = 0;
        int childWidth = 0, childHeight = 0;

        mAllViews = new ArrayList<>();
        mLineHeight = new ArrayList<>();

        List<View> lineViews = new ArrayList<>();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();

            childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;

            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);

                lineWidth = childWidth;
                lineHeight = childHeight;
                lineViews = new ArrayList<>();
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(childHeight, lineHeight);
            }
            lineViews.add(child);

            if (i == (count - 1)) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);
        width += getPaddingLeft() + getPaddingRight();
        height += getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = getPaddingTop();//开始布局子view的 top距离
        int left = getPaddingLeft();//开始布局子view的 left距离

        int lineNum = mAllViews.size();//行数
        List<View> lineView;
        int lineHeight;
        for (int i = 0; i < lineNum; i++) {
            lineView = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            for (int j = 0; j < lineView.size(); j++) {
                View child = lineView.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
                int ld = left + params.leftMargin;
                int td = top + params.topMargin;
                int rd = ld + child.getMeasuredWidth();//不需要加上 params.rightMargin,
                int bd = td + child.getMeasuredHeight();//不需要加上 params.bottomMargin, 因为在 onMeasure , 中已经加在了 lineHeight 中
                child.layout(ld, td, rd, bd);

                left += child.getMeasuredWidth() + params.leftMargin + params.rightMargin;//因为在 这里添加了;
            }

            left = getPaddingLeft();
            top += lineHeight;
        }
    }
}
