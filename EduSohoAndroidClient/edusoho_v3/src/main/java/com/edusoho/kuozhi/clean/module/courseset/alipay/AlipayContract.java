package com.edusoho.kuozhi.clean.module.courseset.alipay;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by DF on 2017/4/12.
 */

public interface AlipayContract {

    interface View extends BaseView<Presenter>{

        void showLoadDialog(boolean isShow);
    }

    interface Presenter extends BasePresenter{

    }


}
