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
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

import static com.edusoho.kuozhi.R.color.error;

/**
 * Created by Zhang on 2016/12/13.
 */

public class CourseDetailModel implements Serializable {

    public static void getCourseDetail(String courseId,
            final ResponseCallbackListener<CourseDetail>callbackListener){
        String url = String.format(Const.COURSE_GETCOURSE, courseId);
        RequestUrl requestUrl = EdusohoApp.app.bindUrl(url, true);
        EdusohoApp.app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("课程不存在")){
                    callbackListener.onFailure("Error", response);
                    return;
                }
                CourseDetail apiResponse = ModelDecor.getInstance().
                        decor(response, new TypeToken<CourseDetail>() {
                });
                if (apiResponse != null) {
                    callbackListener.onSuccess(apiResponse);
                } else if (apiResponse != null) {
                    callbackListener.onFailure("Error", response);
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
