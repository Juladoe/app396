package com.edusoho.kuozhi.v3.cache.request;


import com.edusoho.kuozhi.v3.cache.request.model.Request;
import com.edusoho.kuozhi.v3.cache.request.model.Response;

import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Pattern;

/**
 * Created by howzhi on 15/4/28.
 */
public abstract class RequestManager {

    private static RequestManager mRequestManager;

    private HashMap<String, RequestHandler> mRequestHandlerList;

    protected ScheduledThreadPoolExecutor mWorkExecutor;

    public RequestManager() {
        mRequestHandlerList = new HashMap<>();
        mWorkExecutor = new ScheduledThreadPoolExecutor(3);
    }

    public static RequestManager getDefaultManager() {
        synchronized (RequestManager.class) {
            if (mRequestManager == null) {
                mRequestManager = RequestManagerFactory.createDefaultManager();
            }
        }
        return mRequestManager;
    }

    public abstract void destroy();

    public abstract void get(Request request, RequestCallback callback);

    public abstract <T> T blockGet(Request request, RequestCallback<T> callback);

    public abstract <T> T blocPost(Request request, RequestCallback<T> callback);

    public abstract void post(Request request, RequestCallback callback);

    public void registHandler(String pattern, RequestHandler handler) {
        mRequestHandlerList.put(pattern, handler);
    }

    protected void handleRequest(Request request, Response response) {
        for (String filter : mRequestHandlerList.keySet()) {
            if (Pattern.matches(filter, request.url)) {
                mRequestHandlerList.get(filter).handler(request, response);
            }
        }
    }
}
