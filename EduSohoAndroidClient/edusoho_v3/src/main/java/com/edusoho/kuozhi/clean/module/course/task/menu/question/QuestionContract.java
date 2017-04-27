package com.edusoho.kuozhi.clean.module.course.task.menu.question;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;

import java.util.List;

/**
 * Created by DF on 2017/4/25.
 */

public interface QuestionContract {

    interface View extends BaseView<Presenter>{

        void setEmptyView(boolean isShow);

        void setSwipeView(boolean isShow);

        void showCompleteView(List<DiscussDetail.ResourcesBean> mList, boolean isHave);

        void addAdapterData(List<DiscussDetail.ResourcesBean> list, boolean isHave);

        void changeAdapterMoreStatus(int status);

        void goToDiscussDetailActivity(DiscussDetail.ResourcesBean resourcesBean);

    }

    interface Presenter extends BasePresenter{

        void reFreshData();

    }

}
