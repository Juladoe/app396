package com.edusoho.kuozhi.clean.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.edusoho.kuozhi.R;

/**
 * Created by JesseHuang on 2017/4/12.
 */

public class ESProgressBar extends ProgressBar {
    private Paint mPaint;
    private float mScale;
    private float mPercentSize;

    public ESProgressBar(Context context) {
        super(context);
    }

    public ESProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ESProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ESProgressBar);
        mPercentSize = ta.getDimensionPixelSize(TypedValue.COMPLEX_UNIT_PX, 0);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setColor(Color.WHITE);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setTextSize(mPercentSize);
        mScale = (float) getProgress() / getMax();
        String progress = (int) (mScale * 100) + "%";
        float textWidth = getWidth() * mScale;
        float textHeight = getHeight();
        float tX = (textWidth - getFontLength(mPaint, progress)) / 2;
        float tY = (textHeight - getFontHeight(mPaint)) / 2 + getFontLeading(mPaint);
        canvas.drawText(progress, tX, tY, mPaint);
    }

    private float getFontLength(Paint paint, String str) {
        return paint.measureText(str);
    }

    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    public static float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }
}
