package com.edusoho.kuozhi.util.html;

import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.edusoho.kuozhi.util.AppUtil;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by howzhi on 14-10-29.
 */
public class EduTagHandler implements Html.TagHandler {

    private int startIndex = 0;
    private int endIndex = 0;
    private HashMap<String, String> attributes;

    @Override
    public void handleTag(boolean opening, String tag, Editable editable, XMLReader xmlReader) {
        if ("span".equalsIgnoreCase(tag)) {
            if (opening) {
                startIndex = editable.length();
                attributes = processAttributes(xmlReader);
            } else {
                endIndex = editable.length();
                String style = attributes.get("style");
                setStyle(style, editable);
            }
        }
    }

    private int parseFontSize(String value)
    {
        if (value == null) {
            return 0;
        }
        if (value.endsWith("px")) {
            value = value.substring(0, value.length() - 2);
        }
        return AppUtil.parseInt(value);
    }

    private void setStyle(String style, Editable editable)
    {
        if (style == null) {
            return;
        }
        Matcher styleMatcher = STYLE_PAT.matcher(style);
        while (styleMatcher.find()) {
            String name = styleMatcher.group(1);
            String value = styleMatcher.group(2);
            if ("color".equalsIgnoreCase(name)) {
                editable.setSpan(
                        new ForegroundColorSpan(Color.parseColor(value)),
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            } else if ("font-size".equalsIgnoreCase(name)) {
                editable.setSpan(
                        new AbsoluteSizeSpan(parseFontSize(value), true),
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            } else if ("background-color".equalsIgnoreCase(name)) {
                editable.setSpan(
                        new BackgroundColorSpan(Color.parseColor(value)),
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
    }

    private HashMap<String, String> processAttributes(XMLReader xmlReader) {
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[])dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = (Integer)lengthField.get(atts);

            for(int i = 0; i < len; i++)
                map.put(data[i * 5 + 1], data[i * 5 + 4]);
        }
        catch (Exception e) {
            Log.d(null, "Exception: " + e);
        }

        return map;
    }

    public static Pattern STYLE_PAT = Pattern.compile(
            "([\\-\\w]+):([#\\-\\w]+);",
            Pattern.DOTALL
    );
}
