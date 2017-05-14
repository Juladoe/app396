package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.StudyCourse;
import com.edusoho.kuozhi.clean.bean.innerbean.Study;
import com.edusoho.kuozhi.v3.entity.lesson.TeachLesson;
import com.edusoho.kuozhi.v3.model.bal.thread.MyThreadEntity;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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

    @GET("me/vip_levels/{levelId}")
    Observable<JsonObject> isVip(@Path("levelId") int levelId);

    @GET("me/courses?relation=teaching")
    Observable<TeachLesson> getMyTeachCourse(@Query("start") int start, @Query("limit") int limit);

    @GET("me/courses")
    Observable<DataPageResult<StudyCourse>> getMyStudyCourse(@Query("offset") int offset, @Query("limit") int limit);

    @GET("me/live_course_sets")
    Observable<List<Study>> getMyStudyLiveCourseSet(@Query("offset") int offset, @Query("limit") int limit);

    @GET("me/classrooms")
    Observable<List<Study>> getMyStudyClassRoom(@Query("offset") int offset, @Query("limit") int limit);

    @GET("me/favorite_course_sets")
    Observable<DataPageResult<CourseSet>> getMyFavoriteCourseSet(@Query("offset") int offset, @Query("limit") int limit);

    @GET("chaos_threads/getThreads")
    Observable<MyThreadEntity[]> getMyAskThread(@Query("offset") int offset, @Query("limit") int limit);

    @GET("chaos_threads_posts/getThreadPosts")
    Observable<MyThreadEntity[]> getMyAnswerThread(@Query("offser") int offset, @Query("limit") int limit);

}
