package com.edusoho.kuozhi.v3.core;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.edusoho.kuozhi.v3.model.sys.Cache;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;

import java.util.HashMap;

/**
 * Created by howzhi on 14-9-10.
 */
public class AppDbCache implements AppCache {

    private SqliteUtil sqliteUtil;
    private static AppDbCache instance;

    private AppDbCache(Context context) {
        sqliteUtil = new SqliteUtil(context, null, null);
    }

    private static String[] FILTERS = {
            Const.CHECKTOKEN,
            Const.CATEGORYS,
            Const.WEEK_COURSES,
            Const.RECOMMEND_COURSES,
            Const.SCHOOL_BANNER,
            Const.SCHOOL_Announcement
    };

    public static AppDbCache getInstance(Context context) {
        synchronized (AppDbCache.class) {
            if (instance == null) {
                instance = new AppDbCache(context);
            }
        }
        return instance;
    }

    private boolean isCache(String url) {
        Uri queryUrl = Uri.parse(url);
        if (queryUrl == null) {
            return false;
        }
        for (String filter : FILTERS) {
            String path = queryUrl.getPath();
            if (path.endsWith(filter)) {
                return true;
            }
        }

        return false;
    }

//    @Override
//    public <T> void cacheCallback(String url, Cache cache, AjaxCallback<T> ajaxCallback)
//    {
//        AjaxStatus ajaxStatus = new AjaxStatus(Const.CACHE_CODE, "cache");
//        ajaxCallback.callback(url, (T)cache.get(), ajaxStatus);
//    }

    @Override
    public boolean hasCache(RequestUrl key) {
        return false;
    }

    private String buildRequestRealPath(RequestUrl requestUrl) {
        String url = requestUrl.url;
        StringBuilder stringBuilder = new StringBuilder(url);
        stringBuilder.append("?");
        HashMap<String, Object> params = requestUrl.getAllParams();
        for (String key : params.keySet()) {
            String value = params.get(key).toString();
            stringBuilder.append(key)
                    .append("=")
                    .append(value)
                    .append("&");
        }

        return stringBuilder.toString();
    }

    @Override
    public Cache getCache(RequestUrl requestUrl) {
        if (!isCache(requestUrl.url)) {
            return null;
        }

        Log.d(null, "get cache ->" + requestUrl.url);
        String cacheKey = CommonUtil.coverUrlToCacheKey(requestUrl);
        Cache cache = sqliteUtil.query("select * from data_cache where key = ?", new String[]{cacheKey});
        return cache;
    }

    @Override
    public void updateCache(RequestUrl requestUrl, Object cache) {
        if (cache == null || !isCache(requestUrl.url)) {
            return;
        }
        Log.d(null, "update cache->" + requestUrl.url);
        String cacheKey = CommonUtil.coverUrlToCacheKey(requestUrl);
        ContentValues cv = new ContentValues();
        cv.put("value", cache.toString());

        sqliteUtil.update("data_cache", cv, "key=?", new String[]{cacheKey});
    }

    @Override
    public void setCache(RequestUrl requestUrl, Object cache) {
        if (!isCache(requestUrl.url) || cache == null) {
            return;
        }
        Log.d(null, "set cache->" + requestUrl.url);
        String cacheKey = CommonUtil.coverUrlToCacheKey(requestUrl);
        ContentValues cv = new ContentValues();
        cv.put("type", "");
        cv.put("key", cacheKey);
        cv.put("value", cache.toString());
        sqliteUtil.insert("data_cache", cv);
    }

    @Override
    public void delCache(RequestUrl key) {
    }

    @Override
    public void clear() {
    }
}
