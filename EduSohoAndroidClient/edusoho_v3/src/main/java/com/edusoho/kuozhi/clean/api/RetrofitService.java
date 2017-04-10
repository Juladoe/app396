package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseReview;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.CourseStudyPlan;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.Discount;
import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.v3.model.bal.VipLevel;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;


/**
 * Created by JesseHuang on 2017/3/23.
 */

public class RetrofitService {

    private static ApiService mApiService;

    private RetrofitService() {

    }

    public static void init(String host) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().
                        addHeader("Accept", "application/vnd.edusoho.v2+json").
                        build();
                return chain.proceed(request);
            }
        }).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl("http://devtest.edusoho.cn:82/api/")
                .build();
        mApiService = retrofit.create(ApiService.class);
    }

    public static Observable<List<CourseItem>> getTasks(String id) {
        return mApiService.getCourseItems(id);
    }

    public static Observable<CourseProject> getCourseProject(String id) {
        return mApiService.getCourseProject(id);
    }

    public static Observable<CourseSet> getCourseSet(String courseSetId) {
        return mApiService.getCourseSet(courseSetId);
    }

    public static Observable<CourseReview> getCourseReview(String courseSetId, int limit, int offset) {
        return mApiService.getCourseReview(courseSetId, limit, offset);
    }

    public static Observable<List<CourseStudyPlan>> getCourseStudyPlan(String courseSetId){
        return mApiService.getCourseStudyPlan(courseSetId);
    }

    public static Observable<List<VipInfo>> getVipInfo() {
        return mApiService.getVipInfo();
    }

    public static Observable<VipLevel> getVipLevel(String id) {
        return mApiService.getVipLevel(id);
    }

    public static Observable<DataPageResult<CourseMember>> getCourseMembers(String courseId, String role, int offset, int limit) {
        return mApiService.getCourseMembers(courseId, role, offset, limit);
    }

    public static Observable<CourseMember> getCourseMember(String courseId, String userId) {
        return mApiService.getCourseMember(courseId, userId);
    }

    public static Observable<List<CourseProject>> getCourseProjects(String courseSetId) {
        return mApiService.getCourseProjects(courseSetId);
    }

    public static Observable<DataPageResult<com.edusoho.kuozhi.v3.model.bal.course.CourseMember>> getCourseSetMember(String courseSetId) {
        return mApiService.getCourseSetMember(courseSetId);
    }

    public static Observable<JsonObject> getFavorite(int userId, String courseId) {
        return mApiService.getFavorite(userId, courseId);
    }

    public static Observable<DataPageResult<Review>> getCourseProjectReviews(String courseId, int offset, int limit) {
        return mApiService.getCourseProjectReviews(courseId, offset, limit);
    }

    public static Observable<Discount> getDiscountInfo(int discountId){
        return mApiService.getDiscountInfo(discountId);
    }

    public static Observable<String> postOrderInfo(String type, int id){
        return mApiService.postOrderInfo(type, id);
    }

    public static Observable<String> createOrder(Map<String, String> map){
        return mApiService.createOrder(map);
    }

    public static Observable<String> goPay(int id, String type, String payWay){
        return mApiService.goPay(id, type, payWay);
    }

    public static Observable<String> getMyCoupons(){
        return mApiService.getMyCoupons();
    }

    public static Observable<String> getMyVirtualCoin(){
        return mApiService.getMyVirtualCoin();
    }
}
