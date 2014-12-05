package com.edusoho.kuozhi.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ProgressBar;

import com.edusoho.kuozhi.R;

/**
 * Created by howzhi on 14/12/4.
*/

public class LoadProgress extends ProgressBar {

    private static final String nameSpace = "android";
    private Context mContext;
    public LoadProgress(Context context) {
        super(context);
        mContext = context;
    }

    public LoadProgress(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    private void initView(android.util.AttributeSet attrs)
    {
        int width = attrs.getAttributeIntValue(nameSpace, "width", 0);
        int height = attrs.getAttributeIntValue(nameSpace, "height", 0);
        AnimationDrawable animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.page_loading_anim);
        int num = animationDrawable.getNumberOfFrames();
        for (int i=0; i < num; i++) {
            Drawable frameDrawable = animationDrawable.getFrame(i);
            frameDrawable.setBounds(0, 0, width, height);
        }
        animationDrawable.setBounds(0, 0, width, height);
        setIndeterminateDrawable(animationDrawable);
    }
}
