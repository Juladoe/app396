package com.edusoho.kuozhi.core.cache;

import com.androidquery.callback.AjaxCallback;
import com.edusoho.kuozhi.core.model.Cache;
import com.edusoho.kuozhi.core.model.RequestUrl;

/**
 * Created by howzhi on 14-7-23.
 */
public interface AppCache {

    public <T> void cacheCallback(String url, Cache cache, AjaxCallback<T> ajaxCallback);

    public boolean hasCache(RequestUrl key);

    public Cache getCache(RequestUrl key);

    public void setCache(RequestUrl key, Object cache);

    public void delCaceh(RequestUrl key);

    public void clear();
}
