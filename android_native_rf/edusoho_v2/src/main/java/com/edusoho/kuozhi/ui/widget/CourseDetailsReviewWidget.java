package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ReviewListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Review;
import com.edusoho.kuozhi.model.ReviewResult;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-8-27.
 */
public class CourseDetailsReviewWidget extends CourseDetailsLabelWidget {

    private PullToRefreshListView mContentView;
    private AQuery mAQuery;
    private String mCourseId;
    private ActionBarBaseActivity mActivity;
    private ReviewListAdapter mAdapter;
    private boolean isInitHeight;

    public CourseDetailsReviewWidget(Context context) {
        super(context);
    }

    public CourseDetailsReviewWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(AttributeSet attrs) {
        super.initView(attrs);
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CourseDetailsLabelWidget);
        isInitHeight = ta.getBoolean(R.styleable.CourseDetailsLabelWidget_isInitHeight, false);

        mContainer.setPadding(0, -2, 0, 0);
        mContentView = new PullToRefreshListView(mContext);
        mContentView.setFocusable(false);
        mContentView.setFocusableInTouchMode(false);
        mContentView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mContentView.setMode(PullToRefreshBase.Mode.DISABLED);
        mContentView.getRefreshableView().setDividerHeight(1);
        mContentView.getRefreshableView().setSelector(new ColorDrawable(0));

        mAQuery = new AQuery(mContentView);
        setLoadView();
        setContentView(mContentView);
    }

    public void setRefresh(boolean isRefresh)
    {
        if (isRefresh) {
            mContentView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
            mContentView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                }
            });
            return;
        }

        setShowMoreBtn(new OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    public void getReviews(int start, String courseId)
    {
        RequestUrl url = mActivity.app.bindUrl(Const.REVIEWS, true);
        url.setParams(new String[] {
                "courseId", courseId,
                "start", start + ""
        });

        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mLoadView.setVisibility(View.GONE);
                mContentView.onRefreshComplete();
                ReviewResult reviewResult = mActivity.parseJsonValue(
                        object, new TypeToken<ReviewResult>(){});

                if (reviewResult == null) {
                    return;
                }

                int nextStart = reviewResult.start + Const.LIMIT;
                if (nextStart < reviewResult.total) {
                    mContentView.setTag(nextStart);
                } else {
                    mContentView.setMode(PullToRefreshBase.Mode.DISABLED);
                }

                mAdapter.addItem(reviewResult.data);
                if (isInitHeight) {
                    initListHeight(mContentView.getRefreshableView());
                }
            }
        });
    }

    private void initListHeight(ListView listView)
    {
        int totalHeight = 0;

        ListAdapter adapter = listView.getAdapter();
        int count = adapter.getCount();
        for (int i=0; i < count; i++) {
            View child = adapter.getView(i, null, this);
            child.measure(0, 0);
            totalHeight += child.getMeasuredHeight() + listView.getDividerHeight();
        }

        ViewGroup.LayoutParams lp = mContentView.getLayoutParams();
        lp.height = totalHeight;
        mContentView.setLayoutParams(lp);
    }

    public void initReview(
            String courseId, ActionBarBaseActivity actionBarBaseActivity, boolean isRefresh)
    {
        mCourseId = courseId;
        mActivity = actionBarBaseActivity;
        mAdapter = new ReviewListAdapter(
                mContext, null, R.layout.course_details_review_item);
        mContentView.setAdapter(mAdapter);
        setRefresh(isRefresh);
        getReviews(0, mCourseId);
    }

    @Override
    public void onShow() {
    }
}
