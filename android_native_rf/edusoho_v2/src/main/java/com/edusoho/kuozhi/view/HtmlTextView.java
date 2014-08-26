package com.edusoho.kuozhi.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by howzhi on 14-5-15.
 */
public class HtmlTextView extends TextView{
    private Context mContext;
    private Pattern colorPattern;
    private int mDefaultColor;
    private static final String nameSpace = "android";

    public HtmlTextView(Context context) {
        super(context);
        mContext = context;
    }

    public HtmlTextView(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    private void initView(android.util.AttributeSet attrs)
    {
        mDefaultColor = attrs.getAttributeResourceValue(nameSpace, "textColor", R.color.system_text);
        colorPattern = Pattern.compile("\\{@color\\/([^\\s]+)\\s([^\\s]+)\\}", Pattern.DOTALL);
        String text = getText().toString();

        int start = 0;
        StringBuffer stringBuilder = new StringBuffer();
        ArrayList<CharacterStyleWrap> styleArrayList = new ArrayList<CharacterStyleWrap>();
        Matcher matcher = colorPattern.matcher(text);
        while (matcher.find()) {
            String color = matcher.group(1);
            String colorText = matcher.group(2);
            matcher.appendReplacement(stringBuilder, colorText);
            start = stringBuilder.length() - colorText.length();
            styleArrayList.add(new CharacterStyleWrap(
                    new ForegroundColorSpan(parseColor(color)), start, start + colorText.length()));
        }

        matcher.appendTail(stringBuilder);
        setTextColor(stringBuilder.toString(), styleArrayList);
    }

    private int parseColor(String color)
    {
        int id = mContext.getResources().getIdentifier(color, "color", mContext.getPackageName());
        return mContext.getResources().getColor(id == 0 ? mDefaultColor : id);
    }

    private void setTextColor(String text, ArrayList<CharacterStyleWrap> styleArrayList)
    {
        Spannable spannable = new SpannableString(text);
        for (CharacterStyleWrap wrap : styleArrayList) {
            spannable.setSpan(
                    wrap.style, wrap.start, wrap.end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        setText(spannable);
    }

    private class CharacterStyleWrap
    {
        public CharacterStyle style;
        public int start;
        public int end;

        public CharacterStyleWrap(CharacterStyle style, int start, int end)
        {
            this.style = style;
            this.start = start;
            this.end = end;
        }
    }
}
