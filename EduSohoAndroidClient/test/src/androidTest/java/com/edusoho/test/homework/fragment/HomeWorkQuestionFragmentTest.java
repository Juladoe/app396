package com.edusoho.test.homework.fragment;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.homework.ui.fragment.HomeWorkQuestionFragment;
import com.edusoho.test.FragmentTestActivity;
import com.edusoho.test.base.BaseFragmentTestCase;

/**
 * Created by Melomelon on 2015/11/13.
 */
public class HomeWorkQuestionFragmentTest extends BaseFragmentTestCase<HomeWorkQuestionFragment> {

    public HomeWorkQuestionFragmentTest() {
        super(HomeWorkQuestionFragment.class);
    }

    @UiThreadTest
    public void testGetFragment() {
        mFragment = getFragment();
        assertNotNull(mFragment);
    }

    @UiThreadTest
    public void testViewLayout() {
        View layoutView = getFragment().getView();
        TextView mQuestionIndexView = (TextView) layoutView.findViewById(com.edusoho.kuozhi.homework.R.id.homework_index);
        assertNotNull(mQuestionIndexView);
        TextView mQuestionTitleView = (TextView) layoutView.findViewById(com.edusoho.kuozhi.homework.R.id.homework_title);
        assertNotNull(mQuestionTitleView);
        ViewPager mHomeworkQuestionPager = (ViewPager) layoutView.findViewById(com.edusoho.kuozhi.homework.R.id.homework_viewpaper);
        assertNotNull(mHomeworkQuestionPager);
    }
}
