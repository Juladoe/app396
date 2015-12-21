package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.webview.ESWebView;

/**
 * Created by JesseHuang on 15/12/17.
 */
public class TeachFragment extends BaseFragment {
    private ESWebView mWebView;
    private String url = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.webview_activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    protected void initView(View view) {
        mWebView = (ESWebView) view.findViewById(R.id.webView);
    }

    private void initData() {
        Bundle bundle = getArguments();
        url = bundle.getString(Const.WEB_URL);

        if (TextUtils.isEmpty(url)) {
            CommonUtil.longToast(mActivity, "访问的地址不存在");
            return;
        }
        mWebView.initPlugin(mActivity);
        mWebView.loadUrl(url);
    }
}
