package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.google.gson.reflect.TypeToken;

/**
 * Created by melomelon on 16/3/1.
 */
public class MyThreadProvider extends ModelProvider {

    public MyThreadProvider(Context context) {
        super(context);
    }

    public ProviderListener getMyCreatedThread(RequestUrl requestUrl) {
        //// TODO: 16/3/1 根据接口返回再更改类型
        ProviderListener<Object> providerListener = new ProviderListener() {
        };
        addRequest(requestUrl, new TypeToken<Object>() {
        }, providerListener, providerListener);
        return providerListener;
    }

    public ProviderListener getMyPostedThread(RequestUrl requestUrl) {
        //// TODO: 16/3/1 根据接口返回再更改类型
        ProviderListener<Object> providerListener = new ProviderListener() {
        };
        addRequest(requestUrl, new TypeToken<Object>() {
        }, providerListener, providerListener);
        return providerListener;
    }
}
