package com.edusoho.kuozhi.v3.cache.request;

/**
 * Created by howzhi on 15/4/28.
 */
public class RequestManagerFactory {

    public static RequestManager createDefaultManager()
    {
        ESRequestManager requestManager = new ESRequestManager();
        return requestManager;
    }
}
