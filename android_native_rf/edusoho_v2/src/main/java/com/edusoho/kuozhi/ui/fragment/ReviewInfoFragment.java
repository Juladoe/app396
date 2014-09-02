package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.widget.CourseDetailsReviewWidget;

/**
 * Created by howzhi on 14-8-31.
 */
public class ReviewInfoFragment extends BaseFragment {

    private CourseDetailsReviewWidget mReviewWidget;
    public static final String COURSE_ID = "course_id";
    @Override
    public String getTitle() {
        return "教师";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.review_fragment);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mReviewWidget = (CourseDetailsReviewWidget) view.findViewById(R.id.review_course_reviewlist);
        Bundle bundle = getArguments();
        if (bundle == null) {
            mActivity.longToast("无效课程信息！");
            return;
        }
        String courseId = bundle.getString(COURSE_ID);
        mReviewWidget.initReview(courseId, mActivity, true);
    }
}
