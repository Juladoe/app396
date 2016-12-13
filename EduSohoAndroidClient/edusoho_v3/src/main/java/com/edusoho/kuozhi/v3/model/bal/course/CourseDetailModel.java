package com.edusoho.kuozhi.v3.model.bal.course;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.coursedetail.CourseDetail;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.http.ModelDecor;
import com.edusoho.kuozhi.v3.model.base.ApiResponse;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.Api;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Zhang on 2016/12/13.
 */

public class CourseDetailModel implements Serializable {

    public static void getCourseDetail(String courseId,
            final ResponseCallbackListener<List<CourseDetail>>callbackListener){
        String url = String.format(Api.COURSE_GETCOURSE, courseId);
        RequestUrl requestUrl = EdusohoApp.app.bindUrl(url, true);
        EdusohoApp.app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ApiResponse<CourseDetail> apiResponse = ModelDecor.getInstance().
                        decor(response, new TypeToken<ApiResponse<CourseDetail>>() {
                });
                if (apiResponse.resources != null) {
                    callbackListener.onSuccess(apiResponse.resources);
                } else if (apiResponse.error != null) {
                    callbackListener.onFailure(apiResponse.error.code, apiResponse.error.message);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackListener.onFailure("Error", error.getMessage());
            }
        });
    }

}
