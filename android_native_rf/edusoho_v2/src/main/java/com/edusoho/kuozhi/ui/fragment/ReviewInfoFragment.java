package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.widget.CourseDetailsReviewWidget;
import com.edusoho.kuozhi.util.Const;

import java.text.DecimalFormat;

/**
 * Created by howzhi on 14-8-31.
 */
public class ReviewInfoFragment extends BaseFragment {

    private CourseDetailsReviewWidget mReviewWidget;
    public static final String COURSE_ID = "course_id";
    public static final String COURSE = "course";
    public static final int REFRESH_REVIEWS = 0001;

    private Course mCourse;
    private TextView mCourseTitleView;
    private RatingBar mCourseRatingBar;
    private TextView mCourseStudentView;
    private Button mCommitBtn;

    @Override
    public String getTitle() {
        return "评论";
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        int type = message.type.code;
        switch (type) {
            case REFRESH_REVIEWS:
                mReviewWidget.reload();
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(REFRESH_REVIEWS, source)
        };
        return messageTypes;
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
        int courseId = bundle.getInt(COURSE_ID, 0);

        setFragmentData();
        mReviewWidget.hideTitle();
        mReviewWidget.initReview(courseId, mActivity, true);

        mCommitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecommendCourseFragment recommendCourseFragment = new RecommendCourseFragment();
                Bundle fragmentData = new Bundle();
                fragmentData.putInt(Const.COURSE_ID, mCourse.id);
                recommendCourseFragment.setArguments(fragmentData);
                recommendCourseFragment.show(getChildFragmentManager(), "dialog");
            }
        });
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
