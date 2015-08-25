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
    private T mResponse;

    @Override
    public void onErrorResponse(VolleyError error) {
        if (mFailCallabck != null) {
            mFailCallabck.success(error);
        }
    }

    @Override
    public void onResponse(T response) {
        if (mCallabck == null) {
            mResponse = response;
            return;
        }
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
        if (mResponse != null) {
            this.mCallabck.success(mResponse);
            mResponse = null;
        }
        return this;
    }
}
