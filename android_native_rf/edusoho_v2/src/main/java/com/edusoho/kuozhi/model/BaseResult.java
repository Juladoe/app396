package com.edusoho.kuozhi.model;

/**
 * Created by howzhi on 14-9-17.
 */
public class BaseResult<T> {
    public int start;
    public int total;
    public int limit;
    public T data;
}
