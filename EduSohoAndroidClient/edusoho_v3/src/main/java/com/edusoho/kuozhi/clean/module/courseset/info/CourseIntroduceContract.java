package com.edusoho.kuozhi.clean.module.courseset.info;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by DF on 2017/4/1.
 */

public interface CourseIntroduceContract {

    interface View extends BaseView<Presenter> {

        void setLoadViewVis(boolean isVis);

        void showHead();

    }

    interface Presenter extends BasePresenter{

    }

}
