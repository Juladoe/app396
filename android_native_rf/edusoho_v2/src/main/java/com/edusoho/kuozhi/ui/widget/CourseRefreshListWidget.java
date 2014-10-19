package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.EmptyAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by howzhi on 14-8-25.
 */
public class CourseRefreshListWidget extends PullToRefreshListView {

    private ListAdapter mAdapter;
    private Context mContext;
    private String[] mEmptyText = new String[]{ "没有搜到相关课程，请换个关键词试试！" };

    public CourseRefreshListWidget(Context context) {
        super(context);
        mContext = context;
    }

    public CourseRefreshListWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (adapter.isEmpty()) {
            adapter = getEmptyLayoutAdapter();
        }
        super.setAdapter(adapter);
        mAdapter = adapter;
    }

    public void setEmptyText(String[] emptyText)
    {
        mEmptyText = emptyText;
    }

    protected ListAdapter getEmptyLayoutAdapter()
    {
        EmptyAdapter arrayAdapter = new EmptyAdapter(
                mContext, R.layout.course_empty_layout, R.id.list_empty_text, mEmptyText);
        return arrayAdapter;
    }

    protected void setErrorLayout(){
    }

    public ListAdapter getAdapter()
    {
        return mAdapter;
    }
}
