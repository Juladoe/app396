package com.edusoho.kuozhi.clean.http;

import com.edusoho.kuozhi.v3.EdusohoApp;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by JesseHuang on 2017/4/18.
 */

public class HttpUtils {

    private static final String TOKEN_KEY = "X-Auth-Token";
    private static final String MAPI_V2_TOKEN_KEY = "token";
    private static HttpUtils mInstance;
    private static String mBaseUrl;
    private static Map<String, String> mHeaderMaps = new TreeMap<>();

    public static HttpUtils getInstance() {
        mBaseUrl = "";
        mHeaderMaps.clear();
        if (mInstance == null) {
            synchronized (HttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new HttpUtils();
                }
            }
        }
        return mInstance;
    }

    public HttpUtils baseOnMapiV2() {
        mHeaderMaps.clear();
        mBaseUrl = EdusohoApp.app.host + "/mapi_v2/";
        return mInstance;
    }

    public HttpUtils baseOnApi() {
        mHeaderMaps.clear();
        mBaseUrl = EdusohoApp.app.host + "/api/";
        return mInstance;
    }

    public <T> T createApi(final Class<T> clazz) {
        if ("".equals(mBaseUrl) || mBaseUrl == null) {
            return RetrofitClient.getInstance(mHeaderMaps).create(clazz);
        } else {
            return RetrofitClient.getInstance(mBaseUrl, mHeaderMaps).create(clazz);
        }
    }

    public HttpUtils addTokenHeader(String token) {
        mHeaderMaps.clear();
        mHeaderMaps.put(TOKEN_KEY, token == null ? "" : token);
        return mInstance;
    }

    public HttpUtils addMapiV2TokenHeader(String token) {
        mHeaderMaps.put(MAPI_V2_TOKEN_KEY, token == null ? "" : token);
        return mInstance;
    }

    public HttpUtils addHeader(Map<String, String> headerMaps) {
        mHeaderMaps.clear();
        mHeaderMaps.putAll(headerMaps);
        return mInstance;
    }
}
