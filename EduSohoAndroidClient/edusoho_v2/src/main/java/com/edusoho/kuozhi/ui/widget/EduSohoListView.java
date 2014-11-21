package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;

import java.util.ArrayList;

/**
 * Created by howzhi on 14/11/19.
 */
public class EduSohoListView extends RecyclerView {

    private Context mContext;
    private RecyclerViewListBaseAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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

    private void init(AttributeSet attrs)

    {
        //mLayoutManager = new LinearLayoutManager(mContext);
    }

    public void initListHeight()
    {
        int totalHeight = 0;
        int count = mAdapter.getItemCount();
        for (int i=0; i < count; i++) {
            ViewHolder viewHolder = mAdapter.onCreateViewHolder(this, i);
            viewHolder.itemView.measure(0, 0);
            totalHeight += viewHolder.itemView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = totalHeight;
        setLayoutParams(lp);
    }

    public void pushData(ArrayList data)
    {
        mAdapter.addItems(data);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = (RecyclerViewListBaseAdapter) adapter;
        super.setAdapter(adapter);
    }
}
