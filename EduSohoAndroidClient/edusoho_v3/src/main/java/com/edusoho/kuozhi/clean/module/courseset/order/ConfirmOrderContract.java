package com.edusoho.kuozhi.clean.module.courseset.order;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.Map;

/**
 * Created by DF on 2017/4/5.
 */

public interface ConfirmOrderContract {

    interface View extends BaseView<Presenter>{

    }

    interface Presenter extends BasePresenter{

        void postOrderInfo(String type);

        void createOrder(Map<String, String> map);
    }

}
