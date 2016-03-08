package com.edusoho.kuozhi.v3.model.bal.user;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.user.UserEntity;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.http.ModelDecor;
import com.edusoho.kuozhi.v3.model.base.ApiResponse;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.Api;
import com.google.gson.reflect.TypeToken;

/**
 * Created by JesseHuang on 16/3/8.
 */
public class UserModel {

    public void getUserInfoById(int id, final ResponseCallbackListener<UserEntity> callbackListener) {
        RequestUrl requestUrl = EdusohoApp.app.bindNewApiUrl(Api.USER, true);
        EdusohoApp.app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ApiResponse<UserEntity> apiResponse = ModelDecor.getInstance().decor(response, new TypeToken<ApiResponse<UserEntity>>() {
                });
                if (apiResponse.resources != null && apiResponse.resources.size() > 0) {
                    callbackListener.onSuccess(apiResponse.data);
                } else if (apiResponse.error != null) {
                    callbackListener.onFailure(apiResponse.error.name, apiResponse.error.message);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}
