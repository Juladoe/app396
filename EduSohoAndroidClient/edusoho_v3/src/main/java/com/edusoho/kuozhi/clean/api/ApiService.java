package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.v3.model.bal.VipLevel;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by JesseHuang on 2017/3/23.
 */

public interface ApiService {

    @GET("course_sets/{id}")
    Observable<CourseSet> getCourseSet(@Path("id") String id);

    @GET("courses/{id}/items")
    Observable<List<CourseItem>> getCourseItems(@Path("id") String id);

    @GET("courses/{id}")
    Observable<CourseProject> getCourseProject(@Path("id") String id);

    @GET("plugins/vip/vip_levels/{id}")
    Observable<VipLevel> getVipLevel(@Path("id") String id);

    @GET("courses/{id}/members")
    Observable<DataPageResult<CourseMember>> getCourseMembers(@Path("id") String courseId, @Query("role") String role,
                                                              @Query("offset") int offset, @Query("limit") int limit);

    @GET("courses/{courseId}/members/{userId}")
    Observable<CourseMember> getCourseMember(@Path("courseId") String courseId, @Path("userId") String userId);

    @GET("course_sets/{id}/courses")
    Observable<List<CourseProject>> getCourseProjects(@Path("id") String courseSetId);

    @GET("course_sets/{id}/reviews")
    Observable<DataPageResult<Review>> getCourseSetReviews(@Query("courseId") String courseId,
                                                           @Query("offset") int offset, @Query("limit") int limit);

    @GET("courses/{courseId}/reviews")
    Observable<DataPageResult<Review>> getCourseProjectReviews(@Path("courseId") String courseId,
                                                               @Query("offset") int offset, @Query("limit") int limit);
}
