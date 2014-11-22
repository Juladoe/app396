package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.EmptyAdapter;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.adapter.ListLoadAdapter;
import com.edusoho.kuozhi.util.Const;

import java.util.ArrayList;

import library.PullToRefreshBase;
import library.PullToRefreshListView;

/**
 * Created by howzhi on 14-8-25.
 */
public class RefreshListWidget extends PullToRefreshListView {

    public static final int UPDATE = 0001;
    public static final int REFRESH = 0002;
    public static final String TAG = "RefreshListWidget";

    private int mStart;
    private int mTotal;
    private int mLimit;

    private int mMode;
    private ListBaseAdapter mAdapter;
    private ListBaseAdapter mLoadAdapter;
    private ListBaseAdapter mEmptyAdapter;
    private UpdateListener mUpdateListener;
    private Context mContext;
    private String[] mEmptyText = new String[]{ "没有搜到相关课程，请换个关键词试试！" };

    private int mDividerHeight;
    private int mDividerColor;

    public RefreshListWidget(Context context) {
        super(context);
        mContext = context;
        initView(null);
    }

    public RefreshListWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs)
    {
        mMode = REFRESH;
        mLimit = Const.LIMIT;
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.RefreshListWidget);
        mDividerHeight = ta.getDimensionPixelSize(R.styleable.RefreshListWidget_rlw_dividerHeight, 0);
        mDividerColor = ta.getColor(R.styleable.RefreshListWidget_rlw_dividerColor, 0);
        getRefreshableView().setDividerHeight(mDividerHeight);
        //getRefreshableView().setDivider(new ColorDrawable(mDividerColor));
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

    public void setLoadAdapter()
    {
        mLoadAdapter = new ListLoadAdapter(mContext, R.layout.loading_layout);
        setAdapter(mLoadAdapter);
    }

    public void setEmptyText(String[] emptyText)
    {
        mEmptyText = emptyText;
    }

    public void pushData(ArrayList data)
    {
        if (mMode == REFRESH) {
            if (data == null || data.isEmpty()) {
                mEmptyAdapter = getEmptyLayoutAdapter();
                setAdapter(mEmptyAdapter);
                return;
            }
            if (mEmptyAdapter != null) {
                mEmptyAdapter = null;
                setAdapter(mAdapter);
            }
            if (mLoadAdapter != null) {
                mLoadAdapter = null;
                setAdapter(mAdapter);
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
                mEmptyAdapter = getEmptyLayoutAdapter();
                setAdapter(mEmptyAdapter);
                return;
            }
            if (mEmptyAdapter != null) {
                mEmptyAdapter = null;
                setAdapter(mAdapter);
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
        setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mMode = REFRESH;
                Log.d(TAG, "refresh->");
                mUpdateListener.refresh(refreshView);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mMode = UPDATE;
                Log.d(TAG, "update->");
                mUpdateListener.update(refreshView);
            }
        });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        Log.d(null, "EmptyAdapter--->" + adapter);
        if (adapter instanceof EmptyAdapter
                || adapter instanceof ListLoadAdapter) {
            return;
        }
        mAdapter = (ListBaseAdapter) adapter;
    }

    public interface UpdateListener
    {
        public void update(PullToRefreshBase<ListView> refreshView);
        public void refresh(PullToRefreshBase<ListView> refreshView);
    }
}
