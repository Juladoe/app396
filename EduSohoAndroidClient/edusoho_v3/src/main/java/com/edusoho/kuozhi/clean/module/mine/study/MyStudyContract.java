package com.edusoho.kuozhi.clean.module.mine.study;

import com.edusoho.kuozhi.clean.bean.StudyCourse;
import com.edusoho.kuozhi.clean.bean.innerbean.Study;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by DF on 2017/5/11.
 */

public interface MyStudyContract {

    interface View extends BaseView<Presenter>{

        void showStudyCourseComplete(List<StudyCourse> studyCourses);

        void showLiveCourseComplete(List<Study> studies);

        void showClassRoomComplete(List<Study> studies);

        void hideSwp();

    }

    interface Presenter extends BasePresenter{

        void getMyStudyCourse();

        void getMyStudyLiveCourseSet();

        void getMyStudyClassRoom();

    }

}
