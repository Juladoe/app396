package com.edusoho.kuozhi.core.model;

/**
 * Created by howzhi on 14-7-23.
 */
public class Cache {

    public String value;
    public String key;

    public Cache(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String get()
    {
        return value;
    }
}
