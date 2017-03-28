package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.google.gson.Gson;

import java.util.List;

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
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl("http://demo.edusoho.com/api/").build();
        mApiService = retrofit.create(ApiService.class);
    }

    public static Observable<List<CourseTask>> getTasks(int id) {
        return mApiService.getTasks(id);
    }

    public static Observable<CourseProject> getCourseProject(int id) {
        return mApiService.getCourseProject(id);
    }

    public static Observable<CourseSet> getCourseSet(String id) {
        return mApiService.getCourseSet(id);
    }
}
