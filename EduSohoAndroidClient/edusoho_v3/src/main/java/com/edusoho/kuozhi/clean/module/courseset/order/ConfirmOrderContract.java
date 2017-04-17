package com.edusoho.kuozhi.clean.module.courseset.order;

import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by DF on 2017/4/5.
 */

public interface ConfirmOrderContract {

    interface View extends BaseView<Presenter>{

        void showView(OrderInfo orderInfo);

        void showProcessDialog(boolean isShow);
    }

    interface Presenter extends BasePresenter{
    }

}
