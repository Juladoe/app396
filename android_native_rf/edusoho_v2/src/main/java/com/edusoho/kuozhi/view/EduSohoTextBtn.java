package com.edusoho.kuozhi.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by howzhi on 14-8-7.
 */
public class EduSohoTextBtn extends LinearLayout {
    private Context mContext;
    private String text;
    private String icon;
    private int size;
    private ColorStateList color;

    public EduSohoTextBtn(Context context) {
        super(context);
        mContext = context;
    }

    public EduSohoTextBtn(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    private void initView(android.util.AttributeSet attrs)
    {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.EduSohoTextBtn);
        text = ta.getString(R.styleable.EduSohoTextBtn_text);
        icon = ta.getString(R.styleable.EduSohoTextBtn_image);
        size = ta.getDimensionPixelSize(R.styleable.EduSohoTextBtn_size, 0);
        color = ta.getColorStateList(R.styleable.EduSohoTextBtn_color);

        setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LayoutParams(0, 0);
        layoutParams.gravity = Gravity.CENTER;
        setLayoutParams(layoutParams);

        EduSohoIconView iconView = new EduSohoIconView(mContext);
        iconView.setText(icon);
        iconView.setTextColor(color);
        iconView.setTextSize(size * 2.4f);
        addView(iconView);

        TextView textView = new TextView(mContext);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(color);
        textView.setTextSize(size);
        addView(textView);
    }
}
