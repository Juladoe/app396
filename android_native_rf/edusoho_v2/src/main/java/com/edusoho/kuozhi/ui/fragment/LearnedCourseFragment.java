package com.edusoho.kuozhi.ui.fragment;

import com.edusoho.kuozhi.util.Const;

/**
 * Created by howzhi on 14-8-19.
 */
public class LearnedCourseFragment extends MyCourseBaseFragment {

    @Override
    protected String getBaseUrl() {
        return Const.LEARNED;
    }

    @Override
    protected String getEmptyTitle() {
        return "没有已学课程";
    }
}
