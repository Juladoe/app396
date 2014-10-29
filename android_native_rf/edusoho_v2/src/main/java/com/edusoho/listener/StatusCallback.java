package com.edusoho.listener;

/**
 * Created by howzhi on 14-10-12.
 */
public class StatusCallback<T> implements NormalCallback<T> {

    @Override
    public void success(T obj) {
    }

    public void error(T obj){}
}
