package com.edusoho.kuozhi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * 解决低版本兼容的TextView
 * Created by howzhi on 14/12/4.
 */
public class ESTextView extends TextView {

    private float mDefautAlpha = 1.0f;
    private Context mContext;
    private OnClickListener mOnClickListener;

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(null, "action->" + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mOnClickListener == null) {
                    return super.onTouchEvent(event);
                }
                setTextViewAlpha(mDefautAlpha);
                break;
            default:
                setTextViewAlpha(mDefautAlpha);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        mOnClickListener = l;
    }

    private void initView(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.ESTextView);
        mDefautAlpha = ta.getFloat(R.styleable.ESTextView_es_alpha, 1.0f);
        setTextViewAlpha(mDefautAlpha);
    }

    public void setDefaultAlpha(float alpha) {
        mDefautAlpha = alpha;
    }

    public void setTextViewAlpha(float alpha) {
        int alphaTextColor = (int) (0xFF * alpha) << 24 | getCurrentTextColor() & 0xFFFFFF;
        this.setTextColor(alphaTextColor);
    }
}
