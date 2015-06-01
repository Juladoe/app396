package com.edusoho.kuozhi.v3.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Window;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.sys.Meta;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.VolleySingleton;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
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

    public void hideActionBar() {
        mActionBar.hide();
    }

    public void showActionBar() {
        mActionBar.show();
    }

    public void ajaxPost(final RequestUrl requestUrl) {
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

    public void ajaxPostWithLoading(final RequestUrl requestUrl, final Response.Listener<String> responseListener, final Response.ErrorListener errorListener, String loadingText) {
        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        if (!TextUtils.isEmpty(loadingText)) {
            loadDialog.setMessage(loadingText);
        }
        loadDialog.show();
        app.postUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    loadDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response.toString());
                    boolean result = handleRequest(jsonObject.getString("meta"));
                    if (result) {
                        responseListener.onResponse(jsonObject.getString("data"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadDialog.dismiss();
                if (error.networkResponse == null) {
                    CommonUtil.longToast(mActivity, "无网络连接或请求失败");
                } else {
                    if (errorListener != null) {
                        errorListener.onErrorResponse(error);
                    } else {
                        CommonUtil.longToast(mContext, getResources().getString(R.string.request_fail_text));
                    }
                }
            }
        });
    }

    public void ajaxPost(final RequestUrl requestUrl, final Response.Listener<String> responseListener, final Response.ErrorListener errorListener) {
        app.postUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    boolean result = handleRequest(jsonObject.getString("meta"));
                    if (result) {
                        responseListener.onResponse(jsonObject.getString("data"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    CommonUtil.longToast(mActivity, "无网络连接或请求失败");
                } else {
                    if (errorListener != null) {
                        errorListener.onErrorResponse(error);
                    } else {
                        CommonUtil.longToast(mContext, getResources().getString(R.string.request_fail_text));
                    }
                }
            }
        });
    }

    public void ajaxGet(final String url, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        app.getUrl(url, responseListener, errorListener);
    }

    public void ajaxGet(final RequestUrl requestUrl, final Response.Listener<String> responseListener, final Response.ErrorListener errorListener) {
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    boolean result = handleRequest(jsonObject.getString("meta"));
                    if (result) {
                        responseListener.onResponse(jsonObject.getString("data"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    CommonUtil.longToast(mActivity, "无网络连接或请求失败");
                } else {
                    if (errorListener != null) {
                        errorListener.onErrorResponse(error);
                    } else {
                        CommonUtil.longToast(mContext, getResources().getString(R.string.request_fail_text));
                    }
                }
            }
        });
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

    private boolean handleRequest(String meta) throws JSONException {

        Meta metaResult = parseJsonValue(meta, new TypeToken<Meta>() {
        });
        CommonUtil.longToast(mActivity, metaResult.message);
        return metaResult.code == Const.OK;
    }
}
