package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.google.gson.Gson;

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
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Accept", "application/vnd.edusoho.v2+json").build();
                return chain.proceed(request);
            }
        });
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .baseUrl("http://es.devtest1/api/")
                .build();
        mApiService = retrofit.create(ApiService.class);

    }

    public static Observable<List<CourseTask>> getTasks(String id) {
        return mApiService.getTasks(id);
    }

    public static Observable<CourseProject> getCourseProject(String id) {
        return mApiService.getCourseProject(id);
    }

    public static Observable<CourseSet> getCourseSet(String id) {
        return mApiService.getCourseSet(id);
    }
}
