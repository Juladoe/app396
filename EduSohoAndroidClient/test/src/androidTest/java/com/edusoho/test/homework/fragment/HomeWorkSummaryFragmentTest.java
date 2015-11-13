package com.edusoho.test.homework.fragment;

import android.support.v4.app.Fragment;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.homework.ui.fragment.HomeWorkSummaryFragment;
import com.edusoho.test.base.BaseFragmentTestCase;

/**
 * Created by Melomelon on 2015/11/13.
 */
public class HomeWorkSummaryFragmentTest extends BaseFragmentTestCase<HomeWorkSummaryFragment> {

    public HomeWorkSummaryFragmentTest() {
        super(HomeWorkSummaryFragment.class);
    }

    @UiThreadTest
    public void testGetFragment() {
        Fragment mFragment = getFragment();
        assertNotNull(mFragment);
    }

    @UiThreadTest
    public void testViewLayout() {
        View layoutView = getFragment().getView();
        TextView tvCourseTitle = (TextView) layoutView.findViewById(com.edusoho.kuozhi.homework.R.id.homework_belong_content);
        assertNotNull(tvCourseTitle);
        View mLoadLayout = layoutView.findViewById(com.edusoho.kuozhi.homework.R.id.load_layout);
        assertNotNull(mLoadLayout);
        TextView homeworkName = (TextView) layoutView.findViewById(com.edusoho.kuozhi.homework.R.id.homework_name);
        assertNotNull(homeworkName);
        TextView homeworkNameContent = (TextView) layoutView.findViewById(com.edusoho.kuozhi.homework.R.id.homework_name_content);
        assertNotNull(homeworkNameContent);
        TextView homeworkInfo = (TextView) layoutView.findViewById(com.edusoho.kuozhi.homework.R.id.homework_info);
        assertNotNull(homeworkInfo);
        TextView homeworkInfoContent = (TextView) layoutView.findViewById(com.edusoho.kuozhi.homework.R.id.homework_info_content);
        assertNotNull(homeworkInfoContent);
    }
}
