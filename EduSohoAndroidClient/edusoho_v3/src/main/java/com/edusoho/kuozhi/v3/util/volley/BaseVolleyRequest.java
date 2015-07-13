package com.edusoho.kuozhi.v3.util.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.AppUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by howzhi on 15/7/9.
 */
public abstract class BaseVolleyRequest<T> extends Request<T> {

    protected static final int CACHE_MAX_AGE = 604800;

    public static final int CACHE_AUTO = 0001;
    public static final int CACHE_ALWAYS = 0002;
    public static final int CACHE_NONE = 0003;

    protected Response.Listener<T> mListener;
    protected RequestUrl mRequestUrl;
    protected int mIsCache = CACHE_NONE;
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

    public void setCacheMode(int mode) {
        this.mIsCache = mode;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mRequestUrl.getParams();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = mRequestUrl.getHeads();
        headers.put("Cookie", mRequestLocalManager.getCookie());
        return headers;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public String getCacheKey() {
        if (EdusohoApp.app == null || AppUtil.isNetConnect(EdusohoApp.app)) {
            return null;
        }
        return super.getCacheKey();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String cookie = response.headers.get("Set-Cookie");
        mRequestLocalManager.setCookie(cookie);
        Cache.Entry cache = handleResponseCache(response);
        return Response.success(getResponseData(response), cache);
    }

    protected abstract T getResponseData(NetworkResponse response);

    protected Cache.Entry handleResponseCache(NetworkResponse response) {
        switch (mIsCache) {
            case CACHE_ALWAYS :
                return parseResponseCache(response);
            case CACHE_AUTO :
                return HttpHeaderParser.parseCacheHeaders(response);
            case CACHE_NONE :
        }
        return null;
    }

    private Cache.Entry parseResponseCache(NetworkResponse response) {
        Map<String, String> map = response.headers;
        map.put("Cache-Control", "max-age=" + CACHE_MAX_AGE);
        NetworkResponse networkResponse = new NetworkResponse(response.statusCode, response.data, map, true);
        Cache.Entry cache = HttpHeaderParser.parseCacheHeaders(networkResponse);

        return cache;
    }

    protected static class RequestLocalManager {

        public List<String> cookie;
        private static RequestLocalManager instace;

        private RequestLocalManager() {
            cookie = new ArrayList<>();
        }

        public static RequestLocalManager getManager() {
            synchronized (RequestLocalManager.class) {
                if (instace == null) {
                    instace = new RequestLocalManager();
                }
            }

            return instace;
        }

        public List<String> getCookieList() {
            return cookie;
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
