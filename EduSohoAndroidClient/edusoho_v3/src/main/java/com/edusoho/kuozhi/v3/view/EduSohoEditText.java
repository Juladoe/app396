package com.edusoho.kuozhi.v3.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Melomelon on 2015/5/26.
 */
public class EduSohoEditText extends EditText {

    private Paint mPaint;
    public EduSohoEditText(Context context) {
        super(context);
    }

    public EduSohoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.LTGRAY);

        setBackgroundColor(Color.TRANSPARENT);

        canvas.drawRoundRect(new RectF(4+this.getScrollX(), 4+this.getScrollY(),
                this.getWidth()-4+this.getScrollX(), this.getHeight()+ this.getScrollY()-4), 20,20, mPaint);
    }
}
