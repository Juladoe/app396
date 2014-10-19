package com.edusoho.kuozhi.core;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.core.model.Cache;
import com.edusoho.kuozhi.util.Const;


import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by howzhi on 14-7-23.
 */
public class AppCache {

    private HashMap<String, Cache> cacheMap;
    private static AppCache instance;

    private AppCache(){
        cacheMap = new HashMap<String, Cache>();
    }

    private static String[] routing = {
            Const.COURSE_COLUMN
    };

    public static AppCache getInstance()
    {
        synchronized (AppCache.class) {
            if (instance == null) {
                instance = new AppCache();
            }
        }
        return instance;
    }

    private boolean isCache(String url)
    {
        int result = Arrays.binarySearch(routing, url, new Comparator<String>() {
            @Override
            public int compare(String s, String s2) {
                if (s2.contains(s)) {
                    return 0;
                }
                return -1;
            }
        });
        return result >= 0;
    }

    public <T> void cacheCallback(String url, Cache cache, AjaxCallback<T> ajaxCallback)
    {
        AjaxStatus ajaxStatus = new AjaxStatus(200, "cache");
        ajaxCallback.callback(url, (T)cache.get(), ajaxStatus);
    }

    public boolean hasCache(String key)
    {
        return cacheMap.containsKey(key);
    }

    public Cache getCache(String key)
    {
        return cacheMap.get(key);
    }

    public void setCache(String key, Object cache)
    {
        if (isCache(key)) {
            cacheMap.put(key, new Cache(cache));
        }
    }

    public void delCaceh(String key)
    {
        cacheMap.remove(key);
    }

    public void clear()
    {
        cacheMap.clear();
    }
}
