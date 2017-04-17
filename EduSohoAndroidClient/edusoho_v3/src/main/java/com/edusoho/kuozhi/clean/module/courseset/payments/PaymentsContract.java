package com.edusoho.kuozhi.clean.module.courseset.payments;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by DF on 2017/4/7.
 */

public interface PaymentsContract {

    interface View extends BaseView<Presenter>{

        void showLoadDialog(boolean isShow);

        void goToAlipay(String data);

        void sendBroad();

    }

    interface Presenter extends BasePresenter{

        void createOrderAndPay(final String payment, String password, float orderPrice);

    }

}
