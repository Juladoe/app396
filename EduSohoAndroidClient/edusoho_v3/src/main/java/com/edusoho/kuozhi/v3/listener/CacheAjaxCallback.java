package com.edusoho.kuozhi.v3.listener;

import com.androidquery.callback.AjaxCallback;

/**
 * Created by howzhi on 14-9-12.
 */
public class CacheAjaxCallback<T> extends AjaxCallback<T> {

    private boolean cacheRequest;

    public CacheAjaxCallback() {
        super();
    }

    public void setCacheRequest(boolean cacheRequest) {
        this.cacheRequest = cacheRequest;
    }

    public boolean isCacheRequest() {
        return cacheRequest;
    }

}
