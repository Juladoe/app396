package com.edusoho.kuozhi.clean.module.course;

import android.util.Log;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.Member;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.utils.CommonConstant;
import com.edusoho.kuozhi.clean.utils.TimeUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by JesseHuang on 2017/3/23.
 */

public class CourseProjectPresenter implements CourseProjectContract.Presenter {

    private CourseProjectContract.View mView;
    private int mCourseProjectId;
    private CourseProject.Teacher mTeacher;
    private CourseLearningProgress mProgress;
    private Member mMember;

    public CourseProjectPresenter(int courseProjectId, CourseProjectContract.View view) {
        mCourseProjectId = courseProjectId;
        mView = view;
    }

    @Override
    public void consult() {
        //这里需要修改，应该要和RxJava结合使用
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
                        //这么处理不是很好，会有一个跳转
                        initCourseMemberInfo(courseProject);
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


    }

    @Override
    public void joinCourseProject(int courseId) {
        mView.initJoinCourseLayout();
        setCourseLearningProgress(courseId);
    }

    @Override
    public void showCourseProgressInfo() {
        mView.launchDialogProgress(mProgress, mMember);
    }

    @Override
    public void unsubscribe() {

    }

    private void initCourseMemberInfo(final CourseProject courseProject) {
        getCourseMember(courseProject.id, EdusohoApp.app.loginUser.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Member>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Member member) {
                        boolean isLearning = member.user != null && !isExpired(member.deadline);
                        mView.showBottomLayout(!isLearning);
                        mView.showCacheButton(isLearning);
                        mView.showShareButton(!isLearning);
                        mView.showFragments(initCourseModules(isLearning), courseProject);
                        if (isLearning) {
                            mMember = member;
                            mView.initLearnedLayout();
                            setCourseLearningProgress(courseProject.id);
                        }
                    }
                });
    }

    private void setCourseLearningProgress(int courseId) {
        RetrofitService.getMyCourseLearningProgress(EdusohoApp.app.token, courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseLearningProgress>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CourseLearningProgress courseLearningProgress) {
                        mProgress = courseLearningProgress;
                        mView.setProgressBar(courseLearningProgress.progress);
                    }
                });
    }

    private boolean isExpired(String utcTime) {
        return !CommonConstant.EXPIRED_MODE_FOREVER.equals(utcTime) && TimeUtils.getUTCtoDate(utcTime).compareTo(new Date()) < 0;
    }

    private List<CourseProjectEnum> initCourseModules(boolean isLearning) {
        List<CourseProjectEnum> list = new ArrayList<>();
        if (isLearning) {
            list.add(CourseProjectEnum.TASKS);
        } else {
            list.add(CourseProjectEnum.INFO);
            list.add(CourseProjectEnum.TASKS);
            list.add(CourseProjectEnum.RATE);
        }
        return list;
    }

    private Observable<CourseProject> getCourseProject(int id) {
        return RetrofitService.getCourseProject(id);
    }

    private Observable<Member> getCourseMember(int courseId, int userId) {
        return RetrofitService.getCourseMember(courseId, userId);
    }

    private Observable<CourseSet> getCourseSet(int id) {
        return RetrofitService.getCourseSet(id);
    }
}
