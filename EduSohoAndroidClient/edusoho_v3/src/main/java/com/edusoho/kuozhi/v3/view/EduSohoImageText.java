package com.edusoho.kuozhi.v3.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by JesseHuang on 16/1/27.
 */
public class EduSohoImageText extends LinearLayout {
    private Context mContex;
    private ImageView mImageView;
    private TextView mTextView;
    private String text;
    private Drawable mImageNormal;
    private Drawable mImagePressed;
    private float mTextSize;
    private int imageWidth;
    private int imageHeight;
    private ColorStateList mTextNormalColor;
    private ColorStateList mTextPressedColor;

    public EduSohoImageText(Context context) {
        super(context);
        mContex = context;
    }

    public EduSohoImageText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContex = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        TypedArray ta = mContex.obtainStyledAttributes(attrs, R.styleable.EduSohoImageText);
        text = ta.getString(R.styleable.EduSohoImageText_EduSohoImageText_text);
        mImageNormal = ta.getDrawable(R.styleable.EduSohoImageText_EduSohoImageText_image_normal);
        mImagePressed = ta.getDrawable(R.styleable.EduSohoImageText_EduSohoImageText_image_pressed);
        mTextSize = ta.getDimension(R.styleable.EduSohoImageText_EduSohoImageText_text_size, 0);
        imageWidth = ta.getLayoutDimension(R.styleable.EduSohoImageText_EduSohoImageText_image_width, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageHeight = ta.getLayoutDimension(R.styleable.EduSohoImageText_EduSohoImageText_image_height, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextNormalColor = ta.getColorStateList(R.styleable.EduSohoImageText_EduSohoImageText_text_color_normal);
        mTextPressedColor = ta.getColorStateList(R.styleable.EduSohoImageText_EduSohoImageText_text_color_pressed);

        LayoutParams imageLayout = new LayoutParams(imageWidth, imageHeight);
        imageLayout.gravity = Gravity.CENTER_HORIZONTAL;

        mImageView = new ImageView(mContex);
        mImageView.setImageDrawable(mImageNormal);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(mImageView, imageLayout);

        LayoutParams textLayout = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayout.gravity = Gravity.CENTER_HORIZONTAL;
        mTextView = new TextView(mContex);
        mTextView.setText(text);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mTextView.setTextColor(mTextNormalColor);
        mTextView.setGravity(Gravity.CENTER);
        addView(mTextView, textLayout);

        ta.recycle();
    }

    public void setPressed() {
        mTextView.setTextColor(mTextPressedColor);
        mImageView.setImageDrawable(mImagePressed);
    }

    public void setNormal() {
        mTextView.setTextColor(mTextNormalColor);
        mImageView.setImageDrawable(mImageNormal);
    }

}
