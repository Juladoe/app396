package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by JesseHuang on 2017/4/18.
 */

public interface UserApi {

    @GET("me/join_in_courses")
    Observable<List<CourseMember>> getMyJoinCourses(@Header("X-Auth-Token") String token, @Query("courseSetId") int courseSetId);

    @GET("me/course_learning_progress/{courseId}")
    Observable<CourseLearningProgress> getMyCourseLearningProgress(@Header("X-Auth-Token") String token, @Path("courseId") int courseId);

    @GET("me/favorite_course_sets/{courseSetId}")
    Observable<JsonObject> getFavorite(@Header("X-Auth-Token") String token, @Path("courseSetId") int courseSetId);

    @FormUrlEncoded
    @POST("me/favorite_course_sets")
    Observable<JsonObject> favoriteCourseSet(@Header("X-Auth-Token") String token, @Field("courseSetId") int courseSetId);

    @DELETE("me/favorite_course_sets/{courseSetId}")
    Observable<JsonObject> cancelFavoriteCourseSet(@Header("X-Auth-Token") String token, @Path("courseSetId") int courseSetId);

}
