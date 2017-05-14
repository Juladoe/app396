package com.edusoho.kuozhi.clean.module.mine.teach;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;
import com.edusoho.kuozhi.v3.entity.lesson.TeachLesson;

/**
 * Created by DF on 2017/5/12.
 */

public interface MyTeachContract {

    interface View extends BaseView<Presenter>{

        void hideSwpView();

        void showRequestComplete(TeachLesson teachLesson);

    }

    interface Presenter extends BasePresenter{

    }

}
