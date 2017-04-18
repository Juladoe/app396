package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseReview;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.Member;
import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.clean.bean.TrailVideos;
import com.google.gson.JsonObject;

import java.util.List;

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

public interface CourseApi {

    @GET("courses/{id}/items")
    Observable<List<CourseItem>> getCourseItems(@Path("id") int courseId);

    @GET("courses/{id}")
    Observable<CourseProject> getCourseProject(@Path("id") int courseId);

    @GET("courses/{id}/trial_video")
    Observable<TrailVideos> getTrailVideos(@Path("id") int id);

    @GET("courses/{id}/members")
    Observable<DataPageResult<Member>> getCourseMembers(@Path("id") int courseId, @Query("role") String role,
                                                        @Query("offset") int offset, @Query("limit") int limit);

    @GET("courses/{courseId}/members/{userId}")
    Observable<Member> getCourseMember(@Path("courseId") int courseId, @Path("userId") int userId);

    @GET("courses/{courseId}/reviews")
    Observable<DataPageResult<Review>> getCourseProjectReviews(@Path("courseId") int courseId,
                                                               @Query("offset") int offset, @Query("limit") int limit);

    @FormUrlEncoded
    @POST("courses/{id}/members")
    Observable<JsonObject> joinFreeOrVipCourse(@Header("X-Auth-Token") String token,
                                               @Path("id") int courseId, @Field("joinWay") String joinWay);

}
