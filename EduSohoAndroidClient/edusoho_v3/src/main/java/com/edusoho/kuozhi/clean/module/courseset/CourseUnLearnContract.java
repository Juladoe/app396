package com.edusoho.kuozhi.clean.module.courseset;

import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by DF on 2017/3/31.
 */

public interface CourseUnLearnContract {

    interface View extends BaseView<Presenter> {
        void showFragments(String[] titleArray, String[] fragmentArray);

        void showBackGround(String img, CourseSet courseSet);

        void showDiscountInfo(String... text);

        void showFavorite(boolean isFavorite);

        void newFinish(boolean isShow, int content);

        void showProcessDialog(boolean isShow);

        void showLoadView(boolean isShow);
    }

    interface Presenter extends BasePresenter {
        void isJoin();

        void joinStudy();
    }
}
