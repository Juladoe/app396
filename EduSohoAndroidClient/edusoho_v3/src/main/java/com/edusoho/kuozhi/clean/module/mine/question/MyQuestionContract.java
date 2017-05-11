package com.edusoho.kuozhi.clean.module.mine.question;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;
import com.edusoho.kuozhi.v3.model.bal.thread.MyThreadEntity;

/**
 * Created by DF on 2017/5/11.
 */

interface MyQuestionContract {

    interface View extends BaseView<Presenter>{

        void showAskComplete(MyThreadEntity[] myThreadEntities);

        void showAnswerComplete(MyThreadEntity[] entities);

        void hideSwp();

    }

    interface Presenter extends BasePresenter{

        void requestAskData();

        void requestAnswerData();

    }

}
