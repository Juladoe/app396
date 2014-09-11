package com.edusoho.kuozhi.core.cache;

import android.content.Context;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.core.model.Cache;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.util.Const;


import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Pattern;

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
