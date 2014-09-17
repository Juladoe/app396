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
    private float iconSizeScale;
    private ColorStateList color;
    private TextView mText;
    private EduSohoIconView mIcon;

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
        iconSizeScale = ta.getFloat(R.styleable.EduSohoTextBtn_iconSizeScale, 1.0f);
        color = ta.getColorStateList(R.styleable.EduSohoTextBtn_color);

        LinearLayout.LayoutParams layoutParams = new LayoutParams(0, 0);
        layoutParams.gravity = Gravity.CENTER;
        setLayoutParams(layoutParams);

        mIcon = new EduSohoIconView(mContext);
        mIcon.setText(icon);
        mIcon.setTextColor(color);
        mIcon.setTextSize(size * iconSizeScale);
        addView(mIcon);

        mText = new TextView(mContext);
        mText.setText(text);
        mText.setGravity(Gravity.CENTER);
        mText.setTextColor(color);
        mText.setTextSize(size);
        addView(mText);
    }

    public void setText(String text)
    {
        mText.setText(text);
    }

    public void setText(int resId)
    {
        mText.setText(resId);
    }

    public void setText(String text, int iconId)
    {
        mText.setText(text);
        mIcon.setText(iconId);
    }

    public void setTextColor(int color)
    {
        mText.setTextColor(color);
        mIcon.setTextColor(color);
    }

    public void setIcon(String icon)
    {
        mIcon.setText(icon);
    }

    public void setIcon(int iconId)
    {
        mIcon.setText(iconId);
    }
}
