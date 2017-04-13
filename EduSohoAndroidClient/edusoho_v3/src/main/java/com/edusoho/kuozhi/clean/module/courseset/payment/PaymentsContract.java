package com.edusoho.kuozhi.clean.module.courseset.payment;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.Map;

/**
 * Created by DF on 2017/4/7.
 */

public interface PaymentsContract {

    interface View extends BaseView<Presenter>{

        void showLoadDialog(boolean isShow);

        void goToAlipay(String data);

    }

    interface Presenter extends BasePresenter{

        void createOrderAndPay(Map<String, String> map, String type, String payment);

    }

}
