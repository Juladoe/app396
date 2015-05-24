package com.edusoho.kuozhi.v3.Cache.request;


import com.edusoho.kuozhi.v3.Cache.request.model.Request;
import com.edusoho.kuozhi.v3.Cache.request.model.Response;

/**
 * Created by howzhi on 15/4/28.
 */
public interface RequestHandler {

    public void handler(Request request, Response response);
}
