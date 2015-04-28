package com.edusoho.kuozhi.v3.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.ResultCallback;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.VolleySingleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by JesseHuang on 15/4/23.
 */
public class ActionBarBaseActivity extends ActionBarActivity {

    public static final String TAG = "ActionBarBaseActivity";
    public static final String BACK = "返回";
    protected ActionBarBaseActivity mActivity;
    public Gson gson;
    protected Context mContext;
    public EdusohoApp app;
    public ActionBar mActionBar;
    protected FragmentManager mFragmentManager;
    protected TextView mTitleTextView;
    private View mTitleLayoutView;

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

    public void setBackMode(String backTitle, String title) {
        mTitleLayoutView = getLayoutInflater().inflate(R.layout.actionbar_custom_title, null);
        mTitleTextView = (TextView) mTitleLayoutView.findViewById(R.id.action_bar_title);
        mTitleTextView.setText(title);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        //layoutParams.width = (int) (EdusohoApp.screenW * 0.6);
        layoutParams.gravity = Gravity.CENTER;
        mActionBar.setCustomView(mTitleLayoutView, layoutParams);

        if (backTitle != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
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
