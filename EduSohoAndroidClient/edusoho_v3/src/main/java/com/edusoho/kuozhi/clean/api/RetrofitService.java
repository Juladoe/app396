package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.v3.model.bal.VipLevel;

import java.io.IOException;
import java.util.List;

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

    public static Observable<CourseSet> getCourseSet(String id) {
        return mApiService.getCourseSet(id);
    }

    public static Observable<VipLevel> getVipLevel(String id) {
        return mApiService.getVipLevel(id);
    }

    public static Observable<DataPageResult<CourseMember>> getCourseMembers(String courseId, int offset, int limit) {
        return mApiService.getCourseMembers(courseId, offset, limit);
    }

    public static Observable<List<CourseProject>> getCourseProjects(String courseSetId) {
        return mApiService.getCourseProjects(courseSetId);
    }

    public static Observable<DataPageResult<Review>> getCourseProjectReviews(String courseSetId, String courseId, int offset, int limit) {
        return mApiService.getCourseProjectReviews(courseSetId, courseId, offset, limit);
    }
}
