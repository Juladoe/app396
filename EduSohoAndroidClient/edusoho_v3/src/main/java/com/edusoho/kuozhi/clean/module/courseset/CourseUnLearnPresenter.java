package com.edusoho.kuozhi.clean.module.courseset;

import android.content.Context;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.CourseStudyPlan;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.Discount;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.module.courseset.info.CourseIntroduceFragment;
import com.edusoho.kuozhi.clean.module.courseset.plan.StudyPlayFragment;
import com.edusoho.kuozhi.clean.module.courseset.review.CourseEvaluateFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.course.CourseMember;
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
    private String mCourseId;
    private CourseSet mCourseSet;
    private List<CourseStudyPlan> mCourseStudyPlans;
    private List<VipInfo> mVipInfos;

    public CourseUnLearnPresenter(String mCourseId, CourseUnLearnContract.View view) {
        this.mCourseId = mCourseId;
        this.mView = view;
    }

    @Override
    public void subscribe() {
        if (mCourseId == null || "0".equals(mCourseId)) {
            mView.newFinish(true, R.string.lesson_unexit);
            return;
        }
        isJoin();
    }

    @Override
    public void isJoin() {
        if (EdusohoApp.app.loginUser != null) {
            getCourseSetMember(mCourseId)
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
                                mView.goToCourseProjectActivity(courseId + "");
                                mView.newFinish(false, 0);
                            } else {
                                getCourseSet();
                                getFavoriteInfo(EdusohoApp.app.loginUser.id, mCourseId);
                            }
                        }
                    });
            return;
        }
        getCourseSet();
    }

    private void getCourseSet() {
        getCourseSet(mCourseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CourseSet>() {
                    @Override
                    public void call(CourseSet courseSet) {
                        if (courseSet != null) {
                            mCourseSet = courseSet;
                            mView.showFragments(getTitleArray(), getFragmentArray());
                            mView.setCourseSet(courseSet);
                            mView.showBackGround("http://demo.edusoho.com/files/course/2016/11-03/132045d61012373326.jpg");
//                            if (mCourseSet.getDiscountId() != 0) {
//                                getDiscountInfo(mCourseSet.getDiscountId());
                                getDiscountInfo(1);
//                            }
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<CourseSet, Observable<List<CourseStudyPlan>>>() {
                    @Override
                    public Observable<List<CourseStudyPlan>> call(CourseSet courseSet) {
                        return getCourseStudyPlan(mCourseId);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<List<CourseStudyPlan>>() {
                    @Override
                    public void call(List<CourseStudyPlan> list) {
                        mCourseStudyPlans = list;
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<List<CourseStudyPlan>, Observable<List<VipInfo>>>() {
                    @Override
                    public Observable<List<VipInfo>> call(List<CourseStudyPlan> list) {
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

    private void getFavoriteInfo(int userId, String courseId){
        getFavorite(userId, courseId)
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

    private void getDiscountInfo(int discountId){
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
                            if("running".equals(discount.getStatus())) {
                                long time = Long.parseLong(discount.getEndTime()) - Long.parseLong(discount.getStartTime());
                                mView.showDiscountInfo(discount.getName(), time);
                            }
                        }
                    }
                });
    }

    private String[] getFragmentArray() {
        return new String[]{
                CourseIntroduceFragment.class.getName(),
                StudyPlayFragment.class.getName(),
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
        if (!"0".equals(mCourseId)) {
            mView.showProcessDialog(true);
            if (mCourseStudyPlans != null && mVipInfos != null) {
                if (mCourseStudyPlans.size() == 1) {
                    CourseStudyPlan courseStudyPlan = mCourseStudyPlans.get(0);
                    if ("0".equals(courseStudyPlan.getBuyable())) {
                        mView.showToast(R.string.course_limit_join);
                        return;
                    }

                    if ("1".equals(courseStudyPlan.getIsFree())) {
                        mView.goToCourseProjectActivity(mCourseStudyPlans.get(0).getId());
                        mView.newFinish(true, R.string.join_success);
                    }
                    mView.goToConfirmOrderActivity(courseStudyPlan);
                }
                mView.showPlanDialog(mCourseStudyPlans, mVipInfos, mCourseSet);
            }
        }

//            if (EdusohoApp.app.loginUser != null && EdusohoApp.app.loginUser.vip != null
//                    && EdusohoApp.app.loginUser.vip.levelId >= mCourseDetail.getCourse().vipLevelId
//                    && mCourseDetail.getCourse().vipLevelId != 0) {
//                CourseUtil.addCourseVip(mCourseId, new CourseUtil.OnAddCourseListener() {
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

    private Observable<CourseSet> getCourseSet(String id) {
        return RetrofitService.getCourseSet(id);
    }

    private Observable<DataPageResult<CourseMember>> getCourseSetMember(String id) {
        return RetrofitService.getCourseSetMember(id);
    }

    private Observable<List<CourseStudyPlan>> getCourseStudyPlan(String id) {
        return RetrofitService.getCourseStudyPlan(id);
    }

    private Observable<List<VipInfo>> getVipInfo() {
        return RetrofitService.getVipInfo();
    }

    private Observable<JsonObject> getFavorite(int userId, String courseId){
        return RetrofitService.getFavorite(userId, courseId);
    }

}
