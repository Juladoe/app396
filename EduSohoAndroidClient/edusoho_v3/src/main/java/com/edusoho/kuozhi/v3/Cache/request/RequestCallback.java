package com.edusoho.kuozhi.v3.Cache.request;

import com.edusoho.kuozhi.v3.Cache.request.model.Response;

/**
 * Created by howzhi on 15/4/28.
 */
public interface RequestCallback<T> {

    public T onResponse(Response<T> response);
}
