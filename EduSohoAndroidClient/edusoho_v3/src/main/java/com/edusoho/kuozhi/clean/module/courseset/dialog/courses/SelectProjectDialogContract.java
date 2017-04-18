package com.edusoho.kuozhi.clean.module.courseset.dialog.courses;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by DF on 2017/4/13.
 */

class SelectProjectDialogContract {

    interface View extends BaseView<Presenter>{

        void showToastOrFinish(int content, boolean isFinish);

        void showProcessDialog(boolean isShow);

        void goToConfirmOrderActivity();

        void goToCourseProjectActivity();

    }

    interface Presenter extends BasePresenter {

        void confirm(CourseProject courseProject);

    }

}
