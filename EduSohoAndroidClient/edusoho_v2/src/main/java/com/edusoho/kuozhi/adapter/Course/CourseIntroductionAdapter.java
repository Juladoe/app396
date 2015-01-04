package com.edusoho.kuozhi.adapter.Course;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.List;

import ch.boye.httpclientandroidlib.util.TextUtils;
import cn.trinea.android.common.util.ResourceUtils;

/**
 * Created by howzhi on 14/12/2.
 */
public class CourseIntroductionAdapter
        extends RecyclerViewListBaseAdapter<String[], CourseIntroductionAdapter.BaseViewHolder> {

    private static final int WEB_VIEW = 1;
    private static final int NORMAL_VIEW = 0;

    public Handler webViewHandler= new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_IMAGES:
                    Bundle bundle = new Bundle();
                    bundle.putInt("index", msg.arg1);
                    bundle.putStringArray("images", (String[]) msg.obj);
                    EdusohoApp.app.mEngine.runNormalPluginWithBundle(
                            "ViewPagerActivity", mContext, bundle);
                    break;
            }
        }
    };
    private static final int SHOW_IMAGES = 0002;

    public CourseIntroductionAdapter(Context context, int resource)
    {
        super(context, resource);
    }

    @Override
    public void addItem(String[] item) {
        mList.add(item);
        notifyItemInserted(mList.size() - 1);
    }

    @Override
    public void addItems(List<String[]> list) {
        mList.addAll(list);
        notifyItemRangeInserted(mList.size() - 1 - list.size(), mList.size() - 1);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(mResource, viewGroup, false);

        switch (i) {
            case WEB_VIEW:
                 return new WebViewViewHolder(
                         LayoutInflater.from(mContext).inflate(R.layout.course_introduction_item_webview_layout, viewGroup, false));
            case NORMAL_VIEW:
                return new ViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder vh, int i) {
        super.onBindViewHolder(vh, i);
        String[] contents = mList.get(i);
        vh.mTitle.setText(contents[0]);
        String content = contents[1];

        int type = getItemViewType(i);
        switch (type) {
            case WEB_VIEW:
                WebViewViewHolder webViewViewHolder = (WebViewViewHolder) vh;
                String template = ResourceUtils.geFileFromAssets(mContext, "template.html");
                template = template.replace("%content%", content);
                webViewViewHolder.mContent.loadDataWithBaseURL(
                       null, template, "text/html", "utf-8", null);
                break;
            case NORMAL_VIEW:
                ViewHolder viewHolder = (ViewHolder) vh;
                viewHolder.mContent.setText(android.text.TextUtils.isEmpty(content) ? "暂无内容" : content);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == (mList.size() - 1) ? WEB_VIEW : NORMAL_VIEW;
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mTitle;
        public BaseViewHolder(View view){
            super(view);

            mTitle = (TextView) view.findViewById(R.id.course_introduction_title);
        }
    }

    public class ViewHolder extends BaseViewHolder
    {
        public TextView mContent;
        public ViewHolder(View view){
            super(view);

            mContent = (TextView) view.findViewById(R.id.course_introduction_content);
        }
    }

    public class WebViewViewHolder extends BaseViewHolder
    {
        public WebView mContent;

        public WebViewViewHolder(View view){
            super(view);

            mContent = (WebView) view.findViewById(R.id.course_introduction_webview);
            initWebViewSetting(mContent);
        }

        private void initWebViewSetting(WebView webView)
        {
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setSupportMultipleWindows(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setAllowFileAccess(true);
            webSettings.setDefaultTextEncodingName("utf-8");

            webView.addJavascriptInterface(new JavaScriptObj(), "jsobj");
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return true;
                }
            });
        }

        /**
         * js注入对象
         */
        public class JavaScriptObj {

            @JavascriptInterface
            public void showImages(String index, String[] imageArray) {
                Log.d(null, "showImages->" + index);
                Message msg = webViewHandler.obtainMessage(SHOW_IMAGES);
                msg.obj = imageArray;
                msg.arg1 = Integer.parseInt(index);
                msg.sendToTarget();
            }
        }
    }
}
