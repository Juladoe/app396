package com.edusoho.kowzhi.view;

import android.content.Context;
import android.webkit.WebView;

/**
 * Created by howzhi on 14-6-2.
 */
public class EduSohoWebView extends WebView{
    private Context mContext;

    public EduSohoWebView(Context context) {
        super(context);
        mContext = context;
        //initView();
    }

    public EduSohoWebView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        //initView();
    }

    private void initView()
    {

    }
}
