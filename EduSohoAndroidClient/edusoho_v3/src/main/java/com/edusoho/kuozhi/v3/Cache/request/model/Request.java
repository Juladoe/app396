package com.edusoho.kuozhi.v3.Cache.request.model;

import java.net.URL;
import java.util.HashMap;

/**
 * Created by howzhi on 15/4/28.
 */
public class Request {

    public HashMap<String, Object> params;
    public HashMap<String, Object> heads;
    public String url;

    private URL requestURL;

    public Request(String url)
    {
        this.url = url;
        initRequest();
    }

    public String getPath()
    {
        return getRequestURL().getPath();
    }

    private URL getRequestURL()
    {
        if (requestURL == null) {
            try {
                requestURL = new URL(url);
            } catch (Exception e) {
            }
        }

        return requestURL;
    }

    public void destory()
    {
        this.params.clear();
        this.heads.clear();
    }

    protected void initRequest()
    {
        this.params = new HashMap<>();
        this.heads = new HashMap<>();
    }
}
