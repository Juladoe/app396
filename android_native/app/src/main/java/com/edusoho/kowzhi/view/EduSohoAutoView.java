package com.edusoho.kowzhi.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-5-15.
 */
public class EduSohoAutoView extends LinearLayout {

    private Context mContext;
    private int mParentWidth;
    private ArrayList<View> childs;

    public EduSohoAutoView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public EduSohoAutoView(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public void setParentWidth(int width)
    {
        mParentWidth = width;
    }

    private void initView()
    {
        childs = new ArrayList<View>();
    }

    public void reLayout()
    {

    }

    public void addItem(View view)
    {
        LinearLayout row = null;
        int count = getChildCount();
        if (count == 0) {
            row = new LinearLayout(mContext);
            addView(row);
        } else {
            row = (LinearLayout) getChildAt(count - 1);
        }
        row.addView(view);
        measure(0, 0);

        int rowW = row.getMeasuredWidth();
        if (rowW > mParentWidth) {
            row.removeViewAt(row.getChildCount() - 1);
            row = new LinearLayout(mContext);
            addView(row);
            row.addView(view);
        }

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)view.getLayoutParams();
        lp.setMargins(10, 10, 10, 10);
        view.setLayoutParams(lp);
    }

}
