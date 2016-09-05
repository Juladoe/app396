package com.edusoho.kuozhi.v3.model.bal.note;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.note.Note;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.http.ModelDecor;
import com.edusoho.kuozhi.v3.model.base.ApiResponse;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

/**
 * Created by JesseHuang on 16/5/9.
 */
public class NoteModel {

    public void getNote(int courseId, int lessonId, int userId, final ResponseCallbackListener<List<Note>> callbackListener) {
        String url = String.format(Const.GET_LESSON_NOTE, courseId) + "?lessonId=" + lessonId;
        RequestUrl requestUrl = EdusohoApp.app.bindNewApiUrl(url, true);
        EdusohoApp.app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ApiResponse<Note> apiResponse = ModelDecor.getInstance().decor(response, new TypeToken<ApiResponse<Note>>() {
                });
                if (apiResponse.resources != null) {
                    callbackListener.onSuccess(apiResponse.resources);
                } else {
                    callbackListener.onFailure(apiResponse.error.code, apiResponse.error.message);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public void postNote(int courseId, int lessonId, int status, String content, final ResponseCallbackListener<Note> callbackListener) {
        RequestUrl requestUrl = EdusohoApp.app.bindNewApiUrl(String.format(Const.LESSON_NOTE, courseId), true);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("lessonId", lessonId + "");
        params.put("status", status + "");
        params.put("content", content);
        EdusohoApp.app.postUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Note apiResponse = ModelDecor.getInstance().decor(response, new TypeToken<Note>() {
                });
                if (apiResponse != null) {
                    callbackListener.onSuccess(apiResponse);
                } else {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

}
