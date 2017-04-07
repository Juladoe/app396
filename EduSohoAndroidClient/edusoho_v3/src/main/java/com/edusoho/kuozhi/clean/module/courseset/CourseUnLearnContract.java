package com.edusoho.kuozhi.clean.module.courseset;

import android.content.Context;

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

        void showFavorite(boolean isFavorite);

        void newFinish(boolean isShow);

        void showProcessDialog(boolean isShow);
    }

    interface Presenter extends BasePresenter {
        void isJoin();

        void joinStudy(Context context);
    }
}
