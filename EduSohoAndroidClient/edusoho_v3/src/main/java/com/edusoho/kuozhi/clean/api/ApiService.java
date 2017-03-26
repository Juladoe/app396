package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseTask;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by JesseHuang on 2017/3/23.
 */

public interface ApiService {

    @GET("/courses/{id}/tasks")
    Observable<List<CourseTask>> getTasks(@Path("id") int id);
}
