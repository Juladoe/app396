package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howzhi on 14-10-20.
 */
public abstract class RecyclerViewListBaseAdapter<T, E extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<E> {

    protected LayoutInflater inflater;
    protected int mResource;
    protected Context mContext;
    protected ArrayList<T> mList;
    private RecyclerItemClick mRecyclerItemClick;

    public RecyclerViewListBaseAdapter(Context context, int resource)
    {
        mResource = resource;
        mContext = context;
        mList = new ArrayList<T>();
        inflater = LayoutInflater.from(mContext);
    }

    public void clear()
    {
        mList.clear();
    }

    public void addItem(T item){}

    public abstract void addItems(List<T> list);

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(E e, int i) {
        final int index = i;
        if (mRecyclerItemClick != null) {
            e.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecyclerItemClick.onItemClick(mList.get(index), index);
                }
            });
        }
    }

    public void setOnItemClick(RecyclerItemClick recyclerItemClick)
    {
        this.mRecyclerItemClick = recyclerItemClick;

    }

    public static abstract class RecyclerItemClick
    {
        public abstract void onItemClick(Object obj, int position);
    }
}
