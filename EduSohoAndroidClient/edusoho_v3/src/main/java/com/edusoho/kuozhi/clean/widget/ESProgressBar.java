package com.edusoho.kuozhi.clean.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by JesseHuang on 2017/4/12.
 */

public class ESProgressBar extends ProgressBar {
    private String str = "10%";

    private Paint mPaint;

    public ESProgressBar(Context context) {
        super(context);
        init();
    }

    public ESProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ESProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        mPaint.getTextBounds(str, 0, str.length(), rect);
        int x = (getProgressDrawable().getIntrinsicWidth() / 2) - rect.centerX();
        int y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(str, x, y, this.mPaint);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setTextSize(40);
        mPaint.setColor(Color.WHITE);
    }
}
