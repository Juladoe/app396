package com.edusoho.kuozhi.v3.cache.request;


import com.edusoho.kuozhi.v3.cache.request.model.Request;

/**
 * Created by howzhi on 15/4/28.
 */
public class ESRequestManager extends RequestManager {

    public ESRequestManager()
    {
        super();
    }

    @Override
    public void get(Request request, RequestCallback callback) {
    }

    @Override
    public void post(Request request, RequestCallback callback) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public <T> T blockGet(Request request, RequestCallback<T> callback) {
        return null;
    }

    @Override
    public <T> T blocPost(Request request, RequestCallback<T> callback) {
        return null;
    }
}
