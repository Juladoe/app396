package com.edusoho.kuozhi.v3.view.webview;

import android.app.Activity;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by howzhi on 15/7/15.
 */
public class ESWebViewFactory {

    private static final String TAG = "ESWebViewFactory";
    private Queue<ESCordovaWebView> mCacheQueue;
    private static ESWebViewFactory factory;
    private BaseActivity mActivity;
    private Handler mHandler;

    private ESWebViewFactory(BaseActivity activity) {
        mActivity = activity;
        mHandler = new Handler(activity.getMainLooper());
        mCacheQueue = new ArrayDeque<ESCordovaWebView>();
    }

    public static void init(BaseActivity activity) {
        factory = new ESWebViewFactory(activity);
        factory.factoryWebView(null);
    }

    public static ESWebViewFactory getFactory() {
        if (factory == null) {
            throw new RuntimeException("ESWebViewFactory not init");
        }
        return factory;
    }

    public void destory() {
        Log.d(TAG, "ESWebViewFactory destory");
        ESCordovaWebView webView;
        while ( (webView = mCacheQueue.poll()) != null) {
            Log.d(TAG, "mCacheQueue destory");
            webView.handleDestroy();
            webView.destroy();
        }
        factory = null;
    }

    public ESCordovaWebView getWebView() {
        Log.d(TAG, "get mCacheWebView");
        return mCacheQueue.poll();
    }

    public void factoryWebView(AttributeSet attributeSet) {
        mHandler.post(new FactoryRunnable(attributeSet));
    }

    private class FactoryRunnable implements Runnable {

        private AttributeSet mAttributeSet;

        public FactoryRunnable(AttributeSet attributeSet) {
            this.mAttributeSet = attributeSet;
        }

        @Override
        public void run() {
            Log.d(TAG, "create mCacheWebView");
            ESCordovaWebView webView = ESCordovaWebView.create(mActivity, mAttributeSet);
            mCacheQueue.add(webView);

            Log.d(TAG, "mCacheQueue size:" + mCacheQueue.size());
        }
    }
}
