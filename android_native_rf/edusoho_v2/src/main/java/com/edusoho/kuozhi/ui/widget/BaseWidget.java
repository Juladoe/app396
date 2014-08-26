package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.view.View;
import android.widget.Button;

/**
 * Created by howzhi on 14-8-10.
 */
public abstract class BaseWidget extends View{

    private Context mContext;

    public BaseWidget(Context context) {
        super(context);
        mContext = context;
    }

    public BaseWidget(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    protected abstract void initView(android.util.AttributeSet attrs);
}
