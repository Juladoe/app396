package com.edusoho.kuozhi.clean.module.course;

import android.util.Log;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.utils.CommonConstant;
import com.edusoho.kuozhi.clean.utils.TimeUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;


/**
 * Created by JesseHuang on 2017/3/23.
 */

public class CourseProjectPresenter implements CourseProjectContract.Presenter {

    private CourseProjectContract.View mView;
    private String mCourseProjectId;
    private CourseProject.Teacher mTeacher;

    public CourseProjectPresenter(String courseProjectId, CourseProjectContract.View view) {
        mCourseProjectId = courseProjectId;
        mView = view;
    }

    @Override
    public void consult() {
        mView.launchImChatWithTeacher(mTeacher);
    }

    @Override
    public void subscribe() {
        getCourseProject(mCourseProjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CourseProject>() {
                    @Override
                    public void call(CourseProject courseProject) {
                        if (courseProject.teachers.length > 0) {
                            mTeacher = courseProject.teachers[0];
                        }
                        mView.showFragments(initCourseModules(), courseProject);
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<CourseProject, Observable<CourseSet>>() {
                    @Override
                    public Observable<CourseSet> call(CourseProject courseProject) {
                        return getCourseSet(courseProject.courseSetId);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseSet>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CourseSet courseSet) {
                        mView.showCover(courseSet.cover.large);
                    }
                });

        getCourseMember(mCourseProjectId, "3")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseMember>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CourseMember courseMember) {
                        mView.showBottomLayout(!(courseMember.user != null && !isExpired(courseMember.deadline)));
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

    private boolean isExpired(String utcTime) {
        return !CommonConstant.EXPIRED_MODE_FOREVER.equals(utcTime) && TimeUtils.getUTCtoDate(utcTime).compareTo(new Date()) < 0;
    }

    private CourseProjectEnum[] initCourseModules() {
        return new CourseProjectEnum[]{CourseProjectEnum.INFO, CourseProjectEnum.TASKS, CourseProjectEnum.RATE};
    }

    private Observable<CourseProject> getCourseProject(String id) {
        return RetrofitService.getCourseProject(id);
    }

    private Observable<CourseMember> getCourseMember(String courseId, String userId) {
        return RetrofitService.getCourseMember(courseId, userId);
    }

    private Observable<CourseSet> getCourseSet(String id) {
        return RetrofitService.getCourseSet(id);
    }
}
