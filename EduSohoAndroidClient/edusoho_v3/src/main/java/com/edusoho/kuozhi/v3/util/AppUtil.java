package com.edusoho.kuozhi.v3.util;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.ResultCallback;
import com.edusoho.kuozhi.v3.listener.StatusCallback;
import com.edusoho.kuozhi.v3.model.sys.AppUpdateInfo;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

/**
 * Created by JesseHuang on 15/4/26.
 */
public class AppUtil {

    public static void checkUpateApp(
            final ActionBarBaseActivity activity, final StatusCallback<AppUpdateInfo> callback) {
        final EdusohoApp app = activity.app;
        RequestUrl requestUrl = app.bindUrl(Const.APP_UPDATE, false);
        String code = activity.getResources().getString(R.string.app_code);
        requestUrl.setParams(new String[]{
                "code", code
        });
        Log.d(null, "code->" + code);
        activity.ajaxPost(requestUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final AppUpdateInfo appUpdateInfo = activity.parseJsonValue(response.toString(), new TypeToken<AppUpdateInfo>() {
                });
                if (appUpdateInfo == null || appUpdateInfo.androidVersion == null) {
                    return;
                }

                String newVersion = appUpdateInfo.androidVersion;
                Log.d(null, "old version->" + app.getApkVersion());
                int result = CommonUtil.compareVersion(app.getApkVersion(), newVersion);
                if (result == Const.LOW_VERSIO) {
                    callback.success(appUpdateInfo);
                } else {
                    callback.error(appUpdateInfo);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}
