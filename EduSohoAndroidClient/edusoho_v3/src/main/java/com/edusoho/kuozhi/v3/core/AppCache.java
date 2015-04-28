package com.edusoho.kuozhi.v3.core;

import com.androidquery.callback.AjaxCallback;
import com.edusoho.kuozhi.v3.model.sys.Cache;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;

/**
 * Created by howzhi on 14-7-23.
 */
public interface AppCache {

    public <T> void cacheCallback(String url, Cache cache, AjaxCallback<T> ajaxCallback);

    public boolean hasCache(RequestUrl key);

    public Cache getCache(RequestUrl key);

    public void setCache(RequestUrl key, Object cache);

    public void updateCache(RequestUrl key, Object cache);

    public void delCache(RequestUrl key);

    public void clear();
}
