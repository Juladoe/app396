package com.edusoho.kuozhi.v3.model.sys;

import java.util.HashMap;
import java.util.IdentityHashMap;

/**
 * Created by howzhi on 14-9-11.
 */
public class RequestUrl {

    public String url;
    public HashMap<String, String> heads;
    public HashMap<String, String> params;
    public HashMap<String, Object> muiltParams;

    public IdentityHashMap<String, Object> muiltKeysMap;

    public RequestUrl() {
        heads = new HashMap<>();
        params = new HashMap<>();
        muiltParams = new HashMap<>();
        initHeads();
    }

    private void initHeads() {
        heads.put("User-Agent", "Android");
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
    }

    public String setGetParams(String[] values) {
        if(values == null || values.length == 0){
            return url;
        }

        StringBuffer sb = new StringBuffer(url);
        for (int i = 0;i<values.length;i += 2){
            if(i==0){
                sb.deleteCharAt(sb.length()-1);
                sb.append("?"+values[i]+"="+values[i+1]);
            }else {
                sb.append("&"+values[i]+"="+values[i+1]);
            }
        }
        return sb.toString();
    }

    public void setMuiltParams(Object[] values) {
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
        muiltParams.putAll(params);
        return muiltParams;
    }

    public HashMap<String, String> getHeads() {
        return heads;
    }

    public IdentityHashMap<String, Object> initKeysMap() {
        muiltKeysMap = new IdentityHashMap<>();
        return muiltKeysMap;
    }

    public IdentityHashMap<String, Object> getKeysMap() {
        return muiltKeysMap;
    }
}
