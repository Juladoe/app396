package com.edusoho.kuozhi.v3.view.webview;

import android.app.Activity;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;

import org.apache.cordova.Config;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by howzhi on 15/7/15.
 */
public class ESWebViewFactory {

    private static final String TAG = "ESWebViewFactory";
    private Queue<ESCordovaWebView> mCacheQueue;
    private static ESWebViewFactory factory;

    private ESWebViewFactory() {
        mCacheQueue = new ArrayDeque<ESCordovaWebView>();
    }

    public static void init(BaseActivity activity) {
        Config.init(activity);
        factory = new ESWebViewFactory();
    }

    public static ESWebViewFactory getFactory() {
        if (factory == null) {
            init(null);
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

    public ESCordovaWebView getWebView(Activity activity) {
        return ESCordovaWebView.create(activity, null);
    }

}
