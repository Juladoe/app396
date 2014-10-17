package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import com.edusoho.listener.NormalCallback;
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
    private int mCourseId;
    private ActionBarBaseActivity mActivity;
    private ReviewListAdapter mAdapter;
    private boolean isInitHeight;
    private NormalCallback normalCallback;
    private int mLimit;

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
            mContentView.setMode(PullToRefreshBase.Mode.BOTH);
            mContentView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    getReviews(0, mCourseId, false);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                }
            });
            return;
        }
    }

    public void getReviews(int start, int courseId, final boolean isAppend)
    {
        RequestUrl url = mActivity.app.bindUrl(Const.REVIEWS, true);
        url.setParams(new String[] {
                "courseId", courseId + "",
                "start", start + "",
                "limit", mLimit + ""
        });

        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mLoadView.setVisibility(View.GONE);
                mContentView.onRefreshComplete();
                ReviewResult reviewResult = mActivity.parseJsonValue(
                        object, new TypeToken<ReviewResult>(){});

                if (reviewResult == null || reviewResult.total == 0) {
                    showEmptyLayout();
                    return;
                }

                int nextStart = reviewResult.start + Const.LIMIT;
                if (nextStart < reviewResult.total) {
                    mContentView.setTag(nextStart);
                } else {
                    mContentView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }

                if (isAppend) {
                    mAdapter.addItem(reviewResult.data);
                } else {
                    mAdapter.setData(reviewResult.data);
                }
                Log.d(null, "isInitHeight->" + isInitHeight);
                if (isInitHeight) {
                    initListHeight(mContentView.getRefreshableView());
                }
            }
        });
    }

    @Override
    protected View initEmptyLayout() {
        TextView textView = (TextView) super.initEmptyLayout();
        textView.setText(R.string.course_no_review);
        return textView;
    }

    private void initListHeight(ListView listView)
    {
        int totalHeight = 0;

        ListAdapter adapter = listView.getAdapter();
        int count = adapter.getCount();
        for (int i=0; i < count; i++) {
            View child = adapter.getView(i, null, listView);
            child.measure(0, 0);
            totalHeight += child.getMeasuredHeight() + listView.getDividerHeight();
        }

        ViewGroup.LayoutParams lp = mContentView.getLayoutParams();
        lp.height = totalHeight;
        mContentView.setLayoutParams(lp);
    }

    public void initReview(
            int courseId, ActionBarBaseActivity actionBarBaseActivity, boolean isRefresh)
    {
        mCourseId = courseId;
        mActivity = actionBarBaseActivity;
        mAdapter = new ReviewListAdapter(
                mContext, null, R.layout.course_details_review_item);
        mContentView.setAdapter(mAdapter);
        setRefresh(isRefresh);
        mLimit = isRefresh ? Const.LIMIT : 2;

        getReviews(0, mCourseId, false);
    }

    public void setCompledListener(NormalCallback compledListener)
    {
        this.normalCallback = compledListener;
    }

    public void reload()
    {
        mContentView.setRefreshing();
    }
}
