package com.edusoho.kuozhi.clean.module.courseset.payment;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.Map;

/**
 * Created by DF on 2017/4/7.
 */

public interface PayWayContract {

    interface View extends BaseView<Presenter>{

    }

    interface Presenter extends BasePresenter{

        void createOrder(String token, Map<String, String> map);

        void goPay(String type, String payWay);

    }

}
