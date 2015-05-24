package com.edusoho.kuozhi.v3.view.webview;

import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.edusoho.kuozhi.v3.Cache.request.RequestCallback;
import com.edusoho.kuozhi.v3.Cache.request.RequestHandler;
import com.edusoho.kuozhi.v3.Cache.request.RequestManager;
import com.edusoho.kuozhi.v3.Cache.request.model.Request;
import com.edusoho.kuozhi.v3.Cache.request.model.Response;
import com.edusoho.kuozhi.v3.util.AppUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by howzhi on 15/4/29.
 */
public class ESWebViewRequestManager extends RequestManager {

    private static final String TAG = "ESWebViewRequestManager";
    private Context mContext;
    private String mUserAgent;
    private HttpClient mHttpClient;

    public ESWebViewRequestManager(Context context, String userAgent)
    {
        this.mContext = context;
        this.mUserAgent = userAgent;
        initHttpClient();
        registHandler(".+", new WebViewRequestHandler());
    }

    private void initHttpClient()
    {
        mHttpClient = new DefaultHttpClient();

        mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        mHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
    }

    public void setUserAgent(String userAgent)
    {
        this.mUserAgent = userAgent;
    }

    private HttpGet getHttpGet(String url)
    {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", mUserAgent);
        Log.d(null, "User-Agent : " + mUserAgent);

        return httpGet;
    }

    @Override
    public void destory() {
        mHttpClient.getConnectionManager().shutdown();
    }

    @Override
    public <T> T blocPost(Request request, RequestCallback<T> callback) {
        return null;
    }

    @Override
    public <T> T blockGet(Request request, RequestCallback<T> callback) {
        Response response = new Response();
        handleRequest(request, response);

        return callback.onResponse(response);
    }

    private void saveApiRequestCache(String apiUrl)
    {
        Log.d(null, "saveApiRequestCache->" + apiUrl);
        File storage = AppUtil.getAppStorage();
        File apiStore = new File(storage, "apirequest");
        if (!apiStore.exists()) {
            apiStore.mkdir();
        }
        try {
            HttpResponse response = mHttpClient.execute(getHttpGet(apiUrl));
            AppUtil.saveStreamToFile(
                    response.getEntity().getContent(),
                    new File(apiStore, AppUtil.md5(apiUrl)), true
            );

        }catch (Exception e) {

        }
    }

    private File getApiRequestCache(String apiUrl)
    {
        File storage = AppUtil.getAppStorage();
        File apiStore = new File(storage, "apirequest");

        return new File(apiStore, AppUtil.md5(apiUrl));
    }

    private boolean isApiRequest(String urlPath)
    {
        Log.d(null, "urlPath " + urlPath);
        return urlPath.startsWith("/mapi_v2");
    }

    private String getFileExtension(String filePath)
    {
        return MimeTypeMap.getFileExtensionFromUrl(filePath);
    }

    private String getFileMime(String extension)
    {
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return mimeType == null ? "text/html" : mimeType;
    }

    private void saveCacheToFile(String url, File cache)
    {
        Log.d(null, "saveCacheFile->" + url);
        try {
            HttpResponse response = mHttpClient.execute(getHttpGet(url));
            AppUtil.saveStreamToFile(response.getEntity().getContent(), cache, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void get(Request request, RequestCallback callback) {

    }

    @Override
    public void post(Request request, RequestCallback callback) {

    }

    public class WebViewRequestHandler implements RequestHandler
    {
        @Override
        public void handler(final Request request, Response response) {
            Log.d(TAG, "WebViewRequestHandler " + request.url);

            File storage = AppUtil.getAppStorage();
            String extension = getFileExtension(request.url);
            final File cache = new File(storage, String.format("%s.%s", AppUtil.md5(request.url), extension));

            if (isApiRequest(request.getPath())) {
                if (AppUtil.isNetConnect(mContext)) {
                    saveApiRequestCache(request.url);
                } else {
                    try {
                        response.setEncoding("utf-8");
                        response.setMimeType("text/html");
                        response.setContent(new FileInputStream(getApiRequestCache(request.url)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;
            }

            if (cache == null || !cache.exists()) {
                mWorkExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        saveCacheToFile(request.url, cache);
                    }
                });
                return;
            }

            try {
                response.setEncoding("utf-8");
                response.setMimeType(getFileMime(extension));
                response.setContent(new FileInputStream(cache));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
