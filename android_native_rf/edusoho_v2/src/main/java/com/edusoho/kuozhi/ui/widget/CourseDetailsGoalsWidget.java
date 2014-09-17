package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by howzhi on 14-8-27.
 */
public class CourseDetailsGoalsWidget extends CourseDetailsLabelWidget {

    private TextView mContentView;
    private static final String STYLE = "<style>*{color:#808080;}</style>";

    public CourseDetailsGoalsWidget(Context context) {
        super(context);
    }

    public CourseDetailsGoalsWidget(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(AttributeSet attrs) {
        super.initView(attrs);

        mContentView= new TextView(mContext);
        mContentView.setMaxLines(4);
        mContentView.setEllipsize(TextUtils.TruncateAt.END);
        Resources resources = mContext.getResources();
        mContentView.setLineSpacing(resources.getDimension(R.dimen.course_details_widget_label_padding), 1);
        mContentView.setTextColor(resources.getColor(R.color.system_normal_text));
        mContentView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.course_details_widget));
        setContentView(mContentView);
    }

    public void setHtml(String text)
    {
        removeContentView();
        WebView webView = new WebView(mContext);
        webView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(false);
        webSettings.setDefaultTextEncodingName("utf-8");
        webView.setBackgroundColor(getResources().getColor(R.color.system_normal_bg));

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });

        setContentView(webView);
        webView.loadDataWithBaseURL(null, STYLE + text, "text/html", "utf-8", null);
    }

    public void setText(String text)
    {
        mContentView.setText(text);
    }
}
