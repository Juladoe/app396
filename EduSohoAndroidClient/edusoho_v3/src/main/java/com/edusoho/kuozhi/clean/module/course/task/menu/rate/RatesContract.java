package com.edusoho.kuozhi.clean.module.course.task.menu.rate;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by DF on 2017/4/25.
 */

public interface RatesContract {

    interface View extends BaseView<Presenter>{

        void initFragment(CourseProject courseProject);

    }

    interface Presenter extends BasePresenter{

    }

}
