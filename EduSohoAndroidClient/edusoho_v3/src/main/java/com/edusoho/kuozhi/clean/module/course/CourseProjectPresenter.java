package com.edusoho.kuozhi.clean.module.course;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;


/**
 * Created by JesseHuang on 2017/3/23.
 */

public class CourseProjectPresenter implements CourseProjectContract.Presenter {

    private CourseProjectContract.View mView;
    private String mCourseProjectId;

    public CourseProjectPresenter(String courseProjectId, CourseProjectContract.View view) {
        mCourseProjectId = courseProjectId;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void learnTask(int taskId) {

    }

    @Override
    public void favorite(int taskId) {

    }

    @Override
    public void consult() {

    }

    @Override
    public void subscribe() {
        CourseProject courseProject = new CourseProject();
        mView.showFragments(initCourseModules(), courseProject);

//        getCourseProject(mCourseProjectId)
//                .flatMap(new Func1<CourseProject, Observable<CourseSet>>() {
//                    @Override
//                    public Observable<CourseSet> call(CourseProject courseProject) {
//                        mView.showFragments(initCourseModules(), courseProject);
//                        return getCourseSet(courseProject.courseSetId);
//                    }
//                })
//                .subscribe(new Subscriber<CourseSet>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(CourseSet courseSet) {
//                        mView.showTasksCover(courseSet.cover);
//                    }
//                });
    }

    @Override
    public void unsubscribe() {

    }

    private CourseProjectEnum[] initCourseModules() {
        return new CourseProjectEnum[]{CourseProjectEnum.INFO, CourseProjectEnum.TASKS, CourseProjectEnum.RATE};
    }

    private Observable<CourseProject> getCourseProject(int id) {
        return RetrofitService.getCourseProject(id);
    }

    private Observable<CourseSet> getCourseSet(String id) {
        return RetrofitService.getCourseSet(id);
    }
}
