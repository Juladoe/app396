package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshFragment;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.huewu.pla.lib.internal.PLA_AdapterView;

import me.maxwin.view.XListView;

/**
 * Created by howzhi on 14-9-16.
 */
public class XCourseListWidget extends FrameLayout {

    private Context mContext;
    private ListAdapter mAdapter;
    private PullToRefreshScrollView mEmptyLayout;
    private PullToRefreshGridView mCourseListWidget;
    private String mEmptyText = "没有搜到相关课程，请换个关键词试试！";

    public XCourseListWidget(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public XCourseListWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public PullToRefreshGridView getListView()
    {
        return mCourseListWidget;
    }

    private void initView()
    {
        mCourseListWidget = new PullToRefreshGridView(mContext);
        GridView gridView = mCourseListWidget.getRefreshableView();
        gridView.setSmoothScrollbarEnabled(false);
        gridView.setNumColumns(2);
        gridView.setBackgroundColor(Color.TRANSPARENT);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        mCourseListWidget.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mCourseListWidget);
    }

    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(new XCourseObserver());
        mCourseListWidget.setAdapter(mAdapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener)
    {
        mCourseListWidget.setOnItemClickListener(itemClickListener);
    }

    public void setEmptyText(String emptyText)
    {
        mEmptyText = emptyText;
    }

    private PullToRefreshScrollView initEmptyLayout()
    {
        PullToRefreshScrollView scrollView = new PullToRefreshScrollView(mContext);
        scrollView.getRefreshableView().setFillViewport(true);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        View emptyLayout = LayoutInflater.from(mContext).inflate(
                R.layout.course_empty_layout, null);
        emptyLayout.setLayoutParams(new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT));
        scrollView.addView(emptyLayout);
        TextView textView = (TextView) emptyLayout.findViewById(R.id.list_empty_text);
        textView.setText(mEmptyText);

        return scrollView;
    }

    public void setRefreshListener(PullToRefreshBase.OnRefreshListener2 refreshListener)
    {
        if (mEmptyLayout == null) {
            mCourseListWidget.setOnRefreshListener(refreshListener);
        } else {
            mEmptyLayout.setOnRefreshListener(refreshListener);
        }
    }

    private void refreshView()
    {
        if (mAdapter.isEmpty()) {
            if (mEmptyLayout == null) {
                mEmptyLayout = initEmptyLayout();
                addView(mEmptyLayout);
            }
            mEmptyLayout.setVisibility(VISIBLE);
        } else {
            if (mEmptyLayout != null) {
                mEmptyLayout.setVisibility(GONE);
            }
            mCourseListWidget.postInvalidate();
        }
    }

    public void reload()
    {
        mCourseListWidget.setRefreshing();
    }

    public ListAdapter getAdapter()
    {
        return mAdapter;
    }

    public class XCourseObserver extends DataSetObserver
    {
        @Override
        public void onInvalidated() {
            super.onInvalidated();
            Log.d(null, "XCourseObserver->onInvalidated");
            refreshView();
        }

        @Override
        public void onChanged() {
            super.onChanged();
            Log.d(null, "XCourseObserver->onChanged");
            refreshView();
        }
    }

    public void onRefreshComplete()
    {
        mCourseListWidget.onRefreshComplete();
    }

    public void setMode(PullToRefreshBase.Mode mode)
    {
        mCourseListWidget.setMode(mode);
    }
}
