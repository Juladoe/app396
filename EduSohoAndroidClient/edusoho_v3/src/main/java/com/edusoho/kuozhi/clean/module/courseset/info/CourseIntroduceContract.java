package com.edusoho.kuozhi.clean.module.courseset.info;

import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;
import com.edusoho.kuozhi.v3.model.bal.course.CourseMember;

/**
 * Created by DF on 2017/4/1.
 */

public interface CourseIntroduceContract {

    interface View extends BaseView<Presenter> {

        void setData(CourseSet courseSet);

        void setLoadViewVis(boolean isVis);

        void showHead();

        void showInfoAndPeople();

        void showStudent(CourseMember[] courseMembers);

    }

    interface Presenter extends BasePresenter{

    }

}
