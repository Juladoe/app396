package com.edusoho.kuozhi.clean.api;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by DF on 2017/5/4.
 */

public interface CommonApi {

    @GET("setting/course")
    Observable<JsonObject> getCourseSet();
}
