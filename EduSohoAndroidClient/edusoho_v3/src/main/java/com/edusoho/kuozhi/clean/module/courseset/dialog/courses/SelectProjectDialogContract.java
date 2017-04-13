package com.edusoho.kuozhi.clean.module.courseset.dialog.courses;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by DF on 2017/4/13.
 */

public class SelectProjectDialogContract {

    interface View extends BaseView<Presenter>{}

    interface Presenter extends BasePresenter {

        void joinFreeCourse(int courseId);

    }

}
