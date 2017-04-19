package com.edusoho.kuozhi.clean.module.courseset.plan;

import com.edusoho.kuozhi.clean.api.CourseSetApi;
import com.edusoho.kuozhi.clean.api.PluginsApi;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.http.HttpUtils;

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

class CourseProjectsPresenter implements CourseProjectsContract.Presenter {

    private int mCourseSetId;
    private CourseProjectsContract.View mView;
    private List<CourseProject> mCourseProjects;

    CourseProjectsPresenter(CourseProjectsContract.View view, int id) {
        this.mView = view;
        this.mCourseSetId = id;
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .createApi(CourseSetApi.class)
                .getCourseProjects(mCourseSetId)
                .subscribeOn(Schedulers.io())
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
                        return HttpUtils.getInstance()
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
                        mView.setLoadViewVis(false);
                    }

                    @Override
                    public void onNext(List<VipInfo> vipInfo) {
                        mView.setLoadViewVis(false);
                        if (mCourseProjects != null && mCourseProjects.size() != 0 && vipInfo != null) {
                            mView.showComPanies(mCourseProjects, vipInfo);
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

}
