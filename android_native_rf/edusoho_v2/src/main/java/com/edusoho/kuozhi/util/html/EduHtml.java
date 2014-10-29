package com.edusoho.kuozhi.util.html;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-10-29.
 */
public class EduHtml {

    private ArrayList<String> imageArray;
    private Context mContext;

    private EduHtml(Context context)
    {
        this.mContext = context;
    }

    public static SpannableStringBuilder addImageClickListener(
            SpannableStringBuilder spaned, TextView textView, Context context)
    {
        EduHtml instance = new EduHtml(context);
        instance.imageArray = new ArrayList<String>();
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        spaned = instance.addImageClick(spaned);

        return spaned;
    }

    private SpannableStringBuilder addImageClick(SpannableStringBuilder spanned)
    {
        CharacterStyle[] characterStyles = spanned.getSpans(0, spanned.length(), CharacterStyle.class);
        int index = 0;
        for (CharacterStyle characterStyle : characterStyles) {
            if (characterStyle instanceof ImageSpan) {
                ImageSpan imageSpan = (ImageSpan) characterStyle;
                String src = imageSpan.getSource();
                imageArray.add(index, src);
                ImageClickSpan clickSpan = new ImageClickSpan(src, index++);
                int start = spanned.getSpanStart(characterStyle);
                int end = spanned.getSpanEnd(characterStyle);
                spanned.setSpan(clickSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spanned;
    }

    private class ImageClickSpan extends ClickableSpan
    {
        private int mIndex;
        private String imageUrl;

        public ImageClickSpan(String url, int index)
        {
            this.mIndex = index;
            this.imageUrl = url;
        }

        @Override
        public void onClick(View view) {
            Log.d(null, "image click-->" + imageUrl);
            Bundle bundle = new Bundle();
            bundle.putInt("index", mIndex);
            bundle.putStringArrayList("imageList", imageArray);
            EdusohoApp.app.mEngine.runNormalPluginWithBundle("ViewPagerActivity", mContext, bundle);
        }
    }
}
