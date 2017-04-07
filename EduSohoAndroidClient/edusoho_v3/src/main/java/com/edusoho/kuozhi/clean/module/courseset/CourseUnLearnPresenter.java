package com.edusoho.kuozhi.clean.module.courseset;

import android.content.Context;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.CourseStudyPlan;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.module.courseset.info.CourseIntroduceFragment;
import com.edusoho.kuozhi.clean.module.courseset.plan.StudyPlayFragment;
import com.edusoho.kuozhi.clean.module.courseset.review.CourseEvaluateFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.course.CourseMember;
import com.edusoho.kuozhi.v3.view.dialog.CustomDialog;

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
        mView.showFragments(getTitleArray(), getFragmentArray());
        if (mCourseId == null || "0".equals(mCourseId)) {
            mView.newFinish(true);
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
                    .filter(new Func1<DataPageResult<CourseMember>, Boolean>() {
                        @Override
                        public Boolean call(DataPageResult<CourseMember> courseMemberDataPageResult) {
                            return null;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<DataPageResult<CourseMember>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.newFinish(true);
                        }

                        @Override
                        public void onNext(DataPageResult<CourseMember> courseSetMember) {
                            boolean isMember = false;
                            for (CourseMember courseMember : courseSetMember.data) {
                                if (EdusohoApp.app.loginUser.id == courseMember.id) {
                                    isMember = true;
                                    break;
                                }
                            }
                            if (isMember) {
                                // TODO: 2017/4/3 已加入课程，直接进入任务
                            } else {
                                getCourseSet(mCourseId)
                                        .subscribeOn(Schedulers.io())
                                        .unsubscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<CourseSet>() {
                                            @Override
                                            public void onCompleted() {

                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                mView.newFinish(true);
                                            }

                                            @Override
                                            public void onNext(CourseSet courseSet) {

                                            }
                                        });
                            }
                        }
                    });
            return;
        }
        getCourseSet(mCourseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CourseSet>() {
                    @Override
                    public void call(CourseSet courseSet) {
                        if (courseSet != null) {
                            mCourseSet = courseSet;
                            mView.showBackGround("http://demo.edusoho.com/files/course/2016/11-03/132045d61012373326.jpg", courseSet);
                            getPlanAndVipInfo();
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<CourseSet, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(CourseSet courseSet) {
                        return getFavorite(EdusohoApp.app.loginUser.id, mCourseId);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

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

    private Observable<CourseSet> getCourseSet(String id) {
        return RetrofitService.getCourseSet(id);
    }

    @Override
    public void joinStudy(Context context) {
        mView.showProcessDialog(true);
        if (mCourseStudyPlans != null && mVipInfos != null) {
            new CustomDialog(context).initType(6).initPlanData(mCourseStudyPlans, mVipInfos, mCourseSet).show();
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


    private void getPlanAndVipInfo() {
        getCourseStudyPlan(mCourseId)
                .subscribeOn(Schedulers.io())
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

                    }

                    @Override
                    public void onNext(List<VipInfo> vipInfos) {
                        mVipInfos = vipInfos;
                    }
                });
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

    private Observable<Boolean> getFavorite(int userId, String courseId){
        return RetrofitService.getFavorite(userId, courseId);
    }

}
