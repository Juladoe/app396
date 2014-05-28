package com.edusohoapp.app.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextPaint;
import android.widget.TextView;

/**
 * Created by howzhi on 14-5-15.
 */
public class EduSohoTextView extends TextView{
    private Context mContext;

    public EduSohoTextView(Context context) {
        super(context);
        mContext = context;
        //initView();
    }

    public EduSohoTextView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        //initView();
    }

    private void initView()
    {
        TextPaint paint = getPaint();
        Rect bounds = new Rect();

        String text = getText().toString();
        int lines = getLineCount();
        paint.getTextBounds(text, 0, 1, bounds);
        System.out.println(lines);
    }

}
