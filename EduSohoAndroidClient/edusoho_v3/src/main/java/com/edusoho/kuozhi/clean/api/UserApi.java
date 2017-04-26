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
    Observable<List<CourseMember>> getMyJoinCourses(@Query("courseSetId") int courseSetId);

    @GET("me/course_learning_progress/{courseId}")
    Observable<CourseLearningProgress> getMyCourseLearningProgress(@Path("courseId") int courseId);

    @GET("me/favorite_course_sets/{courseSetId}")
    Observable<JsonObject> getFavorite(@Path("courseSetId") int courseSetId);

    @GET("me/course_members/{courseId}")
    Observable<CourseMember> getCourseMember(@Path("courseId") int courseId);

    @FormUrlEncoded
    @POST("me/favorite_course_sets")
    Observable<JsonObject> favoriteCourseSet(@Field("courseSetId") int courseSetId);

    @DELETE("me/favorite_course_sets/{courseSetId}")
    Observable<JsonObject> cancelFavoriteCourseSet(@Path("courseSetId") int courseSetId);

    @DELETE("me/course_members/{courseId}")
    Observable<JsonObject> exitCourse(@Path("courseId") int courseId);

}
