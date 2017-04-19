package com.edusoho.kuozhi.clean.module.course;

import android.app.AlertDialog;

import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.api.CourseSetApi;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.Member;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.utils.CommonConstant;
import com.edusoho.kuozhi.clean.utils.TimeUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.google.gson.JsonObject;

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

    private static final String IS_JOIN_SUCCESS = "success";
    private static final float FREE_PRICE = 0f;
    private CourseProjectContract.View mView;
    private int mCourseProjectId;
    private CourseProject.Teacher mTeacher;
    private CourseLearningProgress mProgress;
    private Member mMember;
    private CourseProject mCourseProject;

    public CourseProjectPresenter(int courseProjectId, CourseProjectContract.View view) {
        mCourseProjectId = courseProjectId;
        mView = view;
        mCourseProjectId = 42;
    }

    @Override
    public void consult() {
        //这里需要修改，应该要和RxJava结合使用
        mView.launchImChatWithTeacher(mTeacher);
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .createApi(CourseApi.class)
                .getCourseProject(mCourseProjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CourseProject>() {
                    @Override
                    public void call(CourseProject courseProject) {
                        mCourseProject = courseProject;
                        if (courseProject.teachers.length > 0) {
                            mTeacher = courseProject.teachers[0];
                        }
                        //这么处理不是很好，会有一个跳转
                        //如果过期底部加入按钮变色
//                        if (isCourseDoNotStarted(courseProject.expiryStartDate)) {
//                            mView.setJoinButton(false);
//                            mView.showFragments(initCourseModules(false), courseProject);
//                        } else {
//                            initCourseMemberInfo(courseProject);
//                        }
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
    public void joinCourseProject(final int courseId) {
        if (mCourseProject.originPrice == FREE_PRICE) {
            joinFreeOrVipCourse(courseId, "free");
        } else if (EdusohoApp.app.loginUser.vip != null && EdusohoApp.app.loginUser.vip.levelId >= mCourseProject.vipLevelId) {
            joinFreeOrVipCourse(courseId, "vip");
        } else {
            mView.launchConfirmOrderActivity(mCourseProjectId, courseId);
        }
    }

    @Override
    public void showCourseProgressInfo() {
        mView.launchDialogProgress(mProgress, mMember);
    }

    @Override
    public void unsubscribe() {

    }

    private void joinFreeOrVipCourse(final int courseId, String joinWay) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .joinFreeOrVipCourse(courseId, joinWay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (jsonObject.get(IS_JOIN_SUCCESS).getAsBoolean()) {
                            mView.initJoinCourseLayout();
                            setCourseLearningProgress(courseId);
                        }
                    }
                });
    }

    private void initCourseMemberInfo(final CourseProject courseProject) {
        if (EdusohoApp.app.loginUser != null) {
            HttpUtils.getInstance()
                    .createApi(CourseApi.class)
                    .getCourseMember(courseProject.id, EdusohoApp.app.loginUser.id)
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
                            if (member.user != null) {
                                if (isCourseExpired(courseProject.expiryEndDate)) {
                                    //课程本身已过期弹出框
                                } else if (isCourseMemberExpired(member.deadline)) {
                                    //退出弹出框
                                } else if (isCourseDoNotStarted(courseProject.expiryStartDate)) {
                                    //点击任务，弹出课程还没开始
                                }
                            }
                            if (CourseProject.CourseStatus.valueOf(courseProject.status) == CourseProject.CourseStatus.DRAFT ||
                                    CourseProject.CourseStatus.valueOf(courseProject.status) == CourseProject.CourseStatus.CLOSED) {

                            }
                        }
                    });
        } else {
            mView.showFragments(initCourseModules(false), courseProject);
        }
    }

    //
//                                if (isDraft(courseProject)) {
//                                    mView.setJoinButton(false);
//                                    return;
//                                }
//                                //mView.setJoinButton(!isCourseStarted(courseProject.expiryStartDate));
//                                if (isExpired(member.deadline)) {
//                                    // TODO: 2017/4/19 弹出框提示已经过期，是否退出
//                                } else {
//                                    mView.showBottomLayout(false);
//                                    mView.showCacheButton(getCourseLearnMode(courseProject) == CourseProject.CourseLearnMode.FREE);
//                                    mView.showShareButton(false);
//                                    mView.showFragments(initCourseModules(true), courseProject);
//                                }
//                            } else {
//                                mView.showFragments(initCourseModules(false), courseProject);
//                            }

    //是否已经加入
//                            boolean isLearning = member.user != null && !isExpired(member.deadline);
//                            mView.showBottomLayout(!isLearning);
//                            mView.showCacheButton(isLearning);
//                            mView.showShareButton(!isLearning);
//                            mView.showFragments(initCourseModules(isLearning), courseProject);
//                            if (isLearning) {
//                                mMember = member;
//                                mView.initLearnedLayout();
//                                setCourseLearningProgress(courseProject.id);
//                            }

    private boolean isCourseExpired(String expiryEndDate) {
        return TimeUtils.getUTCtoDate(expiryEndDate).compareTo(new Date()) < 1;
    }

    private CourseProject.CourseLearnMode getCourseLearnMode(String learnMode) {
        return CourseProject.CourseLearnMode.valueOf(learnMode);
    }

    private boolean isDraft(String courseStatus) {
        return CourseProject.CourseStatus.valueOf(courseStatus) == CourseProject.CourseStatus.DRAFT;
    }

    private boolean isClosed(String courseStatus) {
        return CourseProject.CourseStatus.valueOf(courseStatus) == CourseProject.CourseStatus.CLOSED;
    }

    private boolean isCourseDoNotStarted(String expiryStartDate) {
        return TimeUtils.getUTCtoDate(expiryStartDate).compareTo(new Date()) > 0;
    }

    private boolean isCourseMemberExpired(String deadline) {
        return !CommonConstant.EXPIRED_MODE_FOREVER.equals(deadline) && TimeUtils.getUTCtoDate(deadline).compareTo(new Date()) < 0;
    }

    private void setCourseLearningProgress(int courseId) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyCourseLearningProgress(EdusohoApp.app.token, courseId)
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

    private Observable<CourseSet> getCourseSet(int id) {
        return HttpUtils.getInstance().createApi(CourseSetApi.class).getCourseSet(id);
    }
}
