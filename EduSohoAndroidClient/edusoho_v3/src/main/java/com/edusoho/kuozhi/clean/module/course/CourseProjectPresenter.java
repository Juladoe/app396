package com.edusoho.kuozhi.clean.module.course;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.bean.TaskResultEnum;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.utils.CommonConstant;
import com.edusoho.kuozhi.clean.utils.TimeUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by JesseHuang on 2017/3/23.
 */

public class CourseProjectPresenter implements CourseProjectContract.Presenter {

    private static final String IS_JOIN_SUCCESS = "success";
    private static final float FREE_PRICE = 0f;
    private CourseProjectContract.View mView;
    private int mCourseProjectId;
    private Teacher mTeacher;
    private CourseMember mMember;
    private CourseTask mFirstTrailTask;
    private CourseProject mCourseProject;
    private boolean mIsJoin = false;

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
        HttpUtils.getInstance()
                .createApi(CourseApi.class)
                .getCourseProject(mCourseProjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseProject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CourseProject courseProject) {
                        mCourseProject = courseProject;
                        mView.showCover(mCourseProject.courseSet.cover.middle);
                        if (courseProject.teachers.length > 0) {
                            mTeacher = courseProject.teachers[0];
                        }
                        if (EdusohoApp.app.loginUser != null) {
                            initLoginCourseMemberStatus(courseProject);
                        } else {
                            initLogoutCourseMemberStatus(courseProject);
                            initTrialFirstTask(mCourseProjectId);
                        }
                    }
                });
    }

    private void initTrialFirstTask(final int courseId) {
        HttpUtils.getInstance()
                .createApi(CourseApi.class)
                .getTrialFirstTask(courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseTask>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CourseTask trialTask) {
                        if (trialTask != null && trialTask.id != 0) {
                            mFirstTrailTask = trialTask;
                            mView.initTrailTask(trialTask);
                        } else {
                            mView.setPlayLayoutVisible(false);
                        }
                    }
                });
    }

    @Override
    public void joinCourseProject() {
        if (mCourseProject.learningExpiryDate.expired) {
            return;
        }
        if (mCourseProject.originPrice == FREE_PRICE) {
            joinFreeOrVipCourse(mCourseProjectId);
        } else if (EdusohoApp.app.loginUser.vip != null && EdusohoApp.app.loginUser.vip.levelId >= mCourseProject.vipLevelId) {
            joinFreeOrVipCourse(mCourseProjectId);
        } else {
            mView.launchConfirmOrderActivity(mCourseProject.courseSet.id, mCourseProjectId);
        }
    }

    @Override
    public void finishTask(CourseTask task) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .setCourseTaskStatus(mCourseProjectId, task.id, CourseTask.CourseTaskStatusEnum.FINISH.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TaskEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TaskEvent taskEvent) {
                        mView.setTaskFinishButtonBackground(true);
                        mView.setCurrentTaskStatus(CourseTask.CourseTaskStatusEnum.FINISH);
                    }
                });
    }

    @Override
    public void exitCourse() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .exitCourse(mCourseProjectId)
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
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.COURSE_EXIT));
                            mIsJoin = false;
                            mView.showToast(R.string.exit_course_success);
                            initTrialFirstTask(mCourseProjectId);
                            mView.exitCourseLayout();
                        } else {
                            mView.showToast(R.string.exit_course_failure);
                        }
                    }
                });
    }

    private void initLoginCourseMemberStatus(final CourseProject courseProject) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getCourseMember(courseProject.id)
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
                    public void onNext(CourseMember member) {
                        mMember = member;
                        mIsJoin = member.user != null;
                        if (mIsJoin) {
                            mView.showFragments(initCourseModules(true), courseProject);
                            mView.initLearnLayout(CourseProject.LearnMode.getMode(courseProject.learnMode));
                            setCourseLearningProgress(courseProject.id);
                            if (courseProject.learningExpiryDate.expired) {
                                mView.showExitDialog(CourseProjectActivity.DialogType.COURSE_EXPIRED);
                            } else if (isCourseMemberExpired(member.deadline)) {
                                mView.showExitDialog(CourseProjectActivity.DialogType.COURSE_MEMBER_EXPIRED);
                            }
                            // TODO: 2017/4/20 还需要处理vip过期问题
                        } else {
                            mView.showFragments(initCourseModules(false), courseProject);
                            initTrialFirstTask(mCourseProjectId);
                            if (courseProject.learningExpiryDate.expired) {
                                mView.setJoinButton(false);
                            }
                        }
                    }
                });
    }

    private void initLogoutCourseMemberStatus(final CourseProject courseProject) {
        mView.showFragments(initCourseModules(false), courseProject);
        if (courseProject.learningExpiryDate.expired) {
            mView.setJoinButton(false);
        }
    }

    private void setCourseLearningProgress(int courseId) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyCourseLearningProgress(courseId)
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
                    public void onNext(CourseLearningProgress progress) {
                        // TODO: 2017/4/25 非常不好的处理方式，需要封装
                        MessageEvent<CourseLearningProgress> progressMsg = new MessageEvent<>(progress, MessageEvent.COURSE_JOIN);
                        EventBus.getDefault().post(progressMsg);
                        if (progress.nextTask != null) {
                            mView.initNextTask(progress.nextTask);
                        } else {
                            mView.setPlayLayoutVisible(false);
                        }
                    }
                });
    }

    private void joinFreeOrVipCourse(final int courseId) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .joinFreeOrVipCourse(courseId)
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
                        if (courseMember != null) {
                            mIsJoin = true;
                            mView.showToast(R.string.join_course_success);
                            mView.initJoinCourseLayout(CourseProject.LearnMode.getMode(mCourseProject.learnMode));
                            setCourseLearningProgress(courseId);
                        }
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

    @Override
    public boolean isJoin() {
        return mIsJoin;
    }

    @Override
    public CourseMember getCourseMember() {
        return mMember;
    }

    @Override
    public void unsubscribe() {

    }

    private boolean isCourseMemberExpired(String deadline) {
        return !CommonConstant.EXPIRED_MODE_FOREVER.equals(deadline) && TimeUtils.getUTCtoDate(deadline).compareTo(new Date()) < 0;
    }
}
