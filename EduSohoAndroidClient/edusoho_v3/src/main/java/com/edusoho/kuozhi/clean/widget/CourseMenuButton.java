package com.edusoho.kuozhi.clean.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.util.AppUtil;

/**
 * Created by JesseHuang on 2017/4/23.
 */

public class CourseMenuButton extends LinearLayout {

    public CourseMenuButton(Context context) {
        super(context);
    }

    public CourseMenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CourseMenuButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CourseMenuButton);

        String image = ta.getString(R.styleable.CourseMenuButton_button_image);
        int imageColor = ta.getInteger(R.styleable.CourseMenuButton_image_color, Color.WHITE);
        int imageSize = ta.getDimensionPixelSize(R.styleable.CourseMenuButton_image_size, 0);
        Drawable drawable = ta.getDrawable(R.styleable.CourseMenuButton_image_background);
        int width = AppUtil.dp2px(context, 66);
        int height = AppUtil.dp2px(context, 66);
        LayoutParams imageLayoutParams = new LayoutParams(width, height);
        ESIconView esIconView = new ESIconView(context);
        imageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        esIconView.setText(image);
        esIconView.setTextColor(imageColor);
        esIconView.setTextSize(TypedValue.COMPLEX_UNIT_PX, imageSize);
        esIconView.setBackground(drawable);

        addView(esIconView, imageLayoutParams);


        String text = ta.getString(R.styleable.CourseMenuButton_button_text);
        int textColor = ta.getInteger(R.styleable.CourseMenuButton_button_text_color, Color.WHITE);
        int textSize = ta.getDimensionPixelSize(R.styleable.CourseMenuButton_button_text_size, 0);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(textColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        LayoutParams textLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        textLayoutParams.topMargin = AppUtil.dp2px(context, 15);
        addView(textView, textLayoutParams);
        ta.recycle();
    }
}
