//package com.edusoho.kuozhi.v3.model.bal.course;
//
//import android.text.TextUtils;
//
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.edusoho.kuozhi.v3.EdusohoApp;
//import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
//import com.edusoho.kuozhi.v3.entity.note.Note;
//import com.edusoho.kuozhi.v3.model.bal.http.ModelDecor;
//import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
//import com.edusoho.kuozhi.v3.util.Api;
//import com.google.gson.reflect.TypeToken;
//
//import java.io.Serializable;
//
///**
// * Created by Zhang on 2016/12/13.
// */
//
//public class CourseDetailModel implements Serializable {
//
//    public static CourseDetail getCourseDetail(String courseId){
//        String url = String.format(Api.COURSE_GETCOURSE, courseId);
//        RequestUrl requestUrl = EdusohoApp.app.bindUrl(url, true);
//        EdusohoApp.app.getUrl(requestUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                if (TextUtils.isEmpty(response) || "[]".equals(response)) {
//
//                    return;
//                }
//                Note note = ModelDecor.getInstance().decor(response, new TypeToken<Note>() {
//                });
//                if (note!= null) {
//                    callbackListener.onSuccess(note);
//                } else {
//                    callbackListener.onFailure("500", "get note error");
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//    }
//
//}
