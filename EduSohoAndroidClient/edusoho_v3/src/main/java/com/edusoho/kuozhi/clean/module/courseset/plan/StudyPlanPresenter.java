package com.edusoho.kuozhi.clean.module.courseset.plan;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseProject;
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

    private int mCourseSetId;
    private StudyPlanContract.View mView;
    List<CourseProject> mCourseStudyPlen;

    public StudyPlanPresenter(StudyPlanContract.View view, int id) {
        this.mView = view;
        this.mCourseSetId = id;
    }

    @Override
    public void subscribe() {
        getCourseStudyPlan(mCourseSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<List<CourseProject>>() {
                    @Override
                    public void call(List<CourseProject> list) {
                        mCourseStudyPlen = list;
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

    private Observable<List<CourseProject>> getCourseStudyPlan(int courseSetId) {
        return RetrofitService.getCourseStudyPlan(courseSetId);
    }

    private Observable<List<VipInfo>> getVipInfo() {
        return RetrofitService.getVipInfo();
    }
}
