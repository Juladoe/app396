package com.edusoho.kuozhi.ui.fragment.course;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.CourseReviewAdapter;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.adapter.review.ReviewEmptyAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.CourseDetailsResult;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.ReviewResult;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.course.CoursePaperActivity;
import com.edusoho.kuozhi.ui.fragment.ReviewInfoFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.DividerItemDecoration;
import com.edusoho.kuozhi.view.ESTextView;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 14/12/2.
 */
public class CourseReviewFragment extends ViewPagerBaseFragment {

    public static final String TAG = "CourseReviewFragment";
    private double mRating;
    private int mCourseId;
    private String mRatingNum;
    private TextView mRatingView;
    private View mReviewBtn;
    private CourseReviewAdapter mAdapter;
    private CoursePaperActivity mCoursePaperActivity;

    private View mHeadView;
    private EduSohoListView mListView;
    private RatingBar mCourseReviewRatingBar;

    public static final String RATING = "rating";
    public static final String RATING_NUM = "ratingNum";

    public static final int RELOAD_INFO = 0001;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public EduSohoListView getListView() {
        return mListView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_review_layout);
    }

    @Override
    public void invoke(WidgetMessage message) {
        int type = message.type.code;
        switch (type) {
            case RELOAD_INFO:
                Bundle bundle = message.data;
                mRating = bundle.getDouble(RATING, 0);
                mRatingNum = bundle.getString(RATING_NUM);
                mRatingView.setText(String.format("%.1f分 (%s人)", mRating, mRatingNum));
                mCourseReviewRatingBar.setRating((float) mRating);
                return;
        }
        String messageType = message.type.type;
        if (Const.REFRESH_REVIEWS.equals(messageType)) {
            reloadData();
            app.sendMsgToTarget(CoursePaperActivity.RELOAD_REVIEW_INFO, null, CoursePaperActivity.class);
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(Const.REFRESH_REVIEWS),
                new MessageType(RELOAD_INFO, source)
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCoursePaperActivity = (CoursePaperActivity) activity;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        initBundle();
        mListView = (EduSohoListView) view.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(linearLayoutManager);
        mListView.addItemDecoration(
                new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new CourseReviewAdapter(
                mActivity, R.layout.course_details_review_item);
        mHeadView = initHeadView();
        mAdapter.addHeadView(mHeadView);
        mAdapter.addFooterView(initReviewMoreBtn());

        ReviewEmptyAdapter emptyAdapter = new ReviewEmptyAdapter(
                mContext, R.layout.review_empty_layout,
                new String[] { getResources().getString(R.string.course_no_review)}
        );
        emptyAdapter.setOnItemClick(new RecyclerViewListBaseAdapter.RecyclerItemClick() {
            @Override
            public void onItemClick(Object obj, int position) {
                reviewCourse();
            }
        });
        mListView.setEmptyAdapter(emptyAdapter);
        mListView.setAdapter(mAdapter);

        mRatingView.setText(String.format("%.1f分 (%s人)", mRating, mRatingNum));
        mCourseReviewRatingBar.setRating((float) mRating);
        getReviews(0, mCourseId, false);

        mReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewCourse();
            }
        });
    }

    private void reviewCourse()
    {
        if (app.loginUser == null) {
            LoginActivity.startForResult(mActivity);
            return;
        }

        CourseDetailsResult result = mCoursePaperActivity.getCourseResult();
        if (result.member == null) {
            mActivity.longToast("请加入学习");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(FragmentPageActivity.FRAGMENT, "RecommendCourseFragment");
        bundle.putString(Const.ACTIONBAR_TITLE, "评价课程");
        bundle.putInt(Const.COURSE_ID, mCourseId);
        startActivityWithBundle("FragmentPageActivity", bundle);
    }

    private View initHeadView()
    {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.course_review_head_view, null);

        mCourseReviewRatingBar = (RatingBar) view.findViewById(R.id.course_review_ratingbar);
        mRatingView = (TextView) view.findViewById(R.id.course_review_rating_view);
        mReviewBtn = view.findViewById(R.id.course_review_btn);
        return view;
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
                    mAdapter.setFooterVisible(View.GONE);
                    mListView.pushData(null);
                    return;
                }

                if (isRefresh) {
                    mListView.clear();
                    mAdapter.addHeadView(mHeadView);
                    mAdapter.addFooterView(initReviewMoreBtn());
                }

                if (reviewResult.data.size() < 5) {
                    mAdapter.setFooterVisible(View.GONE);
                }
                mListView.pushData(reviewResult.data);
            }
        });
    }

    private ESTextView initReviewMoreBtn()
    {
        ESTextView reviewBtn = new ESTextView(mContext);
        reviewBtn.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        reviewBtn.setDefaultAlpha(0.87f);
        int padding = AppUtil.dip2px(mContext, 8);
        reviewBtn.setPadding(padding, padding, padding, padding);
        reviewBtn.setGravity(Gravity.CENTER);
        reviewBtn.setText("更多评价");
        reviewBtn.setTextColor(getResources().getColor(R.color.base_color_normal));
        reviewBtn.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.base_large_size));

        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseDetailsResult result = mCoursePaperActivity.getCourseResult();
                Bundle bundle = new Bundle();
                bundle.putString(FragmentPageActivity.FRAGMENT, "ReviewInfoFragment");
                bundle.putBoolean(ReviewInfoFragment.IS_STUDENT, result.member != null);
                bundle.putString(Const.ACTIONBAR_TITLE, "评价列表");
                bundle.putInt(Const.COURSE_ID, mCourseId);
                bundle.putDouble(CourseReviewFragment.RATING, mRating);
                bundle.putString(CourseReviewFragment.RATING_NUM, mRatingNum);
                startActivityWithBundle("FragmentPageActivity", bundle);
            }
        });

        return reviewBtn;
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
