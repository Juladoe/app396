package com.edusoho.kuozhi.clean.module.courseset.plan;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseStudyPlan;
import com.edusoho.kuozhi.clean.bean.VipInfo;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/1.
 */

public class StudyPlanPresenter implements StudyPlanContract.Presenter {

    private String mCourseId;
    private StudyPlanContract.View mView;
    List<CourseStudyPlan> mCourseStudyPlen;

    public StudyPlanPresenter(StudyPlanContract.View view, String id) {
        this.mView = view;
        this.mCourseId = id;
    }

    @Override
    public void subscribe() {
        getCourseStudyPlan(mCourseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<List<CourseStudyPlan>>() {
                    @Override
                    public void call(List<CourseStudyPlan> list) {
                        mCourseStudyPlen = list;
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
                        mView.setLoadViewVis(false);
                    }

                    @Override
                    public void onNext(List<VipInfo> vipInfo) {
                        mView.setLoadViewVis(false);
                        if (mCourseStudyPlen != null && mCourseStudyPlen.size() != 0 && vipInfo != null) {
                            mView.showComPanies(mCourseStudyPlen, vipInfo);
                        }
                    }
                });
    }


    @Override
    public void unsubscribe() {

    }

    private Observable<List<CourseStudyPlan>> getCourseStudyPlan(String id) {
        return RetrofitService.getCourseStudyPlan(id);
    }

    private Observable<List<VipInfo>> getVipInfo() {
        return RetrofitService.getVipInfo();
    }
}
