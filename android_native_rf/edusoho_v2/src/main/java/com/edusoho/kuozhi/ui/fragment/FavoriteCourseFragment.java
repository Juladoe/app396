package com.edusoho.kuozhi.ui.fragment;

import com.edusoho.kuozhi.util.Const;

/**
 * Created by howzhi on 14-8-19.
 */
public class FavoriteCourseFragment extends MyCourseBaseFragment {

    @Override
    protected String getBaseUrl() {
        return Const.FAVORITES;
    }

    @Override
    protected String getEmptyTitle() {
        return "没有收藏课程";
    }
}
