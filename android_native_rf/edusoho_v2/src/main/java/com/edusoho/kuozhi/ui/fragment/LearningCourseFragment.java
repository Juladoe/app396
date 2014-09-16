package com.edusoho.kuozhi.ui.fragment;

import com.edusoho.kuozhi.util.Const;

/**
 * Created by howzhi on 14-9-16.
 */
public class LearningCourseFragment extends MyCourseBaseFragment {

    @Override
    protected String getBaseUrl() {
        return Const.LEARNING;
    }

    @Override
    protected String getEmptyTitle() {
        return "没有在学课程";
    }
}
