package com.edusoho.kuozhi.clean.module.courseset.order;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.clean.module.courseset.order.ConfirmOrderContract.View;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/6.
 */

public class ConfirmOrderPresenter implements ConfirmOrderContract.Presenter {

    private ConfirmOrderContract.View mView;
    private int mCourseId;
    private int mCourseSetId;

    public ConfirmOrderPresenter(ConfirmOrderContract.View mView, int courseSetId, int courseId) {
        this.mView = mView;
        this.mCourseId = courseId;
        this.mCourseSetId = courseSetId;
    }

    @Override
    public void subscribe() {
        RetrofitService.postOrderInfo(EdusohoApp.app.token, "course", mCourseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showProcessDialog(false);
                    }

                    @Override
                    public void onNext(OrderInfo orderInfo) {
                        mView.showProcessDialog(false);
                        if (orderInfo != null) {
                            mView.showPriceView(orderInfo);
                        }
                    }
                });
        RetrofitService.getCourseSet(mCourseSetId)
                .subscribeOn(Schedulers.io())
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
                        mView.showTopView(courseSet);
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

}
