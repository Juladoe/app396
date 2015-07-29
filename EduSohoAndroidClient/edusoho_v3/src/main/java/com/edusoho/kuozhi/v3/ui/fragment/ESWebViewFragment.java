package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.view.View;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.view.webview.ESWebView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by howzhi on 15/7/16.
 */
public class ESWebViewFragment extends BaseFragment {
    private static final String TAG = "ESWebViewFragment";
    protected ESWebView mWebView;
    private PtrClassicFrameLayout mRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_webview);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mRefreshLayout = (PtrClassicFrameLayout)view;
        mWebView = (ESWebView) view.findViewById(R.id.webView);
        mWebView.initPlugin(mActivity);

        mRefreshLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                mWebView.reload();
                mRefreshLayout.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return mWebView.getWebView().getOverScrollY() <= 0;
            }
        });
    }

    public ESWebView getView() {
        return mWebView;
    }
}
