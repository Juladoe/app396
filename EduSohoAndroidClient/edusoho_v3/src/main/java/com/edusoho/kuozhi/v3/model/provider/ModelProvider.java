package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.VolleySingleton;
import com.edusoho.kuozhi.v3.util.volley.BaseVolleyRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;


/**
 * Created by howzhi on 15/8/24.
 */
public abstract class ModelProvider {

    protected VolleySingleton mVolley;
    protected Gson mGson;
    private static final String TAG = "ModelProvider";

    public ModelProvider(Context context)
    {
        this.mGson = new Gson();
        this.mVolley = VolleySingleton.getInstance(context);
    }

    public static void init(Context context, Object target) {
        try {
            Field[] fields = target.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Class targetClass = field.getType();
                if (ModelProvider.class.isAssignableFrom(targetClass)) {
                    Object provider = ProviderFactory.getFactory().create(targetClass, context);
                    field.set(target, provider);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public <T> void addRequest(
            RequestUrl requestUrl, final TypeToken<T> typeToken, Response.Listener<T> responseListener, Response.ErrorListener errorListener) {
        mVolley.getRequestQueue();
        BaseVolleyRequest request = new BaseVolleyRequest(
                Request.Method.GET, requestUrl, responseListener, errorListener) {
            @Override
            protected T getResponseData(NetworkResponse response) {
                T value = null;
                try {
                    value = mGson.fromJson(
                            new String(response.data, "UTF-8"), typeToken.getType());
                } catch (Exception e) {
                }

                return value;
            }
        };

        request.setTag(requestUrl.url);
        mVolley.addToRequestQueue(request);
    }

    public <T> void addPostRequest(
            RequestUrl requestUrl, final TypeToken<T> typeToken, Response.Listener<T> responseListener, Response.ErrorListener errorListener) {
        mVolley.getRequestQueue();
        BaseVolleyRequest request = new BaseVolleyRequest(
                Request.Method.POST, requestUrl, responseListener, errorListener) {
            @Override
            protected T getResponseData(NetworkResponse response) {
                T value = null;
                try {
                    value = mGson.fromJson(
                            new String(response.data, "UTF-8"), typeToken.getType());
                } catch (Exception e) {
                }

                return value;
            }
        };

        request.setTag(requestUrl.url);
        mVolley.addToRequestQueue(request);
    }
}
