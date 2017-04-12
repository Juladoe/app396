package com.edusoho.kuozhi.clean.module.courseset.order;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by DF on 2017/4/5.
 */

public interface ConfirmOrderContract {

    interface View extends BaseView<Presenter>{

        void showCouponView();
    }

    interface Presenter extends BasePresenter{
    }

}
