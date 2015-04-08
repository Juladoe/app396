package com.edusoho.kuozhi.ui.fragment;

/**
 * Created by howzhi on 15/4/3.
 */
public class DefaultLessonFragment extends WebVideoLessonFragment {

    @Override
    protected void loadContent() {
        mWebView.loadUrl(mUri);
    }
}
