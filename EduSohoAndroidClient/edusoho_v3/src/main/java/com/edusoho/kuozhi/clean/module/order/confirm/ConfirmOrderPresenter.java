package com.edusoho.kuozhi.clean.module.order.confirm;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseSetApi;
import com.edusoho.kuozhi.clean.api.OrderApi;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/6.
 */

class ConfirmOrderPresenter implements ConfirmOrderContract.Presenter {

    private ConfirmOrderContract.View mView;
    private int mCourseId;
    private int mCourseSetId;

    ConfirmOrderPresenter(ConfirmOrderContract.View mView, int courseSetId, int courseId) {
        this.mView = mView;
        this.mCourseId = courseId;
        this.mCourseSetId = courseSetId;
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(OrderApi.class)
                .postOrderInfo("course", mCourseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showProcessDialog(false);
                        mView.showToastAndFinish(R.string.confirm_order_error_hint);
                    }

                    @Override
                    public void onNext(OrderInfo orderInfo) {
                        mView.showProcessDialog(false);
                        if (orderInfo != null) {
                            mView.showPriceView(orderInfo);
                        }
                    }
                });
        HttpUtils.getInstance()
                .createApi(CourseSetApi.class)
                .getCourseSet(mCourseSetId)
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
