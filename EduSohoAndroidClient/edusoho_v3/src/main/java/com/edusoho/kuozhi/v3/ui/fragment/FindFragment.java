package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.view.webview.ESWebView;

/**
 * Created by JesseHuang on 15/4/26.
 */
public class FindFragment extends BaseFragment{

    private static final String TAG = "FindFragment";

    protected ESWebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.setTitle(getString(R.string.title_find));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        LayoutInflater localInflater = inflater.cloneInContext(mContext);
        View rootView = localInflater.inflate(R.layout.fragment_webview, container, false);
        webView = (ESWebView) rootView.findViewById(R.id.webView);
        webView.initPlugin(mActivity);
        webView.loadApp("main");
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        setHasOptionsMenu(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            mActivity.setTitle(getString(R.string.title_find));
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.find_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
        if (webView != null) {
            webView.getRequestManager().destroy();
            webView.destroy();
        }
    }

    public ESWebView getView() {
        return webView;
    }

}
