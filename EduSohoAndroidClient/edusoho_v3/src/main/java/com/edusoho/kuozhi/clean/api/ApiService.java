package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseReview;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.CourseStudyPlan;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.Discount;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.v3.model.bal.VipLevel;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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

    @GET("course_sets/{id}/reviews")
    Observable<CourseReview> getCourseReview(@Path("id") String id, @Query("limit") int limit, @Query("offset") int offset);

    @GET("course_sets/{course_setId}/courses")
    Observable<List<CourseStudyPlan>> getCourseStudyPlan(@Path("course_setId") String id);

    @GET("course_sets/{courseSetId}/members")
    Observable<DataPageResult<com.edusoho.kuozhi.v3.model.bal.course.CourseMember>> getCourseSetMember(@Path("courseSetId") String id);

    @GET("plugins/vip/vip_levels")
    Observable<List<VipInfo>> getVipInfo();

    @GET("plugins/vip/vip_levels/{id}")
    Observable<VipLevel> getVipLevel(@Path("id") String id);

    @GET("plugins/discount/discounts/{discountId}")
    Observable<Discount> getDiscountInfo(@Path("discountId") int discountId);

    @GET("courses/{id}/members")
    Observable<DataPageResult<CourseMember>> getCourseMembers(@Path("id") String courseId, @Query("role") String role,
                                                              @Query("offset") int offset, @Query("limit") int limit);

    @GET("courses/{courseId}/members/{userId}")
    Observable<CourseMember> getCourseMember(@Path("courseId") String courseId, @Path("userId") String userId);

    @GET("course_sets/{id}/courses")
    Observable<List<CourseProject>> getCourseProjects(@Path("id") String courseSetId);

    @GET("users/{userId}/favorite_course_sets/{courseSetId}")
    Observable<JsonObject> getFavorite(@Path("userId") int userId, @Path("courseSetId") String courseId);

    @GET("courses/{courseId}/reviews")
    Observable<DataPageResult<Review>> getCourseProjectReviews(@Path("courseId") String courseId,
                                                               @Query("offset") int offset, @Query("limit") int limit);

    @FormUrlEncoded
    @POST("order_info")
    Observable<OrderInfo> postOrderInfo(@Header("X-Auth-Token") String token, @Field("targetType") String type, @Field("targetId") int id);

    @FormUrlEncoded
    @POST("orders")
    Observable<String> createOrder(@Header("X-Auth-Token") String token, @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("pay_center")
    Observable<String> goPay(@Field("orderId") int id, @Field("targetType") String type, @Field("payment") String payWay);

    @GET("me/cash_account")
    Observable<String> getMyVirtualCoin();
}
