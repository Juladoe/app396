package com.edusoho.kuozhi.v3.view.webview;

import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.edusoho.kuozhi.v3.cache.request.RequestCallback;
import com.edusoho.kuozhi.v3.cache.request.RequestHandler;
import com.edusoho.kuozhi.v3.cache.request.RequestManager;
import com.edusoho.kuozhi.v3.cache.request.model.Request;
import com.edusoho.kuozhi.v3.cache.request.model.Response;
import com.edusoho.kuozhi.v3.util.AppUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

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
        //registHandler(".+", new WebViewRequestHandler());
    }

    private void initHttpClient()
    {
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(params, 50);
        //超时
        HttpConnectionParams.setConnectionTimeout(params, 3000);
        HttpConnectionParams.setSoTimeout(params, 3000);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        mHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schReg), params);
    }

    public void setUserAgent(String userAgent)
    {
        this.mUserAgent = userAgent;
    }

    private HttpGet getHttpGet(String url)
    {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", mUserAgent);

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

    private void saveApiRequestCache(Request request)
    {
        Log.d(null, "saveApiRequestCache->" + request.url);
        File apiStore = getApiStorage(request.getHost());
        try {
            HttpResponse response = mHttpClient.execute(getHttpGet(request.url));
            AppUtil.saveStreamToFile(
                    response.getEntity().getContent(),
                    new File(apiStore, AppUtil.md5(request.url)),
                    true
            );

        }catch (Exception e) {

        }
    }

    private File getApiStorage(String host)
    {
        File storage = AppUtil.getAppStorage();
        File apiStore = new File(storage, String.format("%s/%s", host, "apirequest"));
        if (!apiStore.exists()) {
            apiStore.mkdir();
        }
        return apiStore;
    }

    private File getApiRequestCache(Request request)
    {
        File apiStore = getApiStorage(request.getHost());
        return new File(apiStore, AppUtil.md5(request.url));
    }

    private boolean isApiRequest(String urlPath)
    {
        Log.d(TAG, "urlPath " + urlPath);
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

    private File getResourceStorage(Request request)
    {
        File storage = AppUtil.getSchoolStorage(request.getHost());
        File srcDir = new File(storage, request.getDir());
        if (!srcDir.exists()) {
            srcDir.mkdirs();
        }

        return srcDir;
    }

    private void saveCacheToFile(Request request)
    {
        File storage = getResourceStorage(request);

        File cache = new File(storage, request.getName() + "_temp");
        HttpGet httpGet = getHttpGet(request.url);
        try {
            if (cache.exists()) {
                Log.d(TAG, "saveCacheFile Range->" + request.url);
                httpGet.setHeader("Range", "bytes=" + cache.length());
            }

            HttpResponse response = mHttpClient.execute(httpGet);
            if (AppUtil.saveStreamToFile(response.getEntity().getContent(),cache,true)) {
                if (cache.renameTo(new File(cache.getAbsolutePath().replace("_temp", "")))) {
                    Log.d(TAG, "down successed->" + cache.getAbsolutePath());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpGet.abort();
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

            File storage = getResourceStorage(request);
            File cache = new File(storage, request.getName());

            if (isApiRequest(request.getPath())) {
                if (AppUtil.isNetConnect(mContext)) {
                    saveApiRequestCache(request);
                } else {
                    try {
                        response.setEncoding("utf-8");
                        response.setMimeType("text/html");
                        response.setContent(new FileInputStream(getApiRequestCache(request)));
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
                        saveCacheToFile(request);
                    }
                });
                return;
            }

            try {
                String extension = getFileExtension(request.url);
                response.setEncoding("utf-8");
                response.setMimeType(getFileMime(extension));
                response.setContent(new FileInputStream(cache));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
