package com.edusoho.kuozhi.v3.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.ResultCallback;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.VolleySingleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by JesseHuang on 15/5/6.
 * 一般用于NoActionBar的theme
 */
public class BaseActivity extends ActionBarActivity {
    public static final String TAG = "ActionBarBaseActivity";
    public static final String BACK = "返回";
    protected BaseActivity mActivity;
    public Gson gson;
    protected Context mContext;
    public EdusohoApp app;
    public ActionBar mActionBar;
    protected FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this;
        initActivity();
    }

    private void initActivity() {
        app = (EdusohoApp) getApplication();
        mActionBar = getSupportActionBar();
        mFragmentManager = getSupportFragmentManager();
        app.setDisplay(this);
        gson = app.gson;
    }

    public void ajaxPost(final RequestUrl requestUrl, ResultCallback resultCallback) {
        VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return requestUrl.getParams();
            }
        };
        stringRequest.setTag(requestUrl.url);

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void ajaxPost(final RequestUrl requestUrl, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        app.postUrl(requestUrl, responseListener, errorListener);
    }

    public void ajaxGet(final String url, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        app.getUrl(url, responseListener, errorListener);
    }

    public void ajaxGet(final RequestUrl requestUrl, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        app.getUrl(requestUrl, responseListener, errorListener);
    }

    public void runService(String packageName) {

    }

    public <T> T parseJsonValue(String json, TypeToken<T> typeToken) {
        T value = null;
        try {
            value = mActivity.gson.fromJson(
                    json, typeToken.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        }

        return value;
    }
}
