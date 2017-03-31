package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.CourseTask;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by JesseHuang on 2017/3/23.
 */

public interface ApiService {

    @GET("/courses/{id}/tasks")
    Observable<List<CourseTask>> getTasks(@Path("id") String id);

    @GET("/courses/{id}")
    Observable<CourseProject> getCourseProject(@Path("id") String id);

    @GET("/course_sets/{id}")
    Observable<CourseSet> getCourseSet(@Path("id") String id);

}
