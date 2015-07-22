package com.edusoho.kuozhi.v3.view.webview;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.webkit.MimeTypeMap;
import com.android.volley.Response.*;
import com.android.volley.Request.*;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.cache.request.RequestCallback;
import com.edusoho.kuozhi.v3.cache.request.RequestHandler;
import com.edusoho.kuozhi.v3.cache.request.RequestManager;
import com.edusoho.kuozhi.v3.cache.request.model.Request;
import com.edusoho.kuozhi.v3.cache.request.model.ResourceResponse;
import com.edusoho.kuozhi.v3.cache.request.model.Response;
import com.edusoho.kuozhi.v3.model.htmlapp.UpdateAppMeta;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.VolleySingleton;
import com.edusoho.kuozhi.v3.util.volley.BaseVolleyRequest;
import com.edusoho.kuozhi.v3.util.volley.StringVolleyRequest;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by howzhi on 15/4/29.
 */
public class ESWebViewRequestManager extends RequestManager {

    private static final String TAG = "ESWebViewRequestManager";
    private Context mContext;
    private String mUserAgent;
    private String mCode;
    private HttpClient mHttpClient;
    private static HashMap<String, RequestManager> mManagerMap;

    static {
        mManagerMap = new HashMap<>(5);
    }

    public static void clear() {
        for (RequestManager manager : mManagerMap.values()) {
            manager.destroy();
        }
        mManagerMap.clear();
    }

    public static RequestManager getRequestManager(Context context, String code) {
        RequestManager instance = mManagerMap.get(code);
        if (instance == null) {
            instance = new ESWebViewRequestManager(context, code);
            mManagerMap.put(code, instance);
        }

        return instance;
    }

    private ESWebViewRequestManager(Context context, String code)
    {
        super();
        this.mContext = context;
        this.mCode = code;
        this.mUserAgent = String.format("%s%s%s", Build.MODEL, " Android-kuozhi ", Build.VERSION.SDK);
        initHttpClient();
        registHandler(".+/mapi_v2/.+", new ApiRequestHandler());
        registHandler(".+", new WebViewRequestHandler());
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
    public void updateApp(RequestUrl requestUrl, final RequestCallback<String> callback) {
        VolleySingleton volley = VolleySingleton.getInstance(mContext);
        volley.getRequestQueue();
        StringVolleyRequest stringRequest = new StringVolleyRequest(
                Method.GET, requestUrl, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onResponse(new Response<String>(response));
            }
        }, null);
        stringRequest.setTag(requestUrl.url);
        volley.addToRequestQueue(stringRequest);
    }

    @Override
    public void destroy() {
        super.destroy();
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

    private File getApiStorage(String host)
    {
        File storage = AppUtil.getAppStorage();
        File apiStore = new File(storage, String.format("%s/%s", host, "apirequest"));
        if (!apiStore.exists()) {
            apiStore.mkdir();
        }
        return apiStore;
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

    private File getResourceStorage(String host)
    {
        File storage = AppUtil.getSchoolStorage(host);
        File srcDir = new File(storage, mCode);
        if (!srcDir.exists()) {
            srcDir.mkdirs();
        }

        return srcDir;
    }

    private boolean isFileDirExists(File file) {
        if (! file.getParentFile().exists()) {
            return file.getParentFile().mkdirs();
        }

        return true;
    }

    private File saveFile(File storage, Request request) {
        File cache = new File(storage, request.getName() + "_temp");
        HttpGet httpGet = getHttpGet(request.url);
        try {
            if (cache.exists()) {
                httpGet.setHeader("Range", "bytes=" + cache.length());
            }

            HttpResponse response = mHttpClient.execute(httpGet);
            if (AppUtil.saveStreamToFile(response.getEntity().getContent(), cache, true)) {
                File realFile = new File(cache.getAbsolutePath().replace("_temp", ""));
                if (cache.renameTo(realFile)) {
                    return realFile;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpGet.abort();
        }

        return null;
    }

    private boolean unZipFile(String schoolHost, File zinFile) {
        File schoolStorage = AppUtil.getSchoolStorage(schoolHost);
        File schoolAppStorage = new File(schoolStorage, mCode);
        if (! schoolAppStorage.exists()) {
            schoolAppStorage.mkdir();
        }
        try {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zinFile));
            for (ZipEntry e; (e = zin.getNextEntry()) != null; zin.closeEntry()) {
                File file = new File(schoolAppStorage, e.getName());
                if (e.isDirectory()) {
                    file.mkdirs();
                    continue;
                }

                if (! isFileDirExists(file)) {
                    continue;
                }
                if (AppUtil.saveStreamToFile(zin, file, false)) {
                    Log.d(TAG, String.format("file %s is unzip", e.getName()));
                }
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void downloadResource(final Request request, final RequestCallback callback) {
        mWorkExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String domain = EdusohoApp.app.domain;
                File storage = getResourceStorage(domain);
                File saveFile = saveFile(storage, request);
                if (unZipFile(domain, saveFile)) {
                    callback.onResponse(new Response(true));
                    return;
                }
                callback.onResponse(new Response(false));
            }
        });
    }

    @Override
    public void get(final Request request, final RequestCallback callback) {
    }

    @Override
    public void post(Request request, RequestCallback callback) {

    }

    private File getResourceFile(String host, String fileName) {
        File storage = getResourceStorage(host);
        File cache = new File(storage, fileName);

        return cache;
    }

    public class ApiRequestHandler implements RequestHandler
    {
        private VolleySingleton mVolley;

        public ApiRequestHandler() {
            this.mVolley = VolleySingleton.getInstance(mContext);
        }

        @Override
        public void handler(Request request, Response response) {
            Log.d(TAG, "api handler :" + request.url);

            if (request.getPath().endsWith(String.format(Const.MOBILE_APP_URL, "/", mCode))) {
                File cache = getResourceFile(request.getHost(), "index.html");
                if (cache.exists()) {
                    handlerResponse(cache, response);
                }
                return;
            }

            handlerApiRequest(request, response);
        }

        private void handlerApiRequest(Request request, Response proxyResponse) {
            mVolley.getRequestQueue();
            final RequestUrl requestUrl = new RequestUrl(request.url);
            requestUrl.setHeads(new String[] {
                    "token", EdusohoApp.app == null ? "" : EdusohoApp.app.token
            });

            RequestFuture<String> future = RequestFuture.newFuture();
            StringVolleyRequest stringRequest = new StringVolleyRequest(Method.GET, requestUrl, future, future);

            if (mWebView.isGoBack()) {
                stringRequest.setCacheUseMode(BaseVolleyRequest.ALWAYS_USE_CACHE);
                Log.d(TAG, "use cache");
            }
            stringRequest.setTag(requestUrl.url);
            mVolley.addToRequestQueue(stringRequest);

            String result = "";
            try {
                result = future.get();
            } catch (Exception e) {
            }
            proxyResponse.setEncoding("utf-8");
            proxyResponse.setContent(new ByteArrayInputStream(result.getBytes()));
        }
    }

    private void handlerResponse(File file, Response response) {
        try {
            String extension = getFileExtension(file.getName());
            response.setEncoding("utf-8");
            response.setMimeType(getFileMime(extension));
            response.setContent(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class WebViewRequestHandler implements RequestHandler
    {
        private final String[] MIME_FILTERS = { "html", "js", "css" };

        private boolean filterMime(String fileName) {
            String extension = getFileExtension(fileName);
            return CommonUtil.inArray(extension, MIME_FILTERS);
        }

        @Override
        public void handler(Request request, Response response) {
            String path = request.getPath(mCode);

            Response cacheResponse = mResoucrCache.get(path);
            if (cacheResponse != null) {
                Log.d(TAG, "mem cache :" + request.url);
                response.setResponse(cacheResponse);
                return;
            }

            File cache = getResourceFile(request.getHost(), path);
            if (! cache.exists()) {
                return;
            }

            Log.d(TAG, "file cache :" + request.url);
            handlerResponse(cache, response);
            if (! filterMime(cache.getName())) {
                return;
            }
            executeTask(new SaveResourceCacheTask(path, cache));
        }
    }

    private class SaveResourceCacheTask extends Task {

        private String path;
        private File cache;

        public SaveResourceCacheTask(String path, File cache) {
            this.path = path;
            this.cache = cache;
        }

        @Override
        public void run() {
            setResourceCache(path, cache);
        }

        private void setResourceCache(String path, File cache) {
            try {
                String extension = getFileExtension(cache.getName());
                String mime = getFileMime(extension);

                byte[] fileData = EntityUtils.toByteArray(new FileEntity(cache, extension));
                Response response = new ResourceResponse(fileData);
                response.setEncoding("utf-8");
                response.setMimeType(mime);
                mResoucrCache.put(path, response);
                Log.d(TAG, "set mem cache :" + path);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
    }
}
