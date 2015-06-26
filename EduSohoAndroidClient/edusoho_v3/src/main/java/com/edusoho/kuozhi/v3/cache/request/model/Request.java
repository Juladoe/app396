package com.edusoho.kuozhi.v3.cache.request.model;

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
        URL requestURL = getRequestURL();
        return requestURL == null ? "" : getRequestURL().getPath();
    }

    public String getName()
    {
        String path = getPath();
        int lastDirPoint = path.lastIndexOf('/');
        if (lastDirPoint != -1) {
            return path.substring(lastDirPoint + 1);
        }

        return null;
    }

    public String getDir()
    {
        String path = getPath();
        int lastDirPoint = path.lastIndexOf('/');
        int firstDirPoint = path.indexOf('/');
        if (firstDirPoint == lastDirPoint) {
            return null;
        }
        return path.substring(0, lastDirPoint);
    }

    public String getHost()
    {
        URL requestURL = getRequestURL();
        return requestURL == null ? "" : getRequestURL().getHost();
    }

    private URL getRequestURL()
    {
        if (requestURL == null) {
            try {
                requestURL = new URL(url);
            } catch (Exception e) {
                e.printStackTrace();
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
