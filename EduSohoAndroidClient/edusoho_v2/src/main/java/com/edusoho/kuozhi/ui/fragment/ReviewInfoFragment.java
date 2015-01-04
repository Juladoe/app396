package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.widget.CourseDetailsReviewWidget;
import com.edusoho.kuozhi.util.Const;

/**
 * Created by howzhi on 14-8-31.
 */
public class ReviewInfoFragment extends BaseFragment {

    private CourseDetailsReviewWidget mReviewWidget;
    public static final String RATING = "rating";
    public static final String RATING_NUM = "ratingNum";

    public static final String IS_STUDENT = "is_student";

    private double mRating;
    private int mCourseId;
    private boolean mIsStudent;
    private String mRatingNum;
    private TextView mRatingView;
    private View mReviewBtn;
    private RatingBar mCourseReviewRatingBar;

    @Override
    public String getTitle() {
        return "评价";
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        String type = message.type.type;
        if (Const.REFRESH_REVIEWS.equals(type)) {
            mReviewWidget.reload();
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(Const.REFRESH_REVIEWS)
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

        mCourseReviewRatingBar = (RatingBar) view.findViewById(R.id.course_review_ratingbar);
        mRatingView = (TextView) view.findViewById(R.id.course_review_rating_view);
        mReviewBtn = view.findViewById(R.id.course_review_btn);
        mReviewWidget = (CourseDetailsReviewWidget) view.findViewById(R.id.review_course_reviewlist);
        Bundle bundle = getArguments();
        if (bundle == null) {
            mActivity.longToast("无效课程信息！");
            return;
        }

        mIsStudent = bundle.getBoolean(IS_STUDENT, false);
        mCourseId = bundle.getInt(Const.COURSE_ID, 0);
        mRating = bundle.getDouble(RATING, 0);
        mRatingNum = bundle.getString(RATING_NUM);

        mReviewWidget.hideTitle();
        mReviewWidget.initReview(mCourseId, mActivity, true);

        if (mIsStudent) {
            mReviewBtn.setVisibility(View.VISIBLE);
        }
        mReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FragmentPageActivity.FRAGMENT, "RecommendCourseFragment");
                bundle.putString(Const.ACTIONBAT_TITLE, "评价课程");
                bundle.putInt(Const.COURSE_ID, mCourseId);
                startAcitivityWithBundle("FragmentPageActivity", bundle);
            }
        });

        mRatingView.setText(String.format("%.1f分 (%s人)", mRating, mRatingNum));
        mCourseReviewRatingBar.setRating((float) mRating);
    }
}
