package com.edusoho.test;

import android.test.UiThreadTest;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.edusoho.kuozhi.v3.ui.LessonDownloadingActivity;
import com.edusoho.test.base.BaseActivityUnitTestCase;

/**
 * Created by JesseHuang on 15/8/25.
 */
public class LessonDownloadingActivityTest extends BaseActivityUnitTestCase<LessonDownloadingActivity> {
    public LessonDownloadingActivityTest() {
        super(LessonDownloadingActivity.class);
    }

    @UiThreadTest
    public void testLessonDownloadingActivity() {
        LessonDownloadingActivity mActivity = getActivity();
        assertNotNull(mActivity);
    }

    @UiThreadTest
    public void testLessonDownloadingActivityLayout() {
        LessonDownloadingActivity mActivity = getActivity();
        ExpandableListView mListView = (ExpandableListView) mActivity.findViewById(R.id.el_download);
        assertNotNull(mListView);
        TextView btnSelectAll = (TextView) mActivity.findViewById(R.id.tv_select_all);
        assertNotNull(btnSelectAll);
        TextView btnDownload = (TextView) mActivity.findViewById(R.id.tv_download);
        assertNotNull(btnDownload);
    }
}
