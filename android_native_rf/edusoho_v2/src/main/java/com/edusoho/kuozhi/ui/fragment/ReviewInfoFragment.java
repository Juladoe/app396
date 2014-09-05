package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.ui.widget.CourseDetailsReviewWidget;

import java.text.DecimalFormat;

/**
 * Created by howzhi on 14-8-31.
 */
public class ReviewInfoFragment extends BaseFragment {

    private CourseDetailsReviewWidget mReviewWidget;
    public static final String COURSE_ID = "course_id";
    public static final String COURSE = "course";

    private Course mCourse;
    private TextView mCourseTitleView;
    private RatingBar mCourseRatingBar;
    private TextView mCourseStudentView;
    private Button mCommitBtn;

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

        mCommitBtn = (Button) view.findViewById(R.id.review_course_commit_btn);
        mCourseTitleView = (TextView) view.findViewById(R.id.review_course_title);
        mCourseRatingBar = (RatingBar) view.findViewById(R.id.course_details_rating);
        mCourseStudentView = (TextView) view.findViewById(R.id.review_course_student);
        mReviewWidget = (CourseDetailsReviewWidget) view.findViewById(R.id.review_course_reviewlist);
        Bundle bundle = getArguments();
        if (bundle == null) {
            mActivity.longToast("无效课程信息！");
            return;
        }

        mCourse = (Course) bundle.getSerializable(COURSE);
        String courseId = bundle.getString(COURSE_ID);

        setFragmentData();
        mReviewWidget.hideTitle();
        mReviewWidget.initReview(courseId, mActivity, true);
    }

    private void setCommitStatus()
    {
        if (app.loginUser == null) {
            mCommitBtn.setVisibility(View.GONE);
        } else {
            mCommitBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setFragmentData()
    {
        setCommitStatus();
        mCourseTitleView.setText(mCourse.title);
        mCourseRatingBar.setRating((float) mCourse.rating);

        String rating = null;
        if (mCourse.rating == 0) {
            rating = "0";
        } else {
            DecimalFormat format = new DecimalFormat("#.0");
            rating = format.format(mCourse.rating);
        }
        mCourseStudentView.setText(String.format(
                "%s分 (%s人)", rating, mCourse.studentNum));
    }
}
