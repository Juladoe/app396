package com.edusoho.kuozhi.clean.module.courseset;

import android.content.Context;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
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

    private CourseUnLearnContract.View mView;
    private int mCourseSetId;
    private CourseSet mCourseSet;
    private List<CourseProject> mCourseStudyPlans;
    private List<VipInfo> mVipInfos;

    public CourseUnLearnPresenter(int mCourseSetId, CourseUnLearnContract.View view) {
        this.mCourseSetId = mCourseSetId;
        this.mView = view;
    }

    @Override
    public void subscribe() {
        if (mCourseSetId == 0) {
            mView.newFinish(true, R.string.lesson_unexit);
            return;
        }
        isJoin();
    }

    private void isJoin() {
        if (EdusohoApp.app.loginUser != null) {
            getCourseSetMember(mCourseSetId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<DataPageResult<CourseMember>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.newFinish(true, R.string.lesson_unexit);
                        }

                        @Override
                        public void onNext(DataPageResult<CourseMember> courseSetMember) {
                            boolean isMember = false;
                            int courseId = 0;
                            for (CourseMember courseMember : courseSetMember.data) {
                                if (EdusohoApp.app.loginUser.id == courseMember.id) {
                                    isMember = true;
                                    courseId = courseMember.courseId;
                                    break;
                                }
                            }
                            if (isMember) {
                                mView.goToCourseProjectActivity(courseId);
                                mView.newFinish(false, 0);
                            } else {
                                getCourseSet();
                                getFavoriteInfo(EdusohoApp.app.loginUser.id, mCourseSetId);
                            }
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
                        mCourseStudyPlans = list;
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

    private void getFavoriteInfo(int userId, int courseSetId) {
        getFavorite(userId, courseSetId)
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
                            if (jsonObject.get("isFavorite").getAsBoolean()) {
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
            if (mCourseStudyPlans != null && mVipInfos != null) {
                if (mCourseStudyPlans.size() == 1) {
                    CourseProject courseStudyPlan = mCourseStudyPlans.get(0);
                    if ("0".equals(courseStudyPlan.buyable)) {
                        mView.showToast(R.string.course_limit_join);
                        return;
                    }
                    if ("1".equals(courseStudyPlan.isFree)) {
                        mView.showProcessDialog(true);
                        joinFreeCourse();
                    }
                    mView.goToConfirmOrderActivity(courseStudyPlan);
                }
                mView.showPlanDialog(mCourseStudyPlans, mVipInfos, mCourseSet);
            }
        }

//            if (EdusohoApp.app.loginUser != null && EdusohoApp.app.loginUser.vip != null
//                    && EdusohoApp.app.loginUser.vip.levelId >= mCourseDetail.getCourse().vipLevelId
//                    && mCourseDetail.getCourse().vipLevelId != 0) {
//                CourseUtil.addCourseVip(mCourseSetId, new CourseUtil.OnAddCourseListener() {
//                    @Override
//                    public void onAddCourseSuccess(String response) {
//                        hideProcesDialog();
//                        CommonUtil.shortToast(CourseStudyDetailActivity.this, getResources()
//                                .getString(R.string.success_add_course));
//                        initData();
//                    }
//
//                    @Override
//                    public void onAddCourseError(String response) {
//                        hideProcesDialog();
//                    }
//                });
//                return;
//            }
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

    private void joinFreeCourse() {
        RetrofitService.joinFreeCourse(EdusohoApp.app.token)
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
                        if (jsonObject.get("success").getAsBoolean()) {
                            mView.goToCourseProjectActivity(mCourseStudyPlans.get(0).id);
                            mView.newFinish(true, R.string.join_success);
                        } else {
                            mView.showProcessDialog(false);
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

    private Observable<DataPageResult<CourseMember>> getCourseSetMember(int courseSetId) {
        return RetrofitService.getCourseSetMember(courseSetId);
    }

    private Observable<List<CourseProject>> getCourseStudyPlan(int courseSetId) {
        return RetrofitService.getCourseStudyPlan(courseSetId);
    }

    private Observable<List<VipInfo>> getVipInfo() {
        return RetrofitService.getVipInfo();
    }

    private Observable<JsonObject> getFavorite(int userId, int courseSetId) {
        return RetrofitService.getFavorite(userId, courseSetId);
    }

}
