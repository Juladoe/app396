package com.edusoho.kuozhi.ui.fragment.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.CourseReviewAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.ReviewResult;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.DividerItemDecoration;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 14/12/2.
 */
public class CourseReviewFragment extends BaseFragment {

    public static final String TAG = "CourseReviewFragment";
    private double mRating;
    private int mCourseId;
    private String mRatingNum;
    private TextView mRatingView;
    private View mReviewBtn;
    private View mReviewMoreBtn;
    private EduSohoListView mListView;
    private RatingBar mCourseReviewRatingBar;

    public static final String RATING = "rating";
    public static final String RATING_NUM = "ratingNum";

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_review_layout);
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        String type = message.type.type;
        if (Const.REFRESH_REVIEWS.equals(type)) {
            reloadData();
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(Const.REFRESH_REVIEWS)
        };
        return messageTypes;
    }

    private void initBundle()
    {
        Bundle bundle = getArguments();
        mCourseId = bundle.getInt(Const.COURSE_ID, 0);
        mRating = bundle.getDouble(RATING, 0);
        mRatingNum = bundle.getString(RATING_NUM);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        initBundle();
        mReviewMoreBtn = view.findViewById(R.id.course_review_moreBtn);
        mCourseReviewRatingBar = (RatingBar) view.findViewById(R.id.course_review_ratingbar);
        mRatingView = (TextView) view.findViewById(R.id.course_review_rating_view);
        mListView = (EduSohoListView) view.findViewById(R.id.list_view);
        mReviewBtn = view.findViewById(R.id.course_review_btn);

        mReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(FragmentPageActivity.FRAGMENT, "RecommendCourseFragment");
                bundle.putString(Const.ACTIONBAT_TITLE, "评价课程");
                bundle.putInt(Const.COURSE_ID, mCourseId);
                startAcitivityWithBundle("FragmentPageActivity", bundle);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(linearLayoutManager);
        mListView.addItemDecoration(
                new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL_LIST));

        CourseReviewAdapter mAdapter = new CourseReviewAdapter(
                mActivity, R.layout.course_details_review_item);
        mListView.setAdapter(mAdapter);

        mRatingView.setText(String.format("%.1f分 (%s人)", mRating, mRatingNum));
        mCourseReviewRatingBar.setRating((float) mRating);
        getReviews(0, mCourseId, false);
        mReviewMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(FragmentPageActivity.FRAGMENT, "ReviewInfoFragment");
                bundle.putString(Const.ACTIONBAT_TITLE, "评价列表");
                bundle.putInt(Const.COURSE_ID, mCourseId);
                bundle.putDouble(CourseReviewFragment.RATING, mRating);
                bundle.putString(CourseReviewFragment.RATING_NUM, mRatingNum);
                startAcitivityWithBundle("FragmentPageActivity", bundle);
            }
        });
    }

    public void getReviews(int start, int courseId, final boolean isRefresh)
    {
        RequestUrl url = mActivity.app.bindUrl(Const.REVIEWS, true);
        url.setParams(new String[]{
                Const.COURSE_ID, String.valueOf(courseId),
                "start", String.valueOf(start),
                "limit", String.valueOf(5)
        });

        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                ReviewResult reviewResult = mActivity.parseJsonValue(
                        object, new TypeToken<ReviewResult>(){});

                if (reviewResult == null || reviewResult.total == 0) {
                    mListView.pushData(null);
                    mReviewMoreBtn.setVisibility(View.GONE);
                    return;
                }

                if (reviewResult.data.size() < 5) {
                    mReviewMoreBtn.setVisibility(View.GONE);
                }
                if (isRefresh) {
                    mListView.clear();
                }
                mListView.pushData(reviewResult.data);
            }
        });
    }

    private void reloadData()
    {
        getReviews(0, mCourseId, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
    }
}
