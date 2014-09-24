package com.edusoho.kuozhi.core.model;

import java.util.HashMap;

/**
 * Created by howzhi on 14-9-11.
 */
public class RequestUrl {

    public String url;
    public HashMap<String, String> heads;
    public HashMap<String, String> params;
    public HashMap<String, Object> muiltParams;

    public RequestUrl() {
        heads = new HashMap<String, String>();
        params = new HashMap<String, String>();
        muiltParams = new HashMap<String, Object>();
    }

    public RequestUrl(String url) {
        this();
        this.url = url;
    }

    public void setParams(String[] values) {
        if (values == null || values.length == 0) {
            return;
        }

        for (int i = 0; i < values.length; i = i + 2) {
            params.put(values[i], values[i + 1]);
        }

        muiltParams.putAll(params);
    }

    public void setMuiltParams(Object[] values)
    {
        if (values == null || values.length == 0) {
            return;
        }
        for (int i = 0; i < values.length; i = i + 2) {
            muiltParams.put(values[i].toString(), values[i + 1]);
        }
    }

    public void setParams(HashMap<String, String> p) {
        params = p;
    }

    public void setHeads(String[] values) {
        if (values == null || values.length == 0) {
            return;
        }
        for (int i = 0; i < values.length; i = i + 2) {
            heads.put(values[i], values[i + 1]);
        }
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public HashMap<String, Object> getAllParams() {
        return muiltParams;
    }

    public HashMap<String, String> getHeads() {
        return heads;
    }
}
