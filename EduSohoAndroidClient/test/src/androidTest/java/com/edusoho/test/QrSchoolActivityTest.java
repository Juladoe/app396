package com.edusoho.test;

import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.TextView;

import com.edusoho.kuozhi.v3.ui.QrSchoolActivity;
import com.edusoho.test.base.BaseActivityUnitTestCase;

/**
 * Created by JesseHuang on 15/8/24.
 */
public class QrSchoolActivityTest extends BaseActivityUnitTestCase<QrSchoolActivity> {
    public QrSchoolActivityTest() {
        super(QrSchoolActivity.class);
    }

    @UiThreadTest
    public void testQrSchoolActivity() {
        QrSchoolActivity mActivity = getActivity();
        assertNotNull(mActivity);
    }

    @UiThreadTest
    public void testLayout() {
        QrSchoolActivity mActivity = getActivity();
        Button mQrSearchBtn = (Button) mActivity.findViewById(R.id.qr_search_btn);
        assertNotNull(mQrSearchBtn);
        TextView tvOther = (TextView) mActivity.findViewById(R.id.qr_other_btn);
        assertNotNull(tvOther);
    }


}
