package com.edusoho.kuozhi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import cn.trinea.android.common.util.ImageUtils;

/**
 * Created by howzhi on 14-5-12.
 */
public class EduSohoIconView extends TextView{

    private Context mContext;
    public EduSohoIconView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public EduSohoIconView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView()
    {
        Typeface iconfont = Typeface.createFromAsset(mContext.getAssets(), "iconfont.ttf");
        setTypeface(iconfont);
        setGravity(Gravity.CENTER);
    }
}
