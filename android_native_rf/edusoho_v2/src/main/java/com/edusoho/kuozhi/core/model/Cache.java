package com.edusoho.kuozhi.core.model;

/**
 * Created by howzhi on 14-7-23.
 */
public class Cache {

    public Object data;

    public Cache(Object data)
    {
        this.data = data;
    }

    public <T> T get()
    {
        return (T)data;
    }
}
