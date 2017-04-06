package com.edusoho.kuozhi.clean.api;

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

    @GET("courses/{id}/tasks")
    Observable<List<CourseTask>> getTasks(@Path("id") String id);

    @GET("courses/{id}")
    Observable<CourseProject> getCourseProject(@Path("id") String id);

    @GET("plugins/vip/vip_levels/{id}")
    Observable<VipLevel> getVipLevel(@Path("id") String id);

    @GET("courses/{courseId}/members")
    Observable<DataPageResult<CourseMember>> getCourseMembers(@Path("courseId") String courseId, @Query("offset") int offset, @Query("limit") int limit);

    @GET("course_sets/{course_setId}/courses")
    Observable<List<CourseProject>> getCourseProjects(@Path("course_setId") String courseSetId);

    @GET("course_sets/{id}/reviews")
    Observable<DataPageResult<Review>> getCourseSetReviews(@Query("courseId") String courseId,
                                                           @Query("offset") int offset, @Query("limit") int limit);

    @GET("course_sets/{id}/reviews")
    Observable<DataPageResult<Review>> getCourseProjectReviews(@Path("id") String courseSetId, @Query("courseId") String courseId,
                                                               @Query("offset") int offset, @Query("limit") int limit);
}
