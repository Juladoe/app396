package com.edusoho.kuozhi.clean.module.mine.favorite;

import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by DF on 2017/5/10.
 */

interface MyFavoriteContract {

    interface View extends BaseView<Presenter>{

        void showComplete(List<CourseSet> courseSets);

        void setSwpFreshing(boolean isRefreshing);

    }

    interface Presenter extends BasePresenter{

    }
}
