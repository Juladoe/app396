package com.edusoho.kuozhi.core.model;

import java.util.HashMap;

/**
 * Created by howzhi on 14-9-11.
 */
public class RequestUrl {

    public String url;
    public HashMap<String, String> heads;
    public HashMap<String, String> params;

    public RequestUrl(){
        heads = new HashMap<String, String>();
        params = new HashMap<String, String>();
    }

    public RequestUrl(String url)
    {
        this();
        this.url = url;
    }

    public void setParams(String[] values)
    {
        if (values == null || values.length == 0) {
            return;
        }
        for (int i=0; i < values.length; i = i + 2) {
            params.put(values[i], values[i+1]);
        }
    }

    public void setHeads(String[] values)
    {
        if (values == null || values.length == 0) {
            return;
        }
        for (int i=0; i < values.length; i = i + 2) {
            heads.put(values[i], values[i+1]);
        }
    }

    public HashMap<String, String> getParams()
    {
        return params;
    }

    public HashMap<String, String> getHeads()
    {
        return heads;
    }
}
