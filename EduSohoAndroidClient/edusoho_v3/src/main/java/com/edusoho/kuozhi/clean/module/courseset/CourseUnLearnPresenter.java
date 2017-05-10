package com.edusoho.kuozhi.clean.module.courseset;

import android.content.Context;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.api.CourseSetApi;
import com.edusoho.kuozhi.clean.api.PluginsApi;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.Discount;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.module.courseset.info.CourseIntroduceFragment;
import com.edusoho.kuozhi.clean.module.courseset.plan.CourseProjectsFragment;
import com.edusoho.kuozhi.clean.module.courseset.review.CourseEvaluateFragment;
import com.edusoho.kuozhi.clean.utils.biz.CourseHelper;
import com.edusoho.kuozhi.clean.utils.TimeUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.google.gson.JsonObject;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/3/31.
 */

class CourseUnLearnPresenter implements CourseUnLearnContract.Presenter {

    private static final String IS_FAVORITE = "isFavorite";
    private static final String SUCCESS = "success";

    private CourseUnLearnContract.View mView;
    private int mCourseSetId;
    private CourseSet mCourseSet;
    private List<CourseProject> mCourseProjects;
    private List<VipInfo> mVipInfos;

    CourseUnLearnPresenter(int courseSetId, CourseUnLearnContract.View view) {
        this.mCourseSetId = courseSetId;
        this.mView = view;
    }

    @Override
    public void subscribe() {
        if (mCourseSetId == 0) {
            mView.showToast(R.string.lesson_unexit);
            mView.newFinish();
            return;
        }
        isJoin();
    }

    @Override
    public void isJoinCourseSet() {
        if (EdusohoApp.app.loginUser != null) {
            HttpUtils.getInstance()
                    .addTokenHeader(EdusohoApp.app.token)
                    .createApi(CourseSetApi.class)
                    .getMeCourseSetProject(mCourseSetId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<CourseMember>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.showProcessDialog(false);
                        }

                        @Override
                        public void onNext(List<CourseMember> courseMembers) {
                            mView.showProcessDialog(false);
                            if (courseMembers != null && courseMembers.size() > 0) {
                                queryMeLastRecord(courseMembers);
                            } else {
                                acquireCourseProjects();
                            }
                        }
                    });
        }
    }

    private void isJoin() {
        if (EdusohoApp.app.loginUser != null) {
            HttpUtils.getInstance()
                    .addTokenHeader(EdusohoApp.app.token)
                    .createApi(CourseSetApi.class)
                    .getMeCourseSetProject(mCourseSetId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<CourseMember>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.showToast(R.string.lesson_unexit);
                            mView.newFinish();
                        }

                        @Override
                        public void onNext(List<CourseMember> courseMembers) {
                            if (courseMembers != null && courseMembers.size() > 0) {
                                queryMeLastRecord(courseMembers);
                            } else {
                                acquireCourseSet();
                                acquireFavoriteInfo();
                            }
                        }
                    });
        } else {
            acquireCourseSet();
        }
    }

    private void acquireCourseSet() {
        HttpUtils.getInstance()
                .createApi(CourseSetApi.class)
                .getCourseSet(mCourseSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CourseSet>() {
                    @Override
                    public void call(CourseSet courseSet) {
                        if (courseSet != null) {
                            mCourseSet = courseSet;
                            mView.showFragments(getTitleArray(), getFragmentArray());
                            mView.setCourseSet(courseSet);
                            if (mCourseSet.discountId != 0) {
                                acquireDiscountInfo(mCourseSet.discountId);
                            }
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<CourseSet, Observable<List<CourseProject>>>() {
                    @Override
                    public Observable<List<CourseProject>> call(CourseSet courseSet) {
                        return HttpUtils
                                .getInstance()
                                .addTokenHeader(EdusohoApp.app.token)
                                .createApi(CourseSetApi.class)
                                .getCourseProjects(mCourseSetId);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<List<CourseProject>>() {
                    @Override
                    public void call(List<CourseProject> list) {
                        mCourseProjects = list;
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<List<CourseProject>, Observable<List<VipInfo>>>() {
                    @Override
                    public Observable<List<VipInfo>> call(List<CourseProject> list) {
                        return HttpUtils
                                .getInstance()
                                .createApi(PluginsApi.class)
                                .getVipInfo();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VipInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.newFinish();
                    }

                    @Override
                    public void onNext(List<VipInfo> vipInfos) {
                        mVipInfos = vipInfos;
                        mView.showLoadView(false);
                    }
                });
    }

    private void acquireFavoriteInfo() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getFavorite(mCourseSetId)
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
                        if (jsonObject != null) {
                            if (jsonObject.get(IS_FAVORITE).getAsBoolean()) {
                                mView.showFavorite(true);
                            }
                        }
                    }
                });
    }

    private void acquireDiscountInfo(int discountId) {
        HttpUtils.getInstance()
                .createApi(PluginsApi.class)
                .getDiscountInfo(discountId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Discount>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Discount discount) {
                        if (discount != null && Discount.STATUS_RUNNING.equals(discount.status)) {
                            long time = TimeUtils.getMillisecond(discount.endTime) / 1000
                                    - System.currentTimeMillis() / 1000;
                            if (time > 0) {
                                mView.showDiscountInfo(discount.name, time);

                            }
                        }
                    }
                });
    }

    private String[] getFragmentArray() {
        return new String[]{
                CourseIntroduceFragment.class.getName(),
                CourseProjectsFragment.class.getName(),
                CourseEvaluateFragment.class.getName()
        };
    }

    private String[] getTitleArray() {
        return ((Context) mView).getResources().getStringArray(R.array.unlearn_tab);
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void joinStudy() {
        if (mCourseSetId != 0) {
            if (EdusohoApp.app.loginUser == null) {
                mView.goToLoginActivity();
                return;
            }
            if (mCourseProjects != null) {
                if (mCourseProjects.size() == 1) {
                    CourseProject courseProject = mCourseProjects.get(0);
                    if (SUCCESS.equals(courseProject.access.code)) {
                        joinFreeOrVipCourse();
                    } else {
                        mView.showToast(CourseHelper.getCourseErrorRes(courseProject.access.code));
                    }
                    return;
                }
                if (mVipInfos != null) {
                    mView.showPlanDialog(mCourseProjects, mVipInfos, mCourseSet);
                }
            }
        }
    }

    @Override
    public void consultTeacher() {
        if (EdusohoApp.app.loginUser == null) {
            CourseUtil.notLogin();
            return;
        }
        List<Teacher> list = mCourseSet.teachers;
        final Teacher teacher;
        if (list.size() > 0) {
            teacher = list.get(0);
        } else {
            mView.showToast(R.string.lesson_no_teacher);
            return;
        }
        mView.goToImChatActivity(teacher);
    }

    @Override
    public void favoriteCourseSet() {
        if (EdusohoApp.app.loginUser == null) {
            mView.goToLoginActivity();
            return;
        }
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .favoriteCourseSet(mCourseSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showToast(R.string.operate_fail);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (jsonObject != null && jsonObject.get(SUCCESS).getAsBoolean()) {
                            mView.showFavoriteCourseSet(true);
                        }
                    }
                });
    }

    @Override
    public void cancelFavoriteCourseSet() {
        if (EdusohoApp.app.loginUser == null) {
            mView.goToLoginActivity();
            return;
        }
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .cancelFavoriteCourseSet(mCourseSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showToast(R.string.operate_fail);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (jsonObject != null && jsonObject.get(SUCCESS).getAsBoolean()) {
                            mView.showFavoriteCourseSet(false);
                        }
                    }
                });
    }

    private void acquireCourseProjects() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseSetApi.class)
                .getCourseProjects(mCourseSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CourseProject>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<CourseProject> courseProjects) {
                        if (courseProjects != null) {
                            mView.setDialogData(courseProjects);
                        }
                    }
                });
    }

    private void queryMeLastRecord(List<CourseMember> courseProjects) {
        mView.goToCourseProjectActivity(getLastCourseId(courseProjects));
        mView.newFinish();
    }

    private int getLastCourseId(List<CourseMember> courseMembers) {
        int courseId = courseMembers.get(0).courseId;
        if (courseMembers.size() == 1) {
            return courseId;
        }
        for (int i = 0; i < courseMembers.size(); i++) {
            if (i != 0 && TimeUtils.getMillisecond(courseMembers.get(i - 1).lastViewTime)
                    < TimeUtils.getMillisecond(courseMembers.get(i).lastViewTime)) {
                courseId = courseMembers.get(i).courseId;
            }
        }
        return courseId;
    }

    private void joinFreeOrVipCourse() {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .joinFreeOrVipCourse(mCourseProjects.get(0).id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseMember>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showProcessDialog(false);
                        mView.showToast(R.string.join_fail);
                    }

                    @Override
                    public void onNext(CourseMember courseMember) {
                        mView.showProcessDialog(false);
                        if (courseMember != null && courseMember.user != null) {
                            mView.goToCourseProjectActivity(mCourseProjects.get(0).id);
                            mView.showToast(R.string.join_success);
                            mView.newFinish();
                        } else {
                            mView.goToConfirmOrderActivity(mCourseProjects.get(0));
                        }
                    }
                });
    }

}
