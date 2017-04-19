package com.edusoho.kuozhi.clean.module.courseset.review;

import com.edusoho.kuozhi.clean.bean.CourseReview;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by DF on 2017/3/31.
 */

interface CourseEvaluateContract {

    interface View extends BaseView<Presenter> {
        void setLoadViewVis(boolean isVis);

        void setEmptyViewVis(boolean isVis);

        void showCompanies(List<CourseReview.DataBean> list);

        void setRecyclerViewStatus(int status);

        void addData(List<CourseReview.DataBean> list);

        void changeMoreStatus(int status);

        void showToast();
    }

    interface Presenter extends BasePresenter {
        void loadMore();
    }

}
