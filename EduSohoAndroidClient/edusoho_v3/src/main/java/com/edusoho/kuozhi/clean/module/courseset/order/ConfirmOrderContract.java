package com.edusoho.kuozhi.clean.module.courseset.order;

import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by DF on 2017/4/5.
 */

public interface ConfirmOrderContract {

    interface View extends BaseView<Presenter> {

        void showPriceView(OrderInfo orderInfo);

        void showProcessDialog(boolean isShow);

        void showTopView(CourseSet courseSet);

        void showToastAndFinish(int content);
    }

    interface Presenter extends BasePresenter {
    }

}
