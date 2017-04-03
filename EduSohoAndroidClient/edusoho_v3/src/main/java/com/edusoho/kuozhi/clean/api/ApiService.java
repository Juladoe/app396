package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseReview;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.CourseSetMember;
import com.edusoho.kuozhi.clean.bean.CourseStudyPlan;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.v3.model.bal.Member;
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

    @GET("course_sets/{id}/reviews")
    Observable<CourseReview> getCourseReview(@Path("id") String id, @Query("limit") int limit, @Query("offset") int offset);

    @GET("course_sets/{course_setId}/courses")
    Observable<List<CourseStudyPlan>> getCourseStudyPlan(@Path("course_setId") String id);

    @GET("course_sets/{courseSetId}/members")
    Observable<CourseSetMember> getCourseSetMember(@Path("courseSetId") String id);

    @GET("plugins/vip/vip_levels")
    Observable<List<VipInfo>> getVipInfo();

    @GET("plugins/vip/vip_levels/{id}")
    Observable<VipLevel> getVipLevel(@Path("id") String id);

    @GET("/courses/{courseId}/members")
    Observable<DataPageResult<Member>> getCourseMembers(@Path("courseId") String courseId);

    @GET("course_sets/{course_setId}/courses")
    Observable<List<CourseProject>> getCourses(@Path("course_setId") String courseSetId);
}
