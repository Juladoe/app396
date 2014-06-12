package com.edusoho.kowzhi.view;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

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
        Typeface iconfont = Typeface.createFromAsset(mContext.getAssets(), "normal.ttf");
        setTypeface(iconfont);
    }
}
