package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.util.Const;

/**
 * Created by howzhi on 14-9-21.
 */
public class AboutFragment extends BaseFragment {

    public static final String URL = "url";
    public static final String CONTENT = "content";
    public static final String TYPE = "type";
    public static final int FROM_URL = 0010;
    public static final int FROM_STR = 0020;

    private WebView about_content;
    private String mUrl;
    private int mType;
    private String mContent;
    private String mTitle;

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.about_fragment_layout);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUrl = bundle.getString(URL);
            mTitle = bundle.getString(Const.ACTIONBAT_TITLE);
            mContent = bundle.getString(AboutFragment.CONTENT);
            mType = bundle.getInt(AboutFragment.TYPE, FROM_URL);

        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        about_content = (WebView) view.findViewById(R.id.about_content);
        about_content.getSettings().setJavaScriptEnabled(true);
        about_content.getSettings().setDefaultTextEncodingName("UTF-8");
        about_content.getSettings().setUseWideViewPort(true);

        about_content.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    showProgress(false);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(mTitle)) {
                    changeTitle(title);
                }
                Log.d(null, "title->" + title);
            }
        });
        loadContent();
    }

    private void loadContent()
    {
        showProgress(true);
        if (mType == FROM_URL) {
            about_content.loadUrl(mUrl);
        } else {
            Log.d(null, "mContent->" + mContent);
            about_content.loadDataWithBaseURL(null, mContent, "text/html", "utf-8", null);
        }
    }
}
