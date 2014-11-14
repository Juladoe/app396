package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;


import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.EmptyAdapter;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.util.Const;

import java.util.ArrayList;

import library.PullToRefreshBase;
import library.PullToRefreshGridView;

/**
 * Created by howzhi on 14-8-25.
 */
public class RefreshGridViewWidget extends PullToRefreshGridView {

    public static final int UPDATE = 0001;
    public static final int REFRESH = 0002;
    public static final String TAG = "RefreshListWidget";

    private int mStart;
    private int mTotal;
    private int mLimit;

    private int mMode;
    private ListBaseAdapter mAdapter;
    private UpdateListener mUpdateListener;
    private Context mContext;
    private String[] mEmptyText = new String[]{ "没有搜到相关课程，请换个关键词试试！" };

    public RefreshGridViewWidget(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public RefreshGridViewWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView()
    {
        mStart = 0;
        mMode = REFRESH;
        mLimit = Const.LIMIT;
    }

    public void setStart(int start, int total)
    {
        mTotal = total;
        start = start + mLimit;
        if (start < total) {
            this.mStart = start;
            setMode(Mode.BOTH);
        } else {
            setMode(Mode.PULL_FROM_START);
        }
    }

    public void setStart(int start)
    {
        mStart = start;
    }

    public void setEmptyText(String[] emptyText)
    {
        mEmptyText = emptyText;
    }

    public void pushData(ArrayList data)
    {
        if (mMode == REFRESH) {
            if (data == null || data.isEmpty()) {
                mAdapter = getEmptyLayoutAdapter();
                getRefreshableView().setNumColumns(1);
                setAdapter(mAdapter);
                return;
            }
            mAdapter.clear();
        }

        setMode(data.isEmpty() ? Mode.PULL_FROM_START : Mode.BOTH);
        mAdapter.addItems(data);
    }

    public void pushItem(Object item, boolean isEmpty)
    {
        if (mMode == REFRESH) {
            if (item == null || isEmpty) {
                setAdapter(getEmptyLayoutAdapter());
                return;
            }
            mAdapter.clear();
        }
        mAdapter.addItem(item);
    }

    public int getStart()
    {
        return mStart;
    }

    protected ListBaseAdapter getEmptyLayoutAdapter()
    {
        EmptyAdapter<String> arrayAdapter = new EmptyAdapter<String> (
                mContext, R.layout.course_empty_layout, mEmptyText);
        return arrayAdapter;
    }


    public ListAdapter getAdapter()
    {
        return mAdapter;
    }

    public void setUpdateListener(UpdateListener updateListener)
    {
        mUpdateListener = updateListener;
        setOnRefreshListener(new OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                mMode = REFRESH;
                Log.d(TAG, "refresh->");
                if (getAdapter() instanceof EmptyAdapter) {
                    setAdapter(mAdapter);
                }
                mUpdateListener.refresh(refreshView);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                mMode = UPDATE;
                Log.d(TAG, "update->");
                mUpdateListener.update(refreshView);
            }
        });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (ListBaseAdapter) adapter;
    }

    public interface UpdateListener
    {
        public void update(PullToRefreshBase<GridView> refreshView);
        public void refresh(PullToRefreshBase<GridView> refreshView);
    }
}
