package com.edusoho.kuozhi.core.cache;

import android.content.ContentValues;
import android.content.Context;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.core.model.Cache;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.SqliteUtil;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by howzhi on 14-9-10.
 */
public class AppDbCache implements AppCache{

    private SqliteUtil sqliteUtil;
    private static AppDbCache instance;

    private AppDbCache(Context context){
        sqliteUtil = new SqliteUtil(context, null, null);
    }

    private static String[] FILTERS = {
            Const.CHECKTOKEN,
            Const.LOGOUT,
            Const.LOGIN,
            Const.REGIST
    };

    public static AppDbCache getInstance(Context context)
    {
        synchronized (AppDbCache.class) {
            if (instance == null) {
                instance = new AppDbCache(context);
            }
        }
        return instance;
    }

    private boolean isCache(String url)
    {
        int result = Arrays.binarySearch(FILTERS, url, new Comparator<String>() {
            @Override
            public int compare(String s, String s2) {
                if (s2.contains(s)) {
                    return -1;
                }
                return 0;
            }
        });
        return result >= 0;
    }

    @Override
    public <T> void cacheCallback(String url, Cache cache, AjaxCallback<T> ajaxCallback)
    {
        AjaxStatus ajaxStatus = new AjaxStatus(Const.CACHE_CODE, "cache");
        ajaxCallback.callback(url, (T)cache.get(), ajaxStatus);
    }

    @Override
    public boolean hasCache(RequestUrl key)
    {
        return false;
    }

    @Override
    public Cache getCache(RequestUrl url)
    {
        String cacheKey = AppUtil.coverUrlToCacheKey(url);
        Cache cache = sqliteUtil.query("select * from data_cache where key = ?", new String[] { cacheKey });
        return cache;
    }

    @Override
    public void setCache(RequestUrl requestUrl, Object cache)
    {
        String cacheKey = AppUtil.coverUrlToCacheKey(requestUrl);
        if (isCache(requestUrl.url)) {
            ContentValues cv = new ContentValues();
            cv.put("type", "");
            cv.put("key", cacheKey);
            cv.put("value", cache.toString());
            sqliteUtil.insert("data_cache", cv);
        }
    }

    @Override
    public void delCaceh(RequestUrl key)
    {

    }

    @Override
    public void clear()
    {

    }
}
