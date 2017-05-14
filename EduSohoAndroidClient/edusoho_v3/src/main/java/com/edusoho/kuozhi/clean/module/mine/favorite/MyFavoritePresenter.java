package com.edusoho.kuozhi.clean.module.mine.favorite;

import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/5/10.
 */

public class MyFavoritePresenter implements MyFavoriteContract.Presenter {

    private MyFavoriteContract.View mView;

    MyFavoritePresenter(MyFavoriteContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyFavoriteCourseSet(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataPageResult<CourseSet>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.setSwpFreshing(false);
                    }

                    @Override
                    public void onNext(DataPageResult<CourseSet> courseProjectDataPageResult) {
                        mView.setSwpFreshing(false);
                        if (courseProjectDataPageResult != null && courseProjectDataPageResult.data != null && courseProjectDataPageResult.data.size() > 0) {
                            mView.showComplete(courseProjectDataPageResult.data);
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
