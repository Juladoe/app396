package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import library.PullToRefreshBase;
import library.PullToRefreshGridView;


/**
 * Created by howzhi on 14-9-16.
 */
public class XCourseListWidget extends FrameLayout {

    private Context mContext;
    private ListAdapter mAdapter;
    private View mEmptyLayout;
    private PullToRefreshGridView mCourseListWidget;
    private String mEmptyText = "没有搜到相关课程，请换个关键词试试！";
    private int numColumn = 2;

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
        gridView.setNumColumns(numColumn);
        gridView.setBackgroundColor(Color.TRANSPARENT);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setSmoothScrollbarEnabled(true);
        gridView.setFastScrollEnabled(true);
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

    private View initEmptyLayout()
    {
        View emptyLayout = LayoutInflater.from(mContext).inflate(
                R.layout.course_empty_layout, null);
        TextView textView = (TextView) emptyLayout.findViewById(R.id.list_empty_text);
        ImageView iconView = (ImageView) emptyLayout.findViewById(R.id.list_empty_icon);
        iconView.setImageResource(R.drawable.icon_course_empty);
        textView.setText(mEmptyText);

        return emptyLayout;
    }

    public void setRefreshListener(PullToRefreshBase.OnRefreshListener2 refreshListener)
    {
        if (mEmptyLayout == null) {
            mCourseListWidget.setOnRefreshListener(refreshListener);
        }
    }

    public void scrollLater()
    {
        GridView gridView = mCourseListWidget.getRefreshableView();
        int count = mAdapter.getCount();
        int mod = count % 10;
        gridView.smoothScrollToPosition(mod > 0 ? count - mod : count - 10);
    }

    private void refreshView()
    {
        if (mAdapter.isEmpty()) {
            if (mEmptyLayout == null) {
                mEmptyLayout = initEmptyLayout();
                addView(mEmptyLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
