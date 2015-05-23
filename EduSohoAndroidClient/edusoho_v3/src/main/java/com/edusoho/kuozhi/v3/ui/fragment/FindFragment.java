package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.base.CordovaContext;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by JesseHuang on 15/4/26.
 */
public class FindFragment extends BaseFragment implements CordovaInterface {

    protected final ExecutorService threadPool = Executors.newCachedThreadPool();
    protected CordovaPlugin activityResultCallback = null;
    protected boolean keepRunning = true;
    protected boolean activityResultKeepRunning;
    protected CordovaWebView webView;

    @Override
    public void onAttach(Activity activity) {
        Log.d("FindFragment-->", "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContainerView(R.layout.fragment_webview);
        Log.d("FindFragment-->", "onCreate");
        mActivity.setTitle(getString(R.string.title_find));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutInflater localInflater = inflater.cloneInContext(new CordovaContext(getActivity(), this));
        View rootView = localInflater.inflate(R.layout.fragment_webview, container, false);
        webView = (CordovaWebView) rootView.findViewById(R.id.webView);
        Config.init(getActivity());
        //webView.loadUrl("http://trymob.edusoho.cn/apph5/client/index.html", 5000);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        return rootView;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        if (webView == null) {
            webView = (CordovaWebView) view.findViewById(R.id.webView);
            Config.init(getActivity());
            //webView.loadUrl("http://trymob.edusoho.cn/apph5/client/index.html", 5000);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("FindFragment-->", "onActivityCreated");
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
        if (item.getItemId() == R.id.find_search) {

            //TODO 跳转到搜索页面
            app.mEngine.runNormalPlugin("", mActivity, null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CordovaPlugin callback = this.activityResultCallback;
        if (callback != null) {
            callback.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void startActivityForResult(CordovaPlugin cordovaPlugin, Intent intent, int i) {
        this.activityResultCallback = cordovaPlugin;
        this.activityResultKeepRunning = this.keepRunning;

        // If multitasking turned on, then disable it for activities that return results
        if (cordovaPlugin != null) {
            this.keepRunning = false;
        }

        // Start activity
        super.startActivityForResult(intent, i);
    }

    @Override
    public void setActivityResultCallback(CordovaPlugin cordovaPlugin) {
        this.activityResultCallback = cordovaPlugin;

    }

    @Override
    public Object onMessage(String s, Object o) {
        return null;
    }

    @Override
    public ExecutorService getThreadPool() {
        return threadPool;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.handleDestroy();
        }
        if (webView.pluginManager != null) {
            webView.pluginManager.onDestroy();
        }
    }
}
