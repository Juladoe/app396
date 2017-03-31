package com.edusoho.kuozhi.clean.module.courseset;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by DF on 2017/3/31.
 */

public interface CourseUnJoinContract {

    interface View extends BaseView<Presenter> {
        void showFragments(String[] titleArray, String[] fragmentArray);

        void newFinish();
    }

    interface Presenter extends BasePresenter {

    }
}
