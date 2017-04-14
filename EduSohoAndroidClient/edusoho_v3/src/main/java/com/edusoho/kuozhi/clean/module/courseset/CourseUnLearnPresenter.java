package com.edusoho.kuozhi.clean.module.courseset;

import android.content.Context;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.Discount;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.module.courseset.info.CourseIntroduceFragment;
import com.edusoho.kuozhi.clean.module.courseset.plan.CourseProjectsFragment;
import com.edusoho.kuozhi.clean.module.courseset.review.CourseEvaluateFragment;
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

public class CourseUnLearnPresenter implements CourseUnLearnContract.Presenter {

    private static final String IS_FAVORITE = "isFavorite";
    private static final String BUY_ABLE = "1";
    private static final String IS_FREE = "1";
    private static final String IS_JOIN_SUCCESS = "success";
    private static final String FREE = "free";
    private static final String VIP = "vip";

    private CourseUnLearnContract.View mView;
    private int mCourseSetId;
    private CourseSet mCourseSet;
    private List<CourseProject> mCourseProjects;
    private List<VipInfo> mVipInfos;

    public CourseUnLearnPresenter(int courseSetId, CourseUnLearnContract.View view) {
        this.mCourseSetId = courseSetId;
        this.mView = view;
    }

    @Override
    public void subscribe() {
        if (mCourseSetId == 0) {
            mView.newFinish(true, R.string.lesson_unexit);
            return;
        }
        isJoin();
        //lauchLastViewCourseProject(mCourseSetId);
    }

    private void isJoin() {
        if (EdusohoApp.app.loginUser != null) {
            getCourseSetMember(mCourseSetId, EdusohoApp.app.loginUser.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<CourseMember>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.newFinish(true, R.string.lesson_unexit);
                        }

                        @Override
                        public void onNext(CourseMember courseSetMember) {
                            // TODO: 2017/4/14
//                            if (courseSetMember != null) {
//                                    getMeLastRecord();
//                            } else {
                                getCourseSet();
                                getFavoriteInfo();
//                            }
                        }
                    });
            return;
        }
        getCourseSet();
    }

    private void getCourseSet() {
        getCourseSet(mCourseSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CourseSet>() {
                    @Override
                    public void call(CourseSet courseSet) {
                        if (courseSet != null) {
                            mCourseSet = courseSet;
                            mView.showFragments(getTitleArray(), getFragmentArray());
                            mView.setCourseSet(courseSet);
                            mView.showBackGround(courseSet.cover.middle);
                            if (mCourseSet.discountId != 0) {
                                getDiscountInfo(mCourseSet.discountId);
                            }
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<CourseSet, Observable<List<CourseProject>>>() {
                    @Override
                    public Observable<List<CourseProject>> call(CourseSet courseSet) {
                        return getCourseStudyPlan(mCourseSetId);
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
                        return getVipInfo();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VipInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.newFinish(false, 0);
                    }

                    @Override
                    public void onNext(List<VipInfo> vipInfos) {
                        mVipInfos = vipInfos;
                        mView.showLoadView(false);
                    }
                });
    }

    private void getFavoriteInfo() {
        getFavorite(EdusohoApp.app.token, mCourseSetId)
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

    private void getDiscountInfo(int discountId) {
        RetrofitService.getDiscountInfo(discountId)
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
                        if (discount != null) {
                            if ("running".equals(discount.status)) {
                                long currentTime = System.currentTimeMillis();
                                long time = TimeUtils.getMillisecond(discount.endTime) / 1000 - currentTime / 1000;
                                if (time > 0) {
                                    mView.showDiscountInfo(discount.name, time);

                                }
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
            if (mCourseProjects != null && mVipInfos != null) {
                if (mCourseProjects.size() == 1) {
                    CourseProject courseProject = mCourseProjects.get(0);
                    if (!BUY_ABLE.equals(courseProject.buyable)) {
                        mView.showToast(R.string.course_limit_join);
                        return;
                    }
                    if (IS_FREE.equals(courseProject.isFree) || EdusohoApp.app.loginUser.vip != null
                            && mCourseProjects.get(0).vipLevelId != 0
                            && EdusohoApp.app.loginUser.vip.levelId >= mCourseProjects.get(0).vipLevelId) {
                        mView.showProcessDialog(true);
                        joinFreeOrVipCourse(IS_FREE.equals(courseProject.isFree) ? FREE : VIP);
                        return;
                    }
                    mView.goToConfirmOrderActivity(courseProject);
                }
                mView.showPlanDialog(mCourseProjects, mVipInfos, mCourseSet);
            }
        }
    }

    @Override
    public void consultTeacher() {
        if (EdusohoApp.app.loginUser == null) {
            CourseUtil.notLogin();
            return;
        }
        List<CourseSet.CreatorBean> list = mCourseSet.teachers;
        final CourseSet.CreatorBean creatorBean;
        if (list.size() > 0) {
            creatorBean = list.get(0);
        } else {
            mView.showToast(R.string.lesson_no_teacher);
            return;
        }
        mView.goToImChatActivity(creatorBean);
    }

    @Override
    public void favoriteCourseSet() {
        RetrofitService.favoriteCourseSet(EdusohoApp.app.token, mCourseSetId)
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
                        if (jsonObject != null && jsonObject.get("success").getAsBoolean()) {
                            mView.showFavoriteCourseSet(true);
                        }
                    }
                });
    }

    @Override
    public void cancelFavoriteCourseSet() {
        RetrofitService.cancelFavoriteCourseSet(EdusohoApp.app.token, mCourseSetId)
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
                        if (jsonObject != null && jsonObject.get("success").getAsBoolean()) {
                            mView.showFavoriteCourseSet(false);
                        }
                    }
                });
    }

    private void getMeLastRecord() {
        RetrofitService.getMeLastRecord(EdusohoApp.app.token, mCourseSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CourseMember>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.newFinish(true, R.string.lesson_unexit);
                    }

                    @Override
                    public void onNext(List<CourseMember> courseMembers) {
                        if (courseMembers != null) {
                            mView.goToCourseProjectActivity(getLastCourseId(courseMembers));
                            mView.newFinish(false, 0);
                        } else {
                            mView.newFinish(true, R.string.lesson_unexit);
                        }
                    }
                });
    }

    private int getLastCourseId(List<CourseMember> courseMembers) {
        int courseId = courseMembers.get(0).courseId;
        if (courseMembers.size() == 1) {
            return courseId;
        }
        for (int i = 0; i < courseMembers.size(); i++) {
            if (i != 0 && TimeUtils.getMillisecond(courseMembers.get(i-1).lastLearnTime)
                    < TimeUtils.getMillisecond(courseMembers.get(i).lastLearnTime)) {
                courseId = courseMembers.get(i).courseId;
            }
        }
        return courseId;
    }

    private void joinFreeOrVipCourse(String joinWay) {
        RetrofitService.joinFreeOrVipCourse(EdusohoApp.app.token, mCourseProjects.get(0).id, joinWay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showProcessDialog(false);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (jsonObject.get(IS_JOIN_SUCCESS).getAsBoolean()) {
                            mView.goToCourseProjectActivity(mCourseProjects.get(0).id);
                            mView.newFinish(true, R.string.join_success);
                        } else {
                            mView.showProcessDialog(false);
                            mView.showToast(R.string.join_fail);
                        }
                    }
                });
    }

    private void launchLastViewCourseProject(int courseSetId) {
        RetrofitService.getMyJoinCourses(EdusohoApp.app.token, courseSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<com.edusoho.kuozhi.clean.bean.CourseMember>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<CourseMember> courseMembers) {
                        CourseMember maxItem = courseMembers.get(0);
                        for (CourseMember courseMember : courseMembers) {
                            if (TimeUtils.getUTCtoDate(maxItem.lastViewTime).compareTo(TimeUtils.getUTCtoDate(courseMember.lastViewTime)) >= 1) {
                                maxItem = courseMember;
                            }
                        }
                        mView.goToCourseProjectActivity(maxItem.courseId);
                    }
                });
    }

    private Observable<CourseSet> getCourseSet(int courseSetId) {
        return RetrofitService.getCourseSet(courseSetId);
    }

    private Observable<CourseMember> getCourseSetMember(int courseSetId,int userId) {
        return RetrofitService.getCourseSetMember(courseSetId,userId);
    }

    private Observable<List<CourseProject>> getCourseStudyPlan(int courseSetId) {
        return RetrofitService.getCourseStudyPlan(courseSetId);
    }

    private Observable<List<VipInfo>> getVipInfo() {
        return RetrofitService.getVipInfo();
    }

    private Observable<JsonObject> getFavorite(String token, int courseSetId) {
        return RetrofitService.getFavorite(token, courseSetId);
    }

}
