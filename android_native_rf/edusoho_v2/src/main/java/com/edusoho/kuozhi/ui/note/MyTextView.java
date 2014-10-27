package com.edusoho.kuozhi.ui.note;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by onewoman on 14-10-24.
 */
public class MyTextView extends TextView {


    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.line));
        canvas.drawLine(0,this.getHeight()-2,this.getWidth(),this.getHeight()-2,paint);
    }
}
