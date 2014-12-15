package com.edusoho.kuozhi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.edusoho.kuozhi.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * 解决低版本兼容的TextView
 * Created by howzhi on 14/12/4.
*/

public class ESImageView extends ImageView {

    private float mDefautAlpha;
    private Context mContext;
    private OnClickListener mOnClickListener;

    public ESImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    public ESImageView(Context context) {
        super(context);
        mContext = context;
    }

    public ESImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView(attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(null, "action->"+ event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mOnClickListener == null) {
                    return true;
                }
                changeAlpha(mDefautAlpha - 0.33f);
                break;
            default:
                changeAlpha(mDefautAlpha);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        mOnClickListener = l;
    }

    private void initView(AttributeSet attrs)
    {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.ESTextView);
        mDefautAlpha = ta.getFloat(R.styleable.ESTextView_es_alpha, 1.0f);
        changeAlpha(mDefautAlpha);
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
