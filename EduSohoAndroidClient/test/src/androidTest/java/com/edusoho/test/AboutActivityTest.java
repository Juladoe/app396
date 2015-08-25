package com.edusoho.test;

import android.test.UiThreadTest;
import android.widget.TextView;

import com.edusoho.kuozhi.v3.ui.AboutActivity;
import com.edusoho.kuozhi.v3.view.EduUpdateView;
import com.edusoho.test.base.BaseActivityUnitTestCase;

/**
 * Created by JesseHuang on 15/8/24.
 */
public class AboutActivityTest extends BaseActivityUnitTestCase<AboutActivity> {
    public AboutActivityTest() {
        super(AboutActivity.class);
    }

    @UiThreadTest
    public void testAboutActivity() {
        AboutActivity mActivity = getActivity();
        assertNotNull(mActivity);
    }

    @UiThreadTest
    public void testAboutActivityLayout() {
        AboutActivity mActivity = getActivity();
        TextView tvAboutSchool = (TextView) mActivity.findViewById(R.id.tv_about_school);
        assertNotNull(tvAboutSchool);
        TextView tvFeedback = (TextView) mActivity.findViewById(R.id.tv_feedback);
        assertNotNull(tvFeedback);
        EduUpdateView tvCheckUpdate = (EduUpdateView) mActivity.findViewById(R.id.tv_check_update);
        assertNotNull(tvCheckUpdate);
        assertEquals("版本更新 " + mActivity.getResources().getString(R.string.apk_version), tvCheckUpdate.getText().toString());

    }

}
