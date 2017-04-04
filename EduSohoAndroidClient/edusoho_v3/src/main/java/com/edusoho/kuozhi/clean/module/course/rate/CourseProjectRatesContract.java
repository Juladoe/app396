package com.edusoho.kuozhi.clean.module.course.rate;

import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public interface CourseProjectRatesContract {

    interface View extends BaseView<Presenter> {

        void showRates(List<Review> reviews);
    }

    interface Presenter extends BasePresenter {

    }
}
