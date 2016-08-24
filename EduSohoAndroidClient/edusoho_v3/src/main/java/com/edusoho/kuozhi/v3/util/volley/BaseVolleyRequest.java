package com.edusoho.kuozhi.v3.util.volley;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.AppUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by howzhi on 15/7/9.
 */
public abstract class BaseVolleyRequest<T> extends Request<T> {

    private static final String TAG = "BaseVolleyRequest";

    protected static final int CACHE_MAX_AGE = 604800;

    public static final int CACHE_AUTO = 0001;
    public static final int CACHE_ALWAYS = 0002;
    public static final int CACHE_NONE = 0003;

    public static final int ALWAYS_USE_CACHE = 0010;
    public static final int AUTO_USE_CACHE = 0020;

    public static final String PARSE_RESPONSE = "parseResponse";

    protected Response.Listener<T> mListener;
    protected RequestUrl mRequestUrl;
    protected int mIsCache = CACHE_NONE;
    protected int mCacheUseMode = AUTO_USE_CACHE;
    private RequestLocalManager mRequestLocalManager;

    public BaseVolleyRequest(
            int method,
            RequestUrl requestUrl,
            Response.Listener<T> listener,
            Response.ErrorListener errorListener
    ) {
        super(method, requestUrl.url, errorListener);

        this.mRequestUrl = requestUrl;
        mListener = listener;
        initRequest(method);
        mRequestLocalManager = RequestLocalManager.getManager();
    }

    protected void initRequest(int method) {
        if (method == Method.GET) {
            mIsCache = CACHE_ALWAYS;
        }
    }

    public void setCacheUseMode(int mode) {
        this.mCacheUseMode = mode;
    }

    public void setCacheMode(int mode) {
        this.mIsCache = mode;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mRequestUrl.isMuiltkeyParams() ? mRequestUrl.getMuiltKeyParams() : mRequestUrl.getParams();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = mRequestUrl.getHeads();
        headers.put("Cookie", mRequestLocalManager.getCookie());
        Log.d(TAG, "getHeaders: " + mRequestLocalManager.getCookie());
        return headers;
    }

    @Override
    protected void deliverResponse(T response) {
        setTag(null);
        mListener.onResponse(response);
    }

    @Override
    public String getCacheKey() {
        if (mCacheUseMode == ALWAYS_USE_CACHE && mIsCache != CACHE_NONE) {
            return super.getCacheKey();
        }
        if (!PARSE_RESPONSE.equals(getTag()) && AppUtil.isNetConnect(EdusohoApp.app)) {
            return null;
        }
        return super.getCacheKey();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String cookie = response.headers.get("Set-Cookie");
        if (cookie != null) {
            mRequestLocalManager.setCookie(cookie);
        }
        Cache.Entry cache = handleResponseCache(response);

        setTag(PARSE_RESPONSE);
        return Response.success(getResponseData(response), cache);
    }

    protected abstract T getResponseData(NetworkResponse response);

    protected Cache.Entry handleResponseCache(NetworkResponse response) {

        switch (mIsCache) {
            case CACHE_ALWAYS:
                return parseResponseCache(response);
            case CACHE_AUTO:
                return HttpHeaderParser.parseCacheHeaders(response);
            case CACHE_NONE:
        }
        return null;
    }

    private Cache.Entry parseResponseCache(NetworkResponse response) {
        Map<String, String> map = response.headers;
        map.put("Cache-Control", String.format("max-age=%d,stale-while-revalidate=%d", 0, CACHE_MAX_AGE));
        NetworkResponse networkResponse = new NetworkResponse(response.statusCode, response.data, map, true);
        Cache.Entry cache = HttpHeaderParser.parseCacheHeaders(networkResponse);
        return cache;
    }

    protected static class RequestLocalManager {

        public List<String> cookie;
        private static RequestLocalManager instace;

        private RequestLocalManager() {
            cookie = new CopyOnWriteArrayList<>();
        }

        public static RequestLocalManager getManager() {
            synchronized (RequestLocalManager.class) {
                if (instace == null) {
                    instace = new RequestLocalManager();
                }
            }

            return instace;
        }

        public String getCookie() {
            StringBuilder builder = new StringBuilder();
            for (String key : cookie) {
                builder.append(key).append(";");
            }
            return builder.toString();
        }

        public void setCookie(String value) {
            cookie.add(value);
        }
    }
}
