package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerEmptyAdapter;
import com.edusoho.kuozhi.adapter.RecyclerLoadAdapter;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howzhi on 14/11/19.
 */
public class EduSohoListView extends RecyclerView {

    private Context mContext;
    private RecyclerViewListBaseAdapter mAdapter;
    private RecyclerViewListBaseAdapter mDataAdapter;
    private RecyclerViewListBaseAdapter mEmptyAdapter;
    private RecyclerViewListBaseAdapter mLoadingAdapter;

    private String[] mEmptyStrs = { "暂无推荐课程" };
    private int mEmptyIcon = R.drawable.course_empty_icon;

    private boolean mIsSetHeight;
    private int mFixHeight = 0;

    public EduSohoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public EduSohoListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        //addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
    }

    public void addItemDecoration()
    {
        addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
    }

    public void setFixHeight(int fixHeight)
    {
        this.mFixHeight = fixHeight;
    }

    public void initListHeight()
    {
        int totalHeight = 0;
        int count = mAdapter.getItemCount();
        for (int i=0; i < count; i++) {
            ViewHolder viewHolder = mAdapter.onCreateViewHolder(this, i);
            viewHolder.itemView.measure(0, 0);
            totalHeight += viewHolder.itemView.getMeasuredHeight();
            Log.d(null, "item height->" + viewHolder.itemView.getMeasuredHeight());
        }
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = totalHeight;
        setLayoutParams(lp);
    }

    public void setIsSetHeight(boolean isSetHeight)
    {
        this.mIsSetHeight = isSetHeight;
    }

    public void setLoadAdapter()
    {
        mLoadingAdapter = new RecyclerLoadAdapter(mContext, R.layout.loading_layout);
        setAdapter(mLoadingAdapter);
    }

    public void setEmptyString(String[] emptyString)
    {
        this.mEmptyStrs = emptyString;
    }

    public void setEmptyString(String[] emptyString, int icon)
    {
        this.mEmptyStrs = emptyString;
        this.mEmptyIcon = icon;
    }

    public RecyclerViewListBaseAdapter getEmptyAdapter()
    {
        mEmptyAdapter = new RecyclerEmptyAdapter(
                mContext, R.layout.course_empty_layout, mEmptyStrs, mEmptyIcon);
        return mEmptyAdapter;
    }

    public void clear()
    {
        mDataAdapter.clear();
    }

    public void pushData(List data)
    {
        if (data == null || data.isEmpty()) {
            mEmptyAdapter = getEmptyAdapter();
            setAdapter(mEmptyAdapter);
            return;
        }

        mDataAdapter.addItems(data);
        if (mAdapter == mDataAdapter) {
            return;
        }
        setAdapter(mDataAdapter);
    }

    public void pushItem(Object obj)
    {
        mDataAdapter.addItem(obj);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (RecyclerViewListBaseAdapter) adapter;
        if (mIsSetHeight) {
            initListHeight();
        }
        if (adapter instanceof RecyclerEmptyAdapter
                || adapter instanceof RecyclerLoadAdapter) {
            return;
        }
        mDataAdapter = (RecyclerViewListBaseAdapter) adapter;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mFixHeight > 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        measureChildren(widthMeasureSpec, expandSpec);

        View v = getChildAt(getChildCount() - 1);
        if (v != null) {
            Log.d(null, "v->" + v.getHeight());
            expandSpec = MeasureSpec.makeMeasureSpec(
                    getChildTotalHeight(), MeasureSpec.AT_MOST);
        }

        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    private int getChildTotalHeight()
    {
        int total = 0;
        int count = getChildCount();
        for (int i=0; i < count; i++) {
            View v = getChildAt(i);
            total += v.getHeight();
        }

        return total;
    }
}
