package com.edusoho.kuozhi.clean.module.course.rate;

import android.support.v7.widget.RecyclerView;

import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public interface CourseProjectRatesContract {

    interface View extends BaseView<Presenter> {

        void loadRates(List<Review> reviews);

        int getDataCount();

        void loadMoreCompleted();
    }

    interface Presenter extends BasePresenter {
        void getRates();
    }
}
