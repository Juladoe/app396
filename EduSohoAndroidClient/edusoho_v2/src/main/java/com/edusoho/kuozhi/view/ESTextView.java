package com.edusoho.kuozhi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * 解决低版本兼容的TextView
 * Created by howzhi on 14/12/4.
 */
public class ESTextView extends TextView {

    private Context mContext;

    public ESTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    public ESTextView(Context context) {
        super(context);
        mContext = context;
    }

    public ESTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs)
    {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.ESTextView);
        float alpha = ta.getFloat(R.styleable.ESTextView_es_alpha, 0.0f);
        if (Build.VERSION.SDK_INT < 11) {
            ViewHelper.setAlpha(this, alpha);
        } else {
            setAlpha(alpha);
        }
    }

    public void changeAlpha(float alpha)
    {
        if (Build.VERSION.SDK_INT < 11) {
            ViewHelper.setAlpha(this, alpha);
        } else {
            setAlpha(alpha);
        }
    }
}
