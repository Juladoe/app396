package com.edusoho.kuozhi.v3.model.provider;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.listener.NormalCallback;

/**
 * Created by howzhi on 15/8/24.
 */
public abstract class ProviderListener<T> implements Response.Listener<T>, Response.ErrorListener {

    private NormalCallback<T> mCallabck;
    private NormalCallback mFailCallabck;

    @Override
    public void onErrorResponse(VolleyError error) {
        mFailCallabck.success(error);
    }

    @Override
    public void onResponse(T response) {
        mCallabck.success(response);
    }

    public ProviderListener fail(NormalCallback callback)
    {
        this.mFailCallabck = callback;
        return this;
    }

    public ProviderListener success(NormalCallback<T> callabck)
    {
        this.mCallabck = callabck;
        return this;
    }
}
