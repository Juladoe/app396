package com.edusoho.kuozhi.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.util.HashMap;

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
    private FrameLayout mIconLayout;
    private ImageView mUpdateIcon;

    private boolean mIsUpdate;
    private HashMap<String, Object> notifyTypes;

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
        color = ta.getColorStateList(R.styleable.EduSohoTextBtn_fontColor);

        LinearLayout.LayoutParams layoutParams = new LayoutParams(0, 0);
        layoutParams.gravity = Gravity.CENTER;
        setLayoutParams(layoutParams);
        setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams childlp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        mIconLayout = new FrameLayout(mContext);
        mIcon = new EduSohoIconView(mContext);
        mIcon.setText(icon);
        mIcon.setTextColor(color);
        mIcon.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * iconSizeScale);
        mIconLayout.setLayoutParams(childlp);
        mIconLayout.addView(mIcon);
        addView(mIconLayout);

        mText = new TextView(mContext);
        mText.setSingleLine();
        mText.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        mText.setText(text);
        mText.setGravity(Gravity.CENTER);
        mText.setTextColor(color);
        mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        mText.setLayoutParams(childlp);
        addView(mText);

        if (TextUtils.isEmpty(text)) {
            mText.setVisibility(GONE);
        }
        notifyTypes = new HashMap<String, Object>();
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

    public void setUpdateIcon()
    {
        mIsUpdate = true;
        mUpdateIcon = new ImageView(mContext);
        mUpdateIcon.setImageResource(R.drawable.update_bg);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        mUpdateIcon.setLayoutParams(layoutParams);
        mIconLayout.addView(mUpdateIcon);
    }

    public void clearUpdateIcon()
    {
        mIsUpdate = false;
        mIconLayout.removeView(mUpdateIcon);
    }

    public boolean getUpdateMode()
    {
        return mIsUpdate;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mIcon.setEnabled(enabled);
        mText.setEnabled(enabled);
    }

    public void addNotifyType(String type)
    {
        notifyTypes.put(type, null);
    }

    public void addNotifyTypes(String[] types)
    {
        for (String type : types) {
            addNotifyType(type);
        }
    }

    public boolean hasNotify(String type){
        return notifyTypes.containsKey(type);
    }
}
